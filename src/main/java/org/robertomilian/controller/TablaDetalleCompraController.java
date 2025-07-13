package org.robertomilian.controller;

import java.net.URL;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.robertomilian.database.Conexion;
import org.robertomilian.model.DetalleCompra;
import org.robertomilian.model.Compras;
import org.robertomilian.model.Productos;
import org.robertomilian.system.Main;

/**
 * FXML Controller class
 *
 * @author Marcos
 */
public class TablaDetalleCompraController implements Initializable {

    private Main principal;

    @FXML private TableView<DetalleCompra> tablaDetalleCompra;
    @FXML private TableColumn colIdDetalleOrden, colIdOrden, colIdProducto, colCantidad, colPrecioUnitario;

    @FXML private TextField txtIdDetalleOrden, txtCantidad, txtPrecioUnitario, txtBuscar;
    @FXML private ComboBox<Compras> cbxOrdenes;
    @FXML private ComboBox<Productos> cbxProductos;

    @FXML private Button btnNuevo, btnEditar, btnEliminar, btnGuardar, btnCancelar;
    @FXML private Button btnSiguiente, btnAnterior, btnMenu;

    private ObservableList<DetalleCompra> listaDetalleCompra;
    private ObservableList<Compras> listaOrdenes;
    private ObservableList<Productos> listaProductos;

