

package org.robertomilian.controller;

import java.net.URL;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.robertomilian.database.Conexion;
import org.robertomilian.model.Usuarios;
import org.robertomilian.system.Main;
import javafx.scene.control.PasswordField;
/**
 * FXML Controller class
 *
 * @author Marcos
 */


public class TablaUsuariosController implements Initializable {

    private Main principal;

    @FXML private TableView<Usuarios> tablaUsuarios;
    @FXML private TableColumn colIdUsuario, colNombreUsuario, colApellidoUsuario, colEmailUsuario, colTelefonoUsuario, colDireccionUsuario, colFechaRegistro, colContrasena;

    @FXML private TextField txtIdUsuario, txtNombreUsuario, txtApellidoUsuario, txtEmailUsuario, txtTelefonoUsuario, txtDireccionUsuario, txtBuscar;
    @FXML private DatePicker dpFechaRegistro;
    @FXML private PasswordField txtContrasena;

    @FXML private Button btnNuevo, btnEditar, btnEliminar, btnGuardar, btnCancelar;
    @FXML private Button btnSiguiente, btnAnterior, btnMenu;

    private ObservableList<Usuarios> listaUsuarios;

    private enum Operacion {NINGUNA, NUEVO, EDITAR}
    private Operacion tipoOperacion = Operacion.NINGUNA;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarUsuarios();
        tablaUsuarios.setOnMouseClicked(e -> {
            if (tipoOperacion == Operacion.NINGUNA) {
                cargarUsuarioSeleccionado();
            }
        });
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> buscarUsuario());
        cambiarEstadoCampos(false);
    }

    private void configurarColumnas() {
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colApellidoUsuario.setCellValueFactory(new PropertyValueFactory<>("apellidoUsuario"));
        colEmailUsuario.setCellValueFactory(new PropertyValueFactory<>("emailUsuario"));
        colTelefonoUsuario.setCellValueFactory(new PropertyValueFactory<>("telefonoUsuario"));
        colDireccionUsuario.setCellValueFactory(new PropertyValueFactory<>("direccionUsuario"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        colContrasena.setCellValueFactory(new PropertyValueFactory<>("contraseña"));
    }

    private void cargarUsuarios() {
        listaUsuarios = FXCollections.observableArrayList(obtenerUsuarios());
        tablaUsuarios.setItems(listaUsuarios);
        if (!listaUsuarios.isEmpty()) {
            tablaUsuarios.getSelectionModel().selectFirst();
            cargarUsuarioSeleccionado();
        } else {
            limpiarCampos();
        }
    }

    private ArrayList<Usuarios> obtenerUsuarios() {
        ArrayList<Usuarios> lista = new ArrayList<>();
        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("CALL sp_listarUsuarios();");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("FECHA_REGISTRO");
                LocalDateTime fechaRegistro = (ts != null) ? ts.toLocalDateTime() : null;

                lista.add(new Usuarios(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("APELLIDO"),
                        rs.getString("EMAIL"),
                        rs.getString("TELEFONO"),
                        rs.getString("DIRECCION"),
                        fechaRegistro,
                        rs.getString("CONTRASENA")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    private void cargarUsuarioSeleccionado() {
        Usuarios u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u != null) {
            txtIdUsuario.setText(String.valueOf(u.getIdUsuario()));
            txtNombreUsuario.setText(u.getNombreUsuario());
            txtApellidoUsuario.setText(u.getApellidoUsuario());
            txtEmailUsuario.setText(u.getEmailUsuario());
            txtTelefonoUsuario.setText(u.getTelefonoUsuario());
            txtDireccionUsuario.setText(u.getDireccionUsuario());
            if (u.getFechaRegistro() != null) {
                dpFechaRegistro.setValue(u.getFechaRegistro().toLocalDate());
            } else {
                dpFechaRegistro.setValue(null);
            }
            txtContrasena.setText(u.getContraseña());
        } else {
            limpiarCampos();
        }
    }

    private Usuarios obtenerDatosFormulario() {
        int id = txtIdUsuario.getText().isEmpty() ? 0 : Integer.parseInt(txtIdUsuario.getText());
        
        LocalDateTime fechaRegistro;
        if (dpFechaRegistro.getValue() != null) {
            fechaRegistro = dpFechaRegistro.getValue().atStartOfDay();
        } else {
            fechaRegistro = null;
        }

        return new Usuarios(
                id,
                txtNombreUsuario.getText(),
                txtApellidoUsuario.getText(),
                txtEmailUsuario.getText(),
                txtTelefonoUsuario.getText(),
                txtDireccionUsuario.getText(),
                fechaRegistro,
                txtContrasena.getText()
        );
    }

    private void limpiarCampos() {
        txtIdUsuario.clear();
        txtNombreUsuario.clear();
        txtApellidoUsuario.clear();
        txtEmailUsuario.clear();
        txtTelefonoUsuario.clear();
        txtDireccionUsuario.clear();
        dpFechaRegistro.setValue(null);
        txtContrasena.clear();
    }

    private void cambiarEstadoCampos(boolean edicionActiva) {
        txtIdUsuario.setDisable(true);
        txtNombreUsuario.setDisable(!edicionActiva);
        txtApellidoUsuario.setDisable(!edicionActiva);
        txtEmailUsuario.setDisable(!edicionActiva);
        txtTelefonoUsuario.setDisable(!edicionActiva);
        txtDireccionUsuario.setDisable(!edicionActiva);
        dpFechaRegistro.setDisable(!edicionActiva);
        txtContrasena.setDisable(!edicionActiva);

        btnGuardar.setDisable(!edicionActiva);
        btnCancelar.setDisable(!edicionActiva);

        btnNuevo.setDisable(edicionActiva);
        btnEditar.setDisable(edicionActiva || tablaUsuarios.getSelectionModel().isEmpty());
        btnEliminar.setDisable(edicionActiva || tablaUsuarios.getSelectionModel().isEmpty());

        btnSiguiente.setDisable(edicionActiva);
        btnAnterior.setDisable(edicionActiva);
        txtBuscar.setDisable(edicionActiva);
    }

    @FXML
    private void clicNuevo() {
        limpiarCampos();
        tipoOperacion = Operacion.NUEVO;
        cambiarEstadoCampos(true);
        dpFechaRegistro.setValue(LocalDate.now());
    }

    @FXML
    private void clicEditar() {
        if (tablaUsuarios.getSelectionModel().getSelectedItem() != null) {
            tipoOperacion = Operacion.EDITAR;
            cambiarEstadoCampos(true);
        } else {
            System.out.println("Por favor, selecciona un usuario para editar.");
        }
    }

    @FXML
    private void clicEliminar() {
        Usuarios u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u != null) {
            try {
                CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("CALL sp_eliminarUsuario(?);");
                cs.setInt(1, u.getIdUsuario());
                cs.execute();
                cargarUsuarios();
                limpiarCampos();
                System.out.println("Usuario eliminado con éxito.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error al eliminar usuario: " + e.getMessage());
            }
        } else {
            System.out.println("Por favor, selecciona un usuario para eliminar.");
        }
    }

    @FXML
    private void clicGuardar() {
        Usuarios usuario = obtenerDatosFormulario();
        try {
            if (tipoOperacion == Operacion.NUEVO) {
                CallableStatement cs = Conexion.getInstancia().getConexion()
                                                .prepareCall("CALL sp_agregarUsuario(?,?,?,?,?,?,?);");
                cs.setString(1, usuario.getNombreUsuario());
                cs.setString(2, usuario.getApellidoUsuario());
                cs.setString(3, usuario.getEmailUsuario());
                cs.setString(4, usuario.getTelefonoUsuario());
                cs.setString(5, usuario.getDireccionUsuario());
                cs.setTimestamp(6, (usuario.getFechaRegistro() != null) ? Timestamp.valueOf(usuario.getFechaRegistro()) : null);
                cs.setString(7, usuario.getContraseña());
                cs.execute();
                System.out.println("Usuario agregado con éxito.");
            } else if (tipoOperacion == Operacion.EDITAR) {
                CallableStatement cs = Conexion.getInstancia().getConexion()
                                                .prepareCall("CALL sp_editarUsuario(?,?,?,?,?,?,?,?);");
                cs.setInt(1, usuario.getIdUsuario());
                cs.setString(2, usuario.getNombreUsuario());
                cs.setString(3, usuario.getApellidoUsuario());
                cs.setString(4, usuario.getEmailUsuario());
                cs.setString(5, usuario.getTelefonoUsuario());
                cs.setString(6, usuario.getDireccionUsuario());
                cs.setTimestamp(7, (usuario.getFechaRegistro() != null) ? Timestamp.valueOf(usuario.getFechaRegistro()) : null);
                cs.setString(8, usuario.getContraseña());
                cs.execute();
                System.out.println("Usuario editado con éxito.");
            }
            tipoOperacion = Operacion.NINGUNA;
            cambiarEstadoCampos(false);
            cargarUsuarios();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al guardar usuario: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en números: " + e.getMessage());
        }
    }

    @FXML
    private void clicCancelar() {
        cargarUsuarioSeleccionado();
        tipoOperacion = Operacion.NINGUNA;
        cambiarEstadoCampos(false);
    }

    @FXML
    private void clicSiguiente() {
        int index = tablaUsuarios.getSelectionModel().getSelectedIndex();
        if (index < listaUsuarios.size() - 1) {
            tablaUsuarios.getSelectionModel().select(index + 1);
            cargarUsuarioSeleccionado();
        }
    }

    @FXML
    private void clicAnterior() {
        int index = tablaUsuarios.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            tablaUsuarios.getSelectionModel().select(index - 1);
            cargarUsuarioSeleccionado();
        }
    }

//    @FXML
//    public void clicRegresar(ActionEvent evento) {
//        if (evento.getSource() == btnMenu) {
//            if (principal != null) {
//                principal.mostrarInicioView();
//            } else {
//                System.err.println("Error: La instancia de Main no está configurada en TablaUsuariosController.");
//            }
//        }
//    }

    @FXML
    private void buscarUsuario() {
        String texto = txtBuscar.getText().toLowerCase();
        ArrayList<Usuarios> resultados = new ArrayList<>();
        for (Usuarios u : listaUsuarios) {
            if (String.valueOf(u.getIdUsuario()).contains(texto) ||
                (u.getNombreUsuario() != null && u.getNombreUsuario().toLowerCase().contains(texto)) ||
                (u.getApellidoUsuario() != null && u.getApellidoUsuario().toLowerCase().contains(texto)) ||
                (u.getEmailUsuario() != null && u.getEmailUsuario().toLowerCase().contains(texto)) ||
                (u.getTelefonoUsuario() != null && u.getTelefonoUsuario().toLowerCase().contains(texto)) ||
                (u.getDireccionUsuario() != null && u.getDireccionUsuario().toLowerCase().contains(texto))) {
                resultados.add(u);
            }
        }
        tablaUsuarios.setItems(FXCollections.observableArrayList(resultados));
        if (!resultados.isEmpty()) {
            tablaUsuarios.getSelectionModel().selectFirst();
            cargarUsuarioSeleccionado();
        } else {
            limpiarCampos();
        }
    }
}
