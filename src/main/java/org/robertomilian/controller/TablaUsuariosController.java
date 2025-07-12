package org.robertomilian.controller;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javafx.event.ActionEvent;
/**
 * FXML Controller class
 *
 * @author Marcos
 */
public class TablaUsuariosController implements Initializable {
    
    @FXML private TableView<Usuarios> tablaUsuarios;
    @FXML private TableColumn colIdUsuario, colNombreUsuario, colApellidoUsuario, colEmailUsuario,
            colTelefonoUsuario, colDireccionUsuario, colFechaRegistro;

    @FXML private TextField txtBuscar, txtIdUsuario, txtNombreUsuario, txtApellidoUsuario,
            txtEmailUsuario, txtTelefonoUsuario, txtDireccionUsuario, txtContrasena;
    @FXML private DatePicker dpFechaRegistro;

    // Nuevos botones para navegación
    @FXML private Button btnRegresarMenu, btnNuevo, btnEditar, btnEliminar, btnBuscar;
    @FXML private Button btnAnterior, btnSiguiente;

    private Main principal;
    private ObservableList<Usuarios> listaUsuarios;
    private Usuarios modeloUsuario;
    private enum EstadoFormulario { AGREGAR, ELIMINAR, ACTUALIZAR, NINGUNA}
    EstadoFormulario estadoActual = EstadoFormulario.NINGUNA;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public Main getPrincipal() {
        return principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarTablaUsuarios();
        tablaUsuarios.setOnMouseClicked(eh -> cargarUsuarioEnCampos());
        actualizarEstadoFormulario(EstadoFormulario.NINGUNA);
    }

    public void configurarColumnas() {
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<Usuarios, Integer>("idUsuario"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<Usuarios, String>("nombreUsuario"));
        colApellidoUsuario.setCellValueFactory(new PropertyValueFactory<Usuarios, String>("apellidoUsuario"));
        colEmailUsuario.setCellValueFactory(new PropertyValueFactory<Usuarios, String>("emailUsuario"));
        colTelefonoUsuario.setCellValueFactory(new PropertyValueFactory<Usuarios, String>("telefonoUsuario"));
        colDireccionUsuario.setCellValueFactory(new PropertyValueFactory<Usuarios, String>("direccionUsuario"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<Usuarios, LocalDateTime>("fechaRegistro"));
    }

    private ArrayList<Usuarios> listarUsuarios() {
        ArrayList<Usuarios> usuarios = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_listarUsuarios();");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                usuarios.add(new Usuarios(
                        resultado.getInt("ID"),
                        resultado.getString("NOMBRE"),
                        resultado.getString("APELLIDO"),
                        resultado.getString("EMAIL"),
                        resultado.getString("TELEFONO"),
                        resultado.getString("DIRECCION"),
                        resultado.getTimestamp("FECHA_REGISTRO").toLocalDateTime(),
                        null
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar usuarios desde MySQL");
            e.printStackTrace();
        }
        return usuarios;
    }

    private void cargarTablaUsuarios() {
        listaUsuarios = FXCollections.observableArrayList(listarUsuarios());
        tablaUsuarios.setItems(listaUsuarios);
        tablaUsuarios.getSelectionModel().selectFirst();
        cargarUsuarioEnCampos();
    }