    private enum Operacion {NINGUNA, NUEVO, EDITAR}
    private Operacion tipoOperacion = Operacion.NINGUNA;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarOrdenes();
        cargarProductos();
        cargarDetallesCompra();
        tablaDetalleCompra.setOnMouseClicked(e -> {
            if (tipoOperacion == Operacion.NINGUNA) {
                cargarDetalleSeleccionado();
            }
        });
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> buscarDetalleCompra());
        cambiarEstadoCampos(false);
    }

    private void configurarColumnas() {
        colIdDetalleOrden.setCellValueFactory(new PropertyValueFactory<>("idDetalleOrden"));
        colIdOrden.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
    }

    private ArrayList<DetalleCompra> obtenerDetallesCompra() {
        ArrayList<DetalleCompra> lista = new ArrayList<>();
        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("call sp_listarDetalleCompra();");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                lista.add(new DetalleCompra(
                        rs.getInt("ID_DETALLE"),
                        rs.getInt("ID_ORDEN"),
                        rs.getInt("ID_PRODUCTO"),
                        rs.getInt("CANTIDAD"),
                        rs.getBigDecimal("PRECIO_UNITARIO")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener detalles de compra: " + e.getMessage());
        }
        return lista;
    }

    private void cargarDetallesCompra() {
        listaDetalleCompra = FXCollections.observableArrayList(obtenerDetallesCompra());
        tablaDetalleCompra.setItems(listaDetalleCompra);
        if (!listaDetalleCompra.isEmpty()) {
            tablaDetalleCompra.getSelectionModel().selectFirst();
            cargarDetalleSeleccionado();
        } else {
            limpiarCampos();
        }
    }

    private void cargarDetalleSeleccionado() {
        DetalleCompra dc = tablaDetalleCompra.getSelectionModel().getSelectedItem();
        if (dc != null) {
            txtIdDetalleOrden.setText(String.valueOf(dc.getIdDetalleOrden()));
            txtIdDetalleOrden.setDisable(true);
            txtCantidad.setText(String.valueOf(dc.getCantidad()));
            txtPrecioUnitario.setText(String.valueOf(dc.getPrecioUnitario()));

            for (Compras o : listaOrdenes) {
                if (o.getIdOrden() == dc.getIdOrden()) {
                    cbxOrdenes.setValue(o);
                    break;
                }
            }
            for (Productos p : listaProductos) {
                if (p.getIdProducto() == dc.getIdProducto()) {
                    cbxProductos.setValue(p);
                    break;
                }
            }
        } else {
            limpiarCampos();
        }
    }

    private ArrayList<Compras> obtenerOrdenesParaComboBox() {
        ArrayList<Compras> ordenes = new ArrayList<>();
        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("call sp_listarCompras();");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                ordenes.add(new Compras(
                        rs.getInt("ID_ORDEN"),
                        rs.getInt("ID_USUARIO"),
                        null,
                        null,
                        null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cargar órdenes para ComboBox: " + e.getMessage());
        }
        return ordenes;
    }

    private void cargarOrdenes() {
        listaOrdenes = FXCollections.observableArrayList(obtenerOrdenesParaComboBox());
        cbxOrdenes.setItems(listaOrdenes);
    }

    private ArrayList<Productos> obtenerProductosParaComboBox() {
        ArrayList<Productos> productos = new ArrayList<>();
        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("call sp_listarProductos();");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                productos.add(new Productos(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE"),
                        null,
                        null,
                        0,
                        null,
                        null,
                        null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cargar productos para ComboBox: " + e.getMessage());
        }
        return productos;
    }

    private void cargarProductos() {
        listaProductos = FXCollections.observableArrayList(obtenerProductosParaComboBox());
        cbxProductos.setItems(listaProductos);
    }

    private DetalleCompra obtenerDatosFormulario() {
        int idDetalleOrden = txtIdDetalleOrden.getText().isEmpty() ? 0 : Integer.parseInt(txtIdDetalleOrden.getText());
        
        int idOrden = 0;
        if (cbxOrdenes.getSelectionModel().getSelectedItem() != null) {
            idOrden = cbxOrdenes.getSelectionModel().getSelectedItem().getIdOrden();
        }

        int idProducto = 0;
        if (cbxProductos.getSelectionModel().getSelectedItem() != null) {
            idProducto = cbxProductos.getSelectionModel().getSelectedItem().getIdProducto();
        }

        int cantidad = 0;
        try {
            if (!txtCantidad.getText().isEmpty()) {
                cantidad = Integer.parseInt(txtCantidad.getText());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en Cantidad: " + e.getMessage());
        }

        BigDecimal precioUnitario = BigDecimal.ZERO;
        try {
            if (!txtPrecioUnitario.getText().isEmpty()) {
                precioUnitario = new BigDecimal(txtPrecioUnitario.getText());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en Precio Unitario: " + e.getMessage());
        }
        
        return new DetalleCompra(
                idDetalleOrden,
                idOrden,
                idProducto,
                cantidad,
                precioUnitario
        );
    }

    private void limpiarCampos() {
        txtIdDetalleOrden.clear();
        cbxOrdenes.getSelectionModel().clearSelection();
        cbxProductos.getSelectionModel().clearSelection();
        txtCantidad.clear();
        txtPrecioUnitario.clear();
    }

    private void cambiarEstadoCampos(boolean edicionActiva) {
        txtIdDetalleOrden.setDisable(true);
        cbxOrdenes.setDisable(!edicionActiva);
        cbxProductos.setDisable(!edicionActiva);
        txtCantidad.setDisable(!edicionActiva);
        txtPrecioUnitario.setDisable(!edicionActiva);

        btnGuardar.setDisable(!edicionActiva);
        btnCancelar.setDisable(!edicionActiva);

        btnNuevo.setDisable(edicionActiva);
        btnEditar.setDisable(edicionActiva || tablaDetalleCompra.getSelectionModel().isEmpty());
        btnEliminar.setDisable(edicionActiva || tablaDetalleCompra.getSelectionModel().isEmpty());

        btnSiguiente.setDisable(edicionActiva || tablaDetalleCompra.getItems().isEmpty());
        btnAnterior.setDisable(edicionActiva || tablaDetalleCompra.getItems().isEmpty());
        txtBuscar.setDisable(edicionActiva);
    }

    @FXML
    private void clicNuevo() {
        limpiarCampos();
        tipoOperacion = Operacion.NUEVO;
        cambiarEstadoCampos(true);
    }

    @FXML
    private void clicEditar() {
        if (tablaDetalleCompra.getSelectionModel().getSelectedItem() != null) {
            tipoOperacion = Operacion.EDITAR;
            cambiarEstadoCampos(true);
        } else {
            System.out.println("Por favor, selecciona un detalle de compra para editar.");
        }
    }

    @FXML
    private void clicEliminar() {
        DetalleCompra dc = tablaDetalleCompra.getSelectionModel().getSelectedItem();
        if (dc != null) {
            try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                                .prepareCall("call sp_eliminarDetalleCompra(?);")) {
                cs.setInt(1, dc.getIdDetalleOrden());
                cs.execute();
                cargarDetallesCompra();
                limpiarCampos();
                System.out.println("Detalle de compra eliminado con éxito.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error al eliminar detalle de compra: " + e.getMessage());
            }
        } else {
            System.out.println("Por favor, selecciona un detalle de compra para eliminar.");
        }
    }

    @FXML
    private void clicGuardar() {
        DetalleCompra detalleCompra = obtenerDatosFormulario();
        try {
            if (tipoOperacion == Operacion.NUEVO) {
                try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                                    .prepareCall("call sp_agregarDetalleCompra(?,?,?,?);")) {
                    cs.setInt(1, detalleCompra.getIdOrden());
                    cs.setInt(2, detalleCompra.getIdProducto());
                    cs.setInt(3, detalleCompra.getCantidad());
                    cs.setBigDecimal(4, detalleCompra.getPrecioUnitario());
                    cs.execute();
                    System.out.println("Detalle de compra agregado con éxito.");
                }
            } else if (tipoOperacion == Operacion.EDITAR) {
                try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                                    .prepareCall("call sp_editarDetalleCompra(?,?,?,?,?);")) {
                    cs.setInt(1, detalleCompra.getIdDetalleOrden());
                    cs.setInt(2, detalleCompra.getIdOrden());
                    cs.setInt(3, detalleCompra.getIdProducto());
                    cs.setInt(4, detalleCompra.getCantidad());
                    cs.setBigDecimal(5, detalleCompra.getPrecioUnitario());
                    cs.execute();
                    System.out.println("Detalle de compra editado con éxito.");
                }
            }
            tipoOperacion = Operacion.NINGUNA;
            cambiarEstadoCampos(false);
            cargarDetallesCompra();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al guardar detalle de compra: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en números: " + e.getMessage());
        }
    }

    @FXML
    private void clicCancelar() {
        cargarDetalleSeleccionado();
        tipoOperacion = Operacion.NINGUNA;
        cambiarEstadoCampos(false);
    }

    @FXML
    private void clicSiguiente() {
        int index = tablaDetalleCompra.getSelectionModel().getSelectedIndex();
        if (index < listaDetalleCompra.size() - 1) {
            tablaDetalleCompra.getSelectionModel().select(index + 1);
            cargarDetalleSeleccionado();
        }
    }

    @FXML
    private void clicAnterior() {
        int index = tablaDetalleCompra.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            tablaDetalleCompra.getSelectionModel().select(index - 1);
            cargarDetalleSeleccionado();
        }
    }

    @FXML
    private void buscarDetalleCompra() {
        String texto = txtBuscar.getText().toLowerCase();
        ArrayList<DetalleCompra> resultados = new ArrayList<>();
        for (DetalleCompra dc : listaDetalleCompra) {
            if (String.valueOf(dc.getIdDetalleOrden()).contains(texto) ||
                String.valueOf(dc.getIdOrden()).contains(texto) ||
                String.valueOf(dc.getIdProducto()).contains(texto) ||
                String.valueOf(dc.getCantidad()).contains(texto) ||
                String.valueOf(dc.getPrecioUnitario()).toLowerCase().contains(texto)) {
                resultados.add(dc);
            }
        }
        tablaDetalleCompra.setItems(FXCollections.observableArrayList(resultados));
        if (!resultados.isEmpty()) {
            tablaDetalleCompra.getSelectionModel().selectFirst();
            cargarDetalleSeleccionado();
        } else {
            limpiarCampos();
        }
    }

}
