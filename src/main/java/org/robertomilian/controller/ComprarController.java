package org.robertomilian.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.robertomilian.database.Conexion;
import org.robertomilian.model.DetalleCompra;
import org.robertomilian.model.Producto;
import org.robertomilian.system.Main;

public class ComprarController extends TablaGuitarrasController implements Initializable {

    private TablaDetalleCompraController detalleCompraController;

    private Main principal;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @FXML
    private TableView<DetalleCompra> tablaDetalleTemporal;
    @FXML
    private TableColumn colIdDetalleOrden, colIdOrden, colIdProducto, colCantidad, colPrecioUnitario;

    private ObservableList<DetalleCompra> listaDetalleCompra = FXCollections.observableArrayList();

    private int numeroOrdenActual;

    @FXML
    private Spinner<Integer> spCantidad;

    @FXML
    private TextField txtNitCliente;

    public void escenaMenuPrincipal() {
        principal.menuPrincipal();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.inicializarProductos();
        configurarColumnasCarrito();

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1);
        spCantidad.setValueFactory(valueFactory);

        tablaProductos.setOnMouseClicked(e -> {
            if (tipoOperacion == Operacion.NINGUNA) {
                Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
                if (productoSeleccionado != null) {
                    int valorActualSpinner = spCantidad.getValue();

                    SpinnerValueFactory<Integer> newFactory
                            = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, productoSeleccionado.getStock(), valorActualSpinner);

                    spCantidad.setValueFactory(newFactory);

                    if (valorActualSpinner > productoSeleccionado.getStock()) {
                        spCantidad.getValueFactory().setValue(1);
                    }
                }
            }
        });
    }

    private void configurarColumnasCarrito() {
        colIdDetalleOrden.setCellValueFactory(new PropertyValueFactory<>("idDetalleOrden"));
        colIdOrden.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        tablaDetalleTemporal.setItems(listaDetalleCompra);
    }

    public void iniciarNuevaCompra(int idNuevaOrden) {
        this.numeroOrdenActual = idNuevaOrden;
        limpiarCarrito();
        System.out.println("ComprarController: Iniciando nueva compra con ID de orden: " + idNuevaOrden);
    }

    public void limpiarCarrito() {
        listaDetalleCompra.clear();
    }

    private DetalleCompra obtenerDatos() {
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado == null) {
            System.out.println("Por favor, selecciona un producto.");
            return null;
        }

        int idDetalleOrden = 0;
        int idOrden = numeroOrdenActual;
        int idProducto = productoSeleccionado.getIdProducto();
        int cantidad = 0;

        try {
            cantidad = spCantidad.getValue();

            if (cantidad <= 0 || cantidad > productoSeleccionado.getStock()) {
                System.out.println("Cantidad inválida o excede el stock disponible. Stock: " + productoSeleccionado.getStock());
                return null;
            }
        } catch (ClassCastException e) {
            System.out.println("Error: El valor del Spinner no es un número válido. " + e.getMessage());
            return null;
        } catch (NullPointerException e) {
            System.out.println("Error: El Spinner de cantidad no está inicializado. " + e.getMessage());
            return null;
        }

        BigDecimal precio = productoSeleccionado.getPrecio();

        return new DetalleCompra(
                idDetalleOrden,
                idOrden,
                idProducto,
                cantidad,
                precio);
    }

    public void agregarDetalle() {
        DetalleCompra detalleCompra = obtenerDatos();
        if (detalleCompra == null) {
            return;
        }

        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                .prepareCall("call sp_agregarDetalleCompra(?,?,?,?,?);")) {
            cs.setInt(1, detalleCompra.getIdOrden());
            cs.setInt(2, detalleCompra.getIdProducto());
            cs.setInt(3, detalleCompra.getCantidad());
            cs.setBigDecimal(4, detalleCompra.getPrecioUnitario());
            cs.registerOutParameter(5, java.sql.Types.INTEGER);

            cs.execute();

            int idGenerado = cs.getInt(5);
            detalleCompra.setIdDetalleOrden(idGenerado);

            listaDetalleCompra.add(detalleCompra);
            tablaDetalleTemporal.refresh();
            spCantidad.getValueFactory().setValue(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void eliminarDetalle() {
         DetalleCompra detalleSeleccionado = tablaDetalleTemporal.getSelectionModel().getSelectedItem();
         
        if(detalleSeleccionado == null) {
            System.out.println("Debe seleccionar un detalle para eliminar.");
            return;
        }
         
        int idRegistroDetalle = detalleSeleccionado.getIdDetalleOrden();

        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                .prepareCall("call sp_eliminarDetalleCompra(?);")) {
            cs.setInt(1, idRegistroDetalle);
            cs.execute();

            listaDetalleCompra.remove(detalleSeleccionado);
            tablaDetalleTemporal.refresh();
            spCantidad.getValueFactory().setValue(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void finalizarCompra() {
        if (listaDetalleCompra.isEmpty()) {
            System.out.println("El carrito esta vacio. No se puede finalizar la compra.");
            return;
        }

        BigDecimal totalFactura = calcularTotalFactura();
        String nitCliente = txtNitCliente.getText().trim();
        if (nitCliente.isEmpty()) {
            nitCliente = "C/F";
        }

        if (principal != null) {
            principal.mostrarVistaFactura(listaDetalleCompra, totalFactura, numeroOrdenActual, nitCliente);
        } else {
            System.out.println("Error: No se ha establecido la referencia al Main.");
        }
    }

    private BigDecimal calcularTotalFactura() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCompra detalle : listaDetalleCompra) {
            total = total.add(detalle.getSubtotal());
        }
        return total;
    }
}
