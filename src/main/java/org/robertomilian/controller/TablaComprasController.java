package org.robertomilian.controller;
import java.net.URL;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.robertomilian.database.Conexion;
import org.robertomilian.model.Compra;
import org.robertomilian.model.Usuario;
import org.robertomilian.system.Main;


/**
 * FXML Controller class
 *
 * @author Marcos
 */
public class TablaComprasController implements Initializable {

    private Main principal;
    
    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn colIdOrden, colIdUsuario, colFechaOrden, colTotalOrden, colEstadoOrden;

    @FXML private TextField txtIdOrden, txtTotalOrden, txtBuscar;
    @FXML private DatePicker dpFechaOrden;
    @FXML private ComboBox<Usuario> cbxUsuarios;
    @FXML private TextField txtEstadoOrden;

    @FXML private Button btnNuevo, btnEditar, btnEliminar, btnGuardar, btnCancelar;
    @FXML private Button btnSiguiente, btnAnterior, btnMenu;

    private ObservableList<Compra> listaCompras;
    private ObservableList<Usuario> listaUsuarios;

    private enum Operacion {NINGUNA, NUEVO, EDITAR}
    private Operacion tipoOperacion = Operacion.NINGUNA;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarUsuarios();
        cargarCompras();
        tablaCompras.setOnMouseClicked(e -> {
            if (tipoOperacion == Operacion.NINGUNA) {
                cargarCompraSeleccionada();
            }
        });
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> buscarCompra());
        cambiarEstadoCampos(false);
    }

    private void configurarColumnas() {
        colIdOrden.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colFechaOrden.setCellValueFactory(new PropertyValueFactory<>("fechaOrden"));
        colTotalOrden.setCellValueFactory(new PropertyValueFactory<>("totalOrden"));
        colEstadoOrden.setCellValueFactory(new PropertyValueFactory<>("estadoOrden"));
    }

    private ArrayList<Compra> obtenerCompras() {
        ArrayList<Compra> lista = new ArrayList<>();
        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("call sp_listarCompras();");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("FECHA_ORDEN");
                LocalDateTime fechaOrden = (ts != null) ? ts.toLocalDateTime() : null;

                lista.add(new Compra(
                        rs.getInt("ID_ORDEN"),
                        rs.getInt("ID_USUARIO"),
                        fechaOrden,
                        rs.getBigDecimal("TOTAL_ORDEN"),
                        rs.getString("ESTADO_ORDEN")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener Compra: " + e.getMessage());
        }
        return lista;
    }

    private void cargarCompras() {
        listaCompras = FXCollections.observableArrayList(obtenerCompras());
        tablaCompras.setItems(listaCompras);
        if (!listaCompras.isEmpty()) {
            tablaCompras.getSelectionModel().selectFirst();
            cargarCompraSeleccionada();
        } else {
            limpiarCampos();
        }
    }

    private void cargarCompraSeleccionada() {
        Compra c = tablaCompras.getSelectionModel().getSelectedItem();
        if (c != null) {
            txtIdOrden.setText(String.valueOf(c.getIdOrden()));
            txtIdOrden.setDisable(true);
            dpFechaOrden.setValue(c.getFechaOrden() != null ? c.getFechaOrden().toLocalDate() : null);
            txtTotalOrden.setText(String.valueOf(c.getTotalOrden()));

            for (Usuario u : listaUsuarios) {
                if (u.getIdUsuario() == c.getIdUsuario()) {
                    cbxUsuarios.setValue(u);
                    break;
                }
            }
            txtEstadoOrden.setText(c.getEstadoOrden());
        } else {
            limpiarCampos();
        }
    }

    private ArrayList<Usuario> obtenerUsuariosParaComboBox() {
        ArrayList<Usuario> Usuario = new ArrayList<>();
        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("call sp_listarUsuarios();");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Usuario.add(new Usuario(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("APELLIDO"),
                        null, null, null, null, null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cargar Usuario para ComboBox: " + e.getMessage());
        }
        return Usuario;
    }

    private void cargarUsuarios() {
        listaUsuarios = FXCollections.observableArrayList(obtenerUsuariosParaComboBox());
        cbxUsuarios.setItems(listaUsuarios);
    }

    private Compra obtenerDatosFormulario() {
        int idOrden = txtIdOrden.getText().isEmpty() ? 0 : Integer.parseInt(txtIdOrden.getText());
        
        int idUsuario = 0;
        if (cbxUsuarios.getSelectionModel().getSelectedItem() != null) {
            idUsuario = cbxUsuarios.getSelectionModel().getSelectedItem().getIdUsuario();
        }

        LocalDateTime fechaOrden = null;
        if (dpFechaOrden.getValue() != null) {
            fechaOrden = dpFechaOrden.getValue().atStartOfDay();
        } else {
            fechaOrden = LocalDateTime.now();
        }

        BigDecimal totalOrden = BigDecimal.ZERO;
        try {
            if (!txtTotalOrden.getText().isEmpty()) {
                totalOrden = new BigDecimal(txtTotalOrden.getText());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en Total Orden: " + e.getMessage());
        }
        
        String estadoOrden = txtEstadoOrden.getText();
        if (estadoOrden == null || estadoOrden.isEmpty()) {
            estadoOrden = "Pendiente";
        }

        return new Compra(
                idOrden,
                idUsuario,
                fechaOrden,
                totalOrden,
                estadoOrden
        );
    }

    private void limpiarCampos() {
        txtIdOrden.clear();
        cbxUsuarios.getSelectionModel().clearSelection();
        dpFechaOrden.setValue(null);
        txtTotalOrden.clear();
        txtEstadoOrden.clear();
    }

    private void cambiarEstadoCampos(boolean edicionActiva) {
        txtIdOrden.setDisable(true);
        cbxUsuarios.setDisable(!edicionActiva);
        dpFechaOrden.setDisable(!edicionActiva);
        txtTotalOrden.setDisable(!edicionActiva);
        txtEstadoOrden.setDisable(!edicionActiva);

        btnGuardar.setDisable(!edicionActiva);
        btnCancelar.setDisable(!edicionActiva);

        btnNuevo.setDisable(edicionActiva);
        btnEditar.setDisable(edicionActiva || tablaCompras.getSelectionModel().isEmpty());
        btnEliminar.setDisable(edicionActiva || tablaCompras.getSelectionModel().isEmpty());

        btnSiguiente.setDisable(edicionActiva || tablaCompras.getItems().isEmpty());
        btnAnterior.setDisable(edicionActiva || tablaCompras.getItems().isEmpty());
        txtBuscar.setDisable(edicionActiva);
    }

    @FXML
    private void clicNuevo() {
        limpiarCampos();
        tipoOperacion = Operacion.NUEVO;
        cambiarEstadoCampos(true);
        dpFechaOrden.setValue(LocalDate.now());
        txtEstadoOrden.setText("Pendiente");
    }

    @FXML
    private void clicEditar() {
        if (tablaCompras.getSelectionModel().getSelectedItem() != null) {
            tipoOperacion = Operacion.EDITAR;
            cambiarEstadoCampos(true);
        } else {
            System.out.println("Por favor, selecciona una compra para editar.");
        }
    }

    @FXML
    private void clicEliminar() {
        Compra c = tablaCompras.getSelectionModel().getSelectedItem();
        if (c != null) {
            try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                                .prepareCall("call sp_eliminarCompra(?);")) {
                cs.setInt(1, c.getIdOrden());
                cs.execute();
                cargarCompras();
                limpiarCampos();
                System.out.println("Compra eliminada con éxito.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error al eliminar compra: " + e.getMessage());
            }
        } else {
            System.out.println("Por favor, selecciona una compra para eliminar.");
        }
    }

    @FXML
    private void clicGuardar() {
        Compra compra = obtenerDatosFormulario();
        try {
            if (tipoOperacion == Operacion.NUEVO) {
                try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                                    .prepareCall("call sp_agregarCompra(?,?,?,?);")) {
                    cs.setInt(1, compra.getIdUsuario());
                    cs.setTimestamp(2, (compra.getFechaOrden() != null) ? Timestamp.valueOf(compra.getFechaOrden()) : null);
                    cs.setBigDecimal(3, compra.getTotalOrden());
                    cs.setString(4, compra.getEstadoOrden());
                    cs.execute();
                    System.out.println("Compra agregada con éxito.");
                }
            } else if (tipoOperacion == Operacion.EDITAR) {
                try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                                    .prepareCall("call sp_editarCompra(?,?,?,?,?);")) {
                    cs.setInt(1, compra.getIdOrden());
                    cs.setInt(2, compra.getIdUsuario());
                    cs.setTimestamp(3, (compra.getFechaOrden() != null) ? Timestamp.valueOf(compra.getFechaOrden()) : null);
                    cs.setBigDecimal(4, compra.getTotalOrden());
                    cs.setString(5, compra.getEstadoOrden());
                    cs.execute();
                    System.out.println("Compra editada con éxito.");
                }
            }
            tipoOperacion = Operacion.NINGUNA;
            cambiarEstadoCampos(false);
            cargarCompras();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al guardar compra: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en números: " + e.getMessage());
        }
    }

    @FXML
    private void clicCancelar() {
        cargarCompraSeleccionada();
        tipoOperacion = Operacion.NINGUNA;
        cambiarEstadoCampos(false);
    }

    @FXML
    private void clicSiguiente() {
        int index = tablaCompras.getSelectionModel().getSelectedIndex();
        if (index < listaCompras.size() - 1) {
            tablaCompras.getSelectionModel().select(index + 1);
            cargarCompraSeleccionada();
        }
    }

    @FXML
    private void clicAnterior() {
        int index = tablaCompras.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            tablaCompras.getSelectionModel().select(index - 1);
            cargarCompraSeleccionada();
        }
    }

    @FXML
    private void buscarCompra() {
        String texto = txtBuscar.getText().toLowerCase();
        ArrayList<Compra> resultados = new ArrayList<>();
        for (Compra c : listaCompras) {
            if (String.valueOf(c.getIdOrden()).contains(texto) ||
                String.valueOf(c.getIdUsuario()).contains(texto) ||
                (c.getEstadoOrden() != null && c.getEstadoOrden().toLowerCase().contains(texto)) ||
                (c.getFechaOrden() != null && c.getFechaOrden().toLocalDate().toString().contains(texto)) ) {
                resultados.add(c);
            }
        }
        tablaCompras.setItems(FXCollections.observableArrayList(resultados));
        if (!resultados.isEmpty()) {
            tablaCompras.getSelectionModel().selectFirst();
            cargarCompraSeleccionada();
        } else {
            limpiarCampos();
        }
    }
}
