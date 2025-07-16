package org.robertomilian.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.robertomilian.database.Conexion;
import org.robertomilian.model.DetalleCompra;
import org.robertomilian.model.Producto;
import org.robertomilian.system.Main;

/**
 * FXML Controller class
 *
 * @author Roberto
 */
public class ComprarController extends TablaGuitarrasController implements Initializable{
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
    private TextField txtCantidad;
    
    public void escenaMenuPrincipal() {
        principal.menuPrincipal();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.inicializarProductos(); 
        
        configurarColumnasCarrito();
        
        tablaProductos.setOnMouseClicked(e -> {
            if (tipoOperacion == Operacion.NINGUNA) {
                // Lógica al seleccionar un producto de la tabla de productos disponibles
            }
        });
    }   
    
    private void configurarColumnasCarrito() {
        colIdDetalleOrden.setCellValueFactory(new PropertyValueFactory<>("idDetalleOrden"));
        colIdOrden.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
    }
    
    public void iniciarNuevaCompra(int idNuevaOrden) {
        this.numeroOrdenActual = idNuevaOrden;
        limpiarCarrito(); 
        System.out.println("ComprarController: Iniciando nueva compra con ID de orden: " + idNuevaOrden);
    }
    
    public void limpiarCarrito() {
        listaDetalleCompra.clear();
        tablaDetalleTemporal.setItems(listaDetalleCompra);
        System.out.println("ComprarController: Carrito limpiado (visual y lista interna).");
    }
    
    private DetalleCompra obtenerDatos(){
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
            if (!txtCantidad.getText().isEmpty()) {
                int cantidadIngresada = Integer.parseInt(txtCantidad.getText());
                if (cantidadIngresada > 0 && cantidadIngresada <= productoSeleccionado.getStock()) {
                    cantidad = cantidadIngresada;
                } else {
                    System.out.println("Cantidad inválida o excede el stock disponible. Stock: " + productoSeleccionado.getStock());
                    return null;
                }
            } else {
                System.out.println("Por favor, ingresa una cantidad.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en Cantidad. Ingresa un número válido: " + e.getMessage());
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
    
    public void agregarDetalle(){
        DetalleCompra detalleCompra = obtenerDatos();
        if (detalleCompra == null) {
            return; 
        }

        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                          .prepareCall("call sp_agregarDetalleCompra(?,?,?,?);")) {
            cs.setInt(1, detalleCompra.getIdOrden());
            cs.setInt(2, detalleCompra.getIdProducto());
            cs.setInt(3, detalleCompra.getCantidad());
            cs.setBigDecimal(4, detalleCompra.getPrecioUnitario());
            cs.execute();
            System.out.println("Detalle de compra agregado con éxito a la orden " + detalleCompra.getIdOrden());
            
            listaDetalleCompra.add(detalleCompra);
            tablaDetalleTemporal.refresh();
            
            txtCantidad.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al agregar detalle de compra: " + e.getMessage());
        }
    }
    
    @FXML
    public void agregarProductoAlCarrito(){
        agregarDetalle();
    }
}