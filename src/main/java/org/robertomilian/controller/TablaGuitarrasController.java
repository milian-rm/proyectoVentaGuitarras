package org.robertomilian.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.robertomilian.database.Conexion;
import org.robertomilian.model.Producto;
import org.robertomilian.system.Main;
import javafx.scene.control.DatePicker;

/**
 * FXML Controller class
 *
 * @authorrr Bradley
 */
public class TablaGuitarrasController implements Initializable {

    private Main principal;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn colId, colNombre, colDescripcion, colPrecio, colStock, colCategoria, colMarca, colFechaCreacion;

    @FXML private TextField txtId, txtNombre, txtDescripcion, txtPrecio, txtStock, txtCategoria, txtMarca, txtBuscar;
    @FXML private DatePicker dpFechaCreacion;

    @FXML private Button btnNuevo, btnEditar, btnEliminar, btnGuardar, btnCancelar;
    @FXML private Button btnSiguiente, btnAnterior, btnMenu;

    private ObservableList<Producto> listaProductos;

    private enum Operacion {NINGUNA, NUEVO, EDITAR}
    private Operacion tipoOperacion = Operacion.NINGUNA;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    public void escenaMenuPrincipal() {
        principal.menuPrincipal();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarProductos();
        tablaProductos.setOnMouseClicked(e -> {
            if (tipoOperacion == Operacion.NINGUNA) {
                cargarProductoSeleccionado();
            }
        });
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> buscarProducto());
        cambiarEstadoCampos(false);
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionProducto"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
    }

    private void cargarProductos() {
        listaProductos = FXCollections.observableArrayList(obtenerProductos());
        tablaProductos.setItems(listaProductos);
        if (!listaProductos.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
            cargarProductoSeleccionado();
        } else {
            limpiarCampos();
        }
    }

    private ArrayList<Producto> obtenerProductos() {
        ArrayList<Producto> lista = new ArrayList<>();
        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("CALL sp_listarProductos();");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("FECHA_CREACION");
                LocalDateTime fechaCreacion = (ts != null) ? ts.toLocalDateTime() : null;

                lista.add(new Producto(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("DESCRIPCION"),
                        rs.getBigDecimal("PRECIO"),
                        rs.getInt("STOCK"),
                        rs.getString("CATEGORIA"),
                        rs.getString("MARCA"),
                        fechaCreacion
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private void cargarProductoSeleccionado() {
        Producto p = tablaProductos.getSelectionModel().getSelectedItem();
        if (p != null) {
            txtId.setText(String.valueOf(p.getIdProducto()));
            txtNombre.setText(p.getNombreProducto());
            txtDescripcion.setText(p.getDescripcionProducto());
            txtPrecio.setText(p.getPrecio() != null ? p.getPrecio().toPlainString() : "");
            txtStock.setText(String.valueOf(p.getStock()));
            txtCategoria.setText(p.getCategoria());
            txtMarca.setText(p.getMarca());
            if (p.getFechaCreacion() != null) {
                dpFechaCreacion.setValue(p.getFechaCreacion().toLocalDate());
            } else {
                dpFechaCreacion.setValue(null);
            }
        } else {
            limpiarCampos();
        }
    }

    private Producto obtenerDatosFormulario() {
        int id = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());
        BigDecimal precio = txtPrecio.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(txtPrecio.getText());
        int stock = txtStock.getText().isEmpty() ? 0 : Integer.parseInt(txtStock.getText());

        LocalDateTime fechaCreacion;
        if (dpFechaCreacion.getValue() != null) {
            fechaCreacion = dpFechaCreacion.getValue().atStartOfDay();
        } else {
            fechaCreacion = null;
        }

        return new Producto(
                id,
                txtNombre.getText(),
                txtDescripcion.getText(),
                precio,
                stock,
                txtCategoria.getText(),
                txtMarca.getText(),
                fechaCreacion
        );
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtCategoria.clear();
        txtMarca.clear();
        dpFechaCreacion.setValue(null);
    }

    private void cambiarEstadoCampos(boolean edicionActiva) {
        txtId.setDisable(true);
        txtNombre.setDisable(!edicionActiva);
        txtDescripcion.setDisable(!edicionActiva);
        txtPrecio.setDisable(!edicionActiva);
        txtStock.setDisable(!edicionActiva);
        txtCategoria.setDisable(!edicionActiva);
        txtMarca.setDisable(!edicionActiva);
        dpFechaCreacion.setDisable(!edicionActiva);

        btnGuardar.setDisable(!edicionActiva);
        btnCancelar.setDisable(!edicionActiva);

        btnNuevo.setDisable(edicionActiva);
        btnEditar.setDisable(edicionActiva || tablaProductos.getSelectionModel().isEmpty());
        btnEliminar.setDisable(edicionActiva || tablaProductos.getSelectionModel().isEmpty());

        btnSiguiente.setDisable(edicionActiva);
        btnAnterior.setDisable(edicionActiva);
        txtBuscar.setDisable(edicionActiva);
    }

    @FXML
    private void clicNuevo() {
        limpiarCampos();
        tipoOperacion = Operacion.NUEVO;
        cambiarEstadoCampos(true);
        dpFechaCreacion.setValue(LocalDate.now()); 
    }

    @FXML
    private void clicEditar() {
        if (tablaProductos.getSelectionModel().getSelectedItem() != null) {
            tipoOperacion = Operacion.EDITAR;
            cambiarEstadoCampos(true);
        } else {
            System.out.println("Por favor, selecciona un producto para editar.");
        }
    }

    @FXML
    private void clicEliminar() {
        Producto p = tablaProductos.getSelectionModel().getSelectedItem();
        if (p != null) {
            try {
                CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("CALL sp_eliminarProducto(?);");
                cs.setInt(1, p.getIdProducto());
                cs.execute();
                cargarProductos();
                limpiarCampos();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error al eliminar producto: " + e.getMessage());
            }
        } else {
            System.out.println("Por favor, selecciona un producto para eliminar.");
        }
    }

    @FXML
    private void clicGuardar() {
        Producto producto = obtenerDatosFormulario();
        try {
            if (tipoOperacion == Operacion.NUEVO) {
                CallableStatement cs = Conexion.getInstancia().getConexion()
                                                .prepareCall("CALL sp_agregarProducto(?,?,?,?,?,?,?);"); // <-- CORREGIDO: 7 '?'
                cs.setString(1, producto.getNombreProducto());
                cs.setString(2, producto.getDescripcionProducto());
                cs.setBigDecimal(3, producto.getPrecio());
                cs.setInt(4, producto.getStock());
                cs.setString(5, producto.getCategoria());
                cs.setString(6, producto.getMarca());
                // Aseguramos que el parámetro 7 (fechaCreacion) se pase correctamente
                cs.setTimestamp(7, (producto.getFechaCreacion() != null) ? Timestamp.valueOf(producto.getFechaCreacion()) : null);
                cs.execute();
            } else if (tipoOperacion == Operacion.EDITAR) {
                CallableStatement cs = Conexion.getInstancia().getConexion()
                                                .prepareCall("CALL sp_editarProducto(?,?,?,?,?,?,?,?);"); // <-- Asumiendo 8 '?' para EDITAR (ID + 7 campos)
                cs.setInt(1, producto.getIdProducto());
                cs.setString(2, producto.getNombreProducto());
                cs.setString(3, producto.getDescripcionProducto());
                cs.setBigDecimal(4, producto.getPrecio());
                cs.setInt(5, producto.getStock());
                cs.setString(6, producto.getCategoria());
                cs.setString(7, producto.getMarca());
                cs.setTimestamp(8, (producto.getFechaCreacion() != null) ? Timestamp.valueOf(producto.getFechaCreacion()) : null);
                cs.execute();
            }
            tipoOperacion = Operacion.NINGUNA;
            cambiarEstadoCampos(false);
            cargarProductos();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al guardar producto: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en números: " + e.getMessage() + ". Asegúrate de ingresar valores numéricos válidos para Precio y Stock.");
        }
    }

    @FXML
    private void clicCancelar() {
        cargarProductoSeleccionado();
        tipoOperacion = Operacion.NINGUNA;
        cambiarEstadoCampos(false);
    }

    @FXML
    private void clicSiguiente() {
        int index = tablaProductos.getSelectionModel().getSelectedIndex();
        if (index < listaProductos.size() - 1) {
            tablaProductos.getSelectionModel().select(index + 1);
            cargarProductoSeleccionado();
        }
    }

    @FXML
    private void clicAnterior() {
        int index = tablaProductos.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            tablaProductos.getSelectionModel().select(index - 1);
            cargarProductoSeleccionado();
        }
    }

//    @FXML
//    public void clicRegresar(ActionEvent evento) {
//        if (evento.getSource() == btnMenu) {
//            if (principal != null) {
//                principal.mostrarInicioView();
//            } else {
//                System.out.println("Error: La instancia de Main no está configurada en TablaGuitarrasController.");
//            }
//        }
//    }

    
    // buscar por producto
    @FXML
    private void buscarProducto() {
        String texto = txtBuscar.getText().toLowerCase();
        ArrayList<Producto> resultados = new ArrayList<>();
        for (Producto p : listaProductos) {
            if (p.getNombreProducto().toLowerCase().contains(texto) ||
                p.getDescripcionProducto().toLowerCase().contains(texto) ||
                p.getCategoria().toLowerCase().contains(texto) ||
                p.getMarca().toLowerCase().contains(texto)) {
                resultados.add(p);
            }
        }
        tablaProductos.setItems(FXCollections.observableArrayList(resultados));
        if (!resultados.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
            cargarProductoSeleccionado();
        } else {
            limpiarCampos();
        }
    }
}