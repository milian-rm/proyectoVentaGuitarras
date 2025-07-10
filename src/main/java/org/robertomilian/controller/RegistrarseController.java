package org.robertomilian.controller;

import com.mysql.jdbc.PreparedStatement;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import org.robertomilian.system.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;


public class RegistrarseController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    /*

    @FXML
    private TextField txtID, txtNombre, txtBuscar;

    @FXML
    private Button btnRegresarMenuRJugador, btnNuevo, btnEditar, btnEliminar,
            btnGuardar, btnCancelar;

    private Main principal;
    private ObservableList<Jugador> listaJugadores;
    private Jugador modeloJugador;
    private enum acciones {AGREGAR, ELIMINAR, ACTUALIZAR, NINGUNA}
    acciones tipoDeAccion = acciones.NINGUNA;

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public Main getPrincipal() {
        return principal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarTablaJugadores();
        tablaJugadores.setOnMouseClicked(eventoHandler -> cargarJugadorEnTextField());

        cambiarEstado(true);
        btnGuardar.setDisable(true);
        btnCancelar.setDisable(true);
    }

    public void configurarColumnas() {
        colIdJugador.setCellValueFactory(new PropertyValueFactory<Jugador, Integer>("idJugador"));
        colNombreJugador.setCellValueFactory(new PropertyValueFactory<Jugador, String>("nombre"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<Jugador, LocalDateTime>("fechaRegistro"));
    }

    public void cargarTablaJugadores() {
        listaJugadores = FXCollections.observableArrayList(listarJugadores());
        tablaJugadores.setItems(listaJugadores);
        if (!listaJugadores.isEmpty()) {
            tablaJugadores.getSelectionModel().selectFirst();
            cargarJugadorEnTextField();
        } else {
            limpiarTextField();
        }
        habilitarDeshabilitarNodo(false);
    }

    public void cargarJugadorEnTextField() {
        Jugador jugadorSeleccionado = tablaJugadores.getSelectionModel().getSelectedItem();
        if (jugadorSeleccionado != null) {
            txtID.setText(String.valueOf(jugadorSeleccionado.getIdJugador()));
            txtNombre.setText(jugadorSeleccionado.getNombre());
        } else {
            limpiarTextField();
        }
    }

    public ArrayList<Jugador> listarJugadores() {
        ArrayList<Jugador> jugadores = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_ListarJugadores();");
            ResultSet resultado = enunciado.executeQuery();

            while (resultado.next()) {
                jugadores.add(new Jugador(
                        resultado.getInt("idJugador"),
                        resultado.getString("nombre"),
                        resultado.getTimestamp("fechaRegistro").toLocalDateTime()));
            }
        } catch (SQLException ex) {
            System.err.println("Error al cargar jugadores desde MySQL: " + ex.getMessage());
            ex.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error de Base de Datos", "No se pudieron cargar los jugadores.",
                    "Ocurrió un error al intentar recuperar los datos de la base de datos.");
        }
        return jugadores;
    }

    public Jugador obtenerModeloJugador() {
        int idJugador = 0;
        if (!txtID.getText().isEmpty()) {
            try {
                idJugador = Integer.parseInt(txtID.getText());
            } catch (NumberFormatException e) {
                mostrarAlerta(AlertType.ERROR, "Error de Formato", "ID de Jugador Inválido",
                        "El ID del jugador debe ser un número entero.");
                return null;
            }
        }

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Campos Vacíos", "Nombre Requerido",
                    "Por favor, ingrese el nombre del jugador.");
            return null;
        }

        LocalDateTime fechaReg = null;
        if (tipoDeAccion == acciones.ACTUALIZAR && tablaJugadores.getSelectionModel().getSelectedItem() != null) {
            fechaReg = tablaJugadores.getSelectionModel().getSelectedItem().getFechaRegistro();
        }

        Jugador jugador = new Jugador(idJugador, nombre, fechaReg);
        return jugador;
    }


    public void agregarJugador() {
        modeloJugador = obtenerModeloJugador();
        if (modeloJugador == null) {
            return;
        }
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_AgregarJugador(?);");
            enunciado.setString(1, modeloJugador.getNombre());
            enunciado.execute();
            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Jugador Agregado",
                    "El jugador se ha agregado correctamente a la base de datos.");
            cargarTablaJugadores();
        } catch (SQLException ex) {
            System.err.println("Error al agregar jugador: " + ex.getMessage());
            ex.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error de Base de Datos", "No se pudo agregar el jugador.",
                    "Ocurrió un error al intentar agregar el jugador.");
        }
    }

    public void actualizarJugador() {
        modeloJugador = obtenerModeloJugador();
        if (modeloJugador == null) {
            return;
        }
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_EditarJugador(?, ?);");
            enunciado.setInt(1, modeloJugador.getIdJugador());
            enunciado.setString(2, modeloJugador.getNombre());
            enunciado.execute();
            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Jugador Actualizado",
                    "El jugador se ha actualizado correctamente.");
            cargarTablaJugadores();
        } catch (SQLException e) {
            System.err.println("Error al editar/actualizar jugador: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Error de Base de Datos", "No se pudo actualizar el jugador.",
                    "Ocurrió un error al intentar actualizar el jugador.");
        }
    }

    public void eliminarJugador() {
        Jugador jugadorSeleccionado = tablaJugadores.getSelectionModel().getSelectedItem();
        if (jugadorSeleccionado == null) {
            mostrarAlerta(AlertType.WARNING, "Sin Selección", "Ningún Jugador Seleccionado",
                    "Por favor, seleccione el jugador que desea eliminar.");
            return;
        }

        Optional<ButtonType> result = mostrarAlerta(AlertType.CONFIRMATION, "Confirmar Eliminación",
                "¿Está seguro de que desea eliminar este jugador?",
                "Esta acción no se puede deshacer. ¿Desea continuar?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion().
                        prepareCall("call sp_EliminarJugador(?);");
                enunciado.setInt(1, jugadorSeleccionado.getIdJugador());
                enunciado.execute();
                mostrarAlerta(AlertType.INFORMATION, "Éxito", "Jugador Eliminado",
                        "El jugador se ha eliminado correctamente.");
                cargarTablaJugadores();
                limpiarTextField();
            } catch (SQLException e) {
                System.err.println("Error al eliminar jugador: " + e.getMessage());
                e.printStackTrace();
                mostrarAlerta(AlertType.ERROR, "Error de Base de Datos", "No se pudo eliminar el jugador.",
                        "Ocurrió un error al intentar eliminar el jugador.");
            }
        }
    }

    public void limpiarTextField() {
        txtID.clear();
        txtNombre.clear();
        txtBuscar.clear();
    }

    private void cambiarEstado(boolean estado) {
        
    }

    private void habilitarDeshabilitarNodo(boolean habilitar) {
        txtID.setDisable(true);
        btnNuevo.setDisable(habilitar);
        btnEditar.setDisable(habilitar);
        btnEliminar.setDisable(habilitar);

        txtNombre.setDisable(!habilitar);
        btnGuardar.setDisable(!habilitar);
        btnCancelar.setDisable(!habilitar);
    }

    @FXML
    private void btnNuevoAction() {
        limpiarTextField();
        txtNombre.requestFocus();
        tipoDeAccion = acciones.AGREGAR;
        habilitarDeshabilitarNodo(true);
        tablaJugadores.getSelectionModel().clearSelection();
    }

    @FXML
    private void btnEditarAction() {
        if (tablaJugadores.getSelectionModel().getSelectedItem() == null) {
            mostrarAlerta(AlertType.WARNING, "Sin Selección", "Ningún Jugador Seleccionado",
                    "Por favor, seleccione el jugador que desea editar.");
            return;
        }
        tipoDeAccion = acciones.ACTUALIZAR;
        habilitarDeshabilitarNodo(true);
    }

    @FXML
    private void btnEliminarAction() {
        eliminarJugador();
    }

    @FXML
    private void btnCancelarAction() {
        if (!listaJugadores.isEmpty()) {
            tablaJugadores.getSelectionModel().select(0);
            cargarJugadorEnTextField();
        } else {
            limpiarTextField();
        }
        tipoDeAccion = acciones.NINGUNA;
        habilitarDeshabilitarNodo(false);
    }

    @FXML
    public void btnGuardarAction() {
        if (tipoDeAccion == acciones.AGREGAR) {
            agregarJugador();
        } else if (tipoDeAccion == acciones.ACTUALIZAR) {
            actualizarJugador();
        }
        tipoDeAccion = acciones.NINGUNA;
        habilitarDeshabilitarNodo(false);
    }

    @FXML
    public void buscarJugador() {
        ArrayList<Jugador> resultadoBusqueda = new ArrayList<>();
        String nombreABuscar = txtBuscar.getText().toLowerCase().trim();

        if (nombreABuscar.isEmpty()) {
            cargarTablaJugadores();
            return;
        }

        for (Jugador jugador : listaJugadores) {
            if (jugador.getNombre().toLowerCase().contains(nombreABuscar)) {
                resultadoBusqueda.add(jugador);
            }
        }
        tablaJugadores.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (!resultadoBusqueda.isEmpty()) {
            tablaJugadores.getSelectionModel().selectFirst();
            cargarJugadorEnTextField();
        } else {
            limpiarTextField();
        }
    }

    private Optional<ButtonType> mostrarAlerta(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (type == AlertType.CONFIRMATION) {
            return alert.showAndWait();
        } else {
            alert.showAndWait();
            return Optional.empty();
        }
    }
    
        public void clicRegresarAlMenu(ActionEvent evento){
        if (evento.getSource() == btnRegresarMenuRJugador) {
            System.out.println("Nos lleva al menu principal");
            principal.menuPrincipal();
        }
    }
        
    public static List<String> obtenerUltimosJugadores() {
        List<String> jugadores = new ArrayList<>();
        try {
            PreparedStatement statement = (PreparedStatement) Conexion.getInstancia().getConexion().prepareCall("call sp_ObtenerUltimosJugadores()");
            ResultSet resultado = statement.executeQuery();

            while (resultado.next()) {
                jugadores.add(resultado.getString("nombre"));
            }

            resultado.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jugadores;
    }*/
}