    private void cargarUsuarioEnCampos() {
        Usuarios usuario = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuario != null) {
            txtIdUsuario.setText(String.valueOf(usuario.getIdUsuario()));
            txtIdUsuario.setDisable(true);
            txtNombreUsuario.setText(usuario.getNombreUsuario());
            txtApellidoUsuario.setText(usuario.getApellidoUsuario());
            txtEmailUsuario.setText(usuario.getEmailUsuario());
            txtTelefonoUsuario.setText(usuario.getTelefonoUsuario());
            txtDireccionUsuario.setText(usuario.getDireccionUsuario());
            dpFechaRegistro.setValue(usuario.getFechaRegistro().toLocalDate());
            txtContrasena.clear();
        }
    }

    private Usuarios crearModeloUsuario() {
        int idUsuario = txtIdUsuario.getText().isEmpty() ? 0 : Integer.parseInt(txtIdUsuario.getText());

        LocalDateTime fechaRegistro = null;
        if (dpFechaRegistro.getValue() != null) {
            fechaRegistro = dpFechaRegistro.getValue().atStartOfDay();
        } else {
            fechaRegistro = LocalDateTime.now();
        }


        return new Usuarios(
                idUsuario,
                txtNombreUsuario.getText(),
                txtApellidoUsuario.getText(),
                txtEmailUsuario.getText(),
                txtTelefonoUsuario.getText(),
                txtDireccionUsuario.getText(),
                fechaRegistro,
                txtContrasena.getText()
        );
    }

    private void insertarUsuario() {
        modeloUsuario = crearModeloUsuario();
        if (modeloUsuario == null || modeloUsuario.getFechaRegistro() == null) {
            System.out.println("Error: No se pudo crear el modelo de usuario o la fecha de registro es nula.");
            return;
        }
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_agregarUsuario(?, ?, ?, ?, ?, ?, ?);");
            enunciado.setString(1, modeloUsuario.getNombreUsuario());
            enunciado.setString(2, modeloUsuario.getApellidoUsuario());
            enunciado.setString(3, modeloUsuario.getEmailUsuario());
            enunciado.setString(4, modeloUsuario.getTelefonoUsuario());
            enunciado.setString(5, modeloUsuario.getDireccionUsuario());
            enunciado.setTimestamp(6, Timestamp.valueOf(modeloUsuario.getFechaRegistro()));
            enunciado.setString(7, modeloUsuario.getContraseña());
            enunciado.execute();
            cargarTablaUsuarios();
        } catch (SQLException e) {
            System.out.println("Error al insertar un Usuario");
            e.printStackTrace();
        }
    }

    private void actualizarUsuario() {
        modeloUsuario = crearModeloUsuario();
        if (modeloUsuario == null || modeloUsuario.getFechaRegistro() == null) {
            System.out.println("Error: No se pudo crear el modelo de usuario o la fecha de registro es nula.");
            return;
        }
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_editarUsuario(?, ?, ?, ?, ?, ?, ?, ?);");
            enunciado.setInt(1, modeloUsuario.getIdUsuario());
            enunciado.setString(2, modeloUsuario.getNombreUsuario());
            enunciado.setString(3, modeloUsuario.getApellidoUsuario());
            enunciado.setString(4, modeloUsuario.getEmailUsuario());
            enunciado.setString(5, modeloUsuario.getTelefonoUsuario());
            enunciado.setString(6, modeloUsuario.getDireccionUsuario());
            enunciado.setTimestamp(7, Timestamp.valueOf(modeloUsuario.getFechaRegistro()));
            enunciado.setString(8, modeloUsuario.getContraseña());
            enunciado.execute();
            cargarTablaUsuarios();
        } catch (SQLException e) {
            System.out.println("Error al editar un Usuario");
            e.printStackTrace();
        }
    }

    private void eliminarUsuario() {
        modeloUsuario = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (modeloUsuario == null) {
            System.out.println("Error: No hay usuario seleccionado para eliminar.");
            return;
        }
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_eliminarUsuario(?);");
            enunciado.setInt(1, modeloUsuario.getIdUsuario());
            enunciado.execute();
            cargarTablaUsuarios();
        } catch (SQLException e) {
            System.out.println("Error al eliminar un Usuario");
            e.printStackTrace();
        }
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

    public void actualizarEstadoFormulario(EstadoFormulario estado) {
        estadoActual = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.ACTUALIZAR);

        txtNombreUsuario.setDisable(!activo);
        txtApellidoUsuario.setDisable(!activo);
        txtEmailUsuario.setDisable(!activo);
        txtTelefonoUsuario.setDisable(!activo);
        txtDireccionUsuario.setDisable(!activo);
        dpFechaRegistro.setDisable(!activo);
        txtContrasena.setDisable(!activo);

        tablaUsuarios.setDisable(activo);
        btnBuscar.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnAnterior.setDisable(activo || tablaUsuarios.getItems().isEmpty());
        btnSiguiente.setDisable(activo || tablaUsuarios.getItems().isEmpty());


        btnNuevo.setText(activo ? "Guardar" : "Nuevo");
        btnEliminar.setText(activo ? "Cancelar" : "Eliminar");
        btnEditar.setDisable(activo);
    }

    @FXML
    private void agregarUsuario() {
        switch (estadoActual) {
            case NINGUNA:
                limpiarCampos();
                System.out.println("Voy a crear un registro para usuarios");
                actualizarEstadoFormulario(EstadoFormulario.AGREGAR);
                break;
            case AGREGAR:
                insertarUsuario();
                System.out.println("Voy a guardar los datos ingresados");
                actualizarEstadoFormulario(EstadoFormulario.NINGUNA);
                break;
            case ACTUALIZAR:
                actualizarUsuario();
                System.out.println("Voy a guardar edición indicada");
                actualizarEstadoFormulario(EstadoFormulario.NINGUNA);
                break;
        }
    }

    @FXML
    private void editarUsuario() {
        actualizarEstadoFormulario(EstadoFormulario.ACTUALIZAR);
    }

    @FXML
    private void cancelarEliminar() {
        if (estadoActual == EstadoFormulario.NINGUNA) {
            eliminarUsuario();
            System.out.println("Voy a eliminar registro");
        } else {
            limpiarCampos();
            actualizarEstadoFormulario(EstadoFormulario.NINGUNA);
            cargarTablaUsuarios();
        }
    }

    @FXML
    private void buscarPorId() {
        String idTexto = txtBuscar.getText();
        if (idTexto != null && !idTexto.isEmpty()) {
            try {
                int id = Integer.parseInt(idTexto);
                ArrayList<Usuarios> resultadoBusqueda = new ArrayList<>();
                for (Usuarios usuario : listaUsuarios) {
                    if (usuario.getIdUsuario() == id) {
                        resultadoBusqueda.add(usuario);
                        break;
                    }
                }
                tablaUsuarios.setItems(FXCollections.observableArrayList(resultadoBusqueda));
                if (!resultadoBusqueda.isEmpty()) {
                    tablaUsuarios.getSelectionModel().selectFirst();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un ID válido para la búsqueda.");
            }
        } else {
            cargarTablaUsuarios();
        }
    }

    @FXML
    private void anteriorRegistro() {
        int indiceSeleccionado = tablaUsuarios.getSelectionModel().getSelectedIndex();
        if (indiceSeleccionado > 0) {
            tablaUsuarios.getSelectionModel().select(indiceSeleccionado - 1);
            cargarUsuarioEnCampos();
        }
    }

    @FXML
    private void siguienteRegistro() {
        int indiceSeleccionado = tablaUsuarios.getSelectionModel().getSelectedIndex();
        if (indiceSeleccionado < tablaUsuarios.getItems().size() - 1) {
            tablaUsuarios.getSelectionModel().select(indiceSeleccionado + 1);
            cargarUsuarioEnCampos();
        }
    }

    public void RegresarAlMenu(ActionEvent evento){
        if (evento.getSource() == btnRegresarMenu) {
            
        }
    }
}
