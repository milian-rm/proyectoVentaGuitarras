package org.robertomilian.controller;

import com.mysql.jdbc.PreparedStatement;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.sql.CallableStatement;
import java.sql.Date;
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
import javafx.scene.control.DatePicker;
import org.robertomilian.database.Conexion;
import org.robertomilian.model.Usuario;


public class RegistrarseController implements Initializable {
    
    private Main principal;
    
    private Usuario modeloUsuario;
    private enum EstadoFormulario {AGREGAR, EDITAR, ELIMINAR, NINGUNA}
    EstadoFormulario estadoActual = EstadoFormulario.NINGUNA;
    
    
    @FXML
    private TextField  txtIdUsuario, txtNombre, txtApellido, txtEmail,
            txtTelefono, txtDireccion, txtContraseña;
    @FXML
    private DatePicker dpFechaRegistro;
    @FXML
    private Button btnCrearCuenta, btnInicioSesion;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
    }    
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public Main getPrincipal() {
        return principal;
    }
    
    private Usuario cargarModeloUsuario(){

        int idUsuario = txtIdUsuario.getText().isEmpty() ? 0 : Integer.parseInt(txtIdUsuario.getText());
        
        LocalDateTime fechaRegistro;
        if (dpFechaRegistro.getValue() != null) {
            fechaRegistro = dpFechaRegistro.getValue().atStartOfDay();
        } else {
            fechaRegistro = null;
        }

        return new Usuario(
                idUsuario, 
                txtNombre.getText(), 
                txtApellido.getText(), 
                txtEmail.getText(),
                txtTelefono.getText(), 
                txtDireccion.getText(),
                fechaRegistro, 
                txtContraseña.getText()
                );
    }
    

    public void agregarModelo(){
        modeloUsuario = cargarModeloUsuario();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().
                    prepareCall("call sp_AgregarUsuario(?,?,?,?,?,?,?);");
            enunciado.setString(1, modeloUsuario.getNombreUsuario());
            enunciado.setString(2, modeloUsuario.getApellidoUsuario());
            enunciado.setString(3, modeloUsuario.getEmailUsuario());
            enunciado.setString(4, modeloUsuario.getTelefonoUsuario());
            enunciado.setString(5, modeloUsuario.getDireccionUsuario());
            enunciado.setDate(6, Date.valueOf(modeloUsuario.getFechaRegistro().toLocalDate()));
            enunciado.setString(7, modeloUsuario.getContraseña());
            int registrosAgregados = enunciado.executeUpdate();
            if(registrosAgregados > 0){
                System.out.println("Usuario agregado");
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar un Usuario");
            e.printStackTrace();
        }
        
    }

    private void limpiarCampos(){
        txtIdUsuario.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtContraseña.clear();
        txtEmail.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        dpFechaRegistro.setValue(null);
    }
    private void actualizarEstadoFormulario(EstadoFormulario estado){
        estadoActual = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.EDITAR);
                
        btnCrearCuenta.setText(activo ? "Guardar":"Crear");
        btnInicioSesion.setText(activo ? "Cancelar":"InicioSesion");
        
    }
    
    @FXML
    private void manejarBotonInicio() {
        if (estadoActual == EstadoFormulario.AGREGAR || estadoActual == EstadoFormulario.EDITAR) {
            System.out.println("Operación cancelada");
            actualizarEstadoFormulario(EstadoFormulario.NINGUNA);
        } else {
            principal.inicio();
        }
    } 
    
    @FXML
    private void agregarUsuario(){
        agregarModelo();
        limpiarCampos();
        principal.inicioSesion();
    }
}

