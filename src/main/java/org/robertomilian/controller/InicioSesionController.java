package org.robertomilian.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.robertomilian.database.Conexion;
import org.robertomilian.system.Main;

/**
 * FXML Controller class
 *
 * @author Roberto
 */
public class InicioSesionController implements Initializable {

    private Main principal;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContraseña;

    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    public void escenaMenuPrincipal() {
        principal.menuPrincipal();
    }
    public void escenaRegistrarse() {
        principal.registrarse();
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void iniciarSesion() {
        String correo = txtCorreo.getText();
        String contrasena = txtContraseña.getText();
        if (correo.equals("") || contrasena.equals("")) {
            mostrarAlerta("Campos vacíos", "Por favor ingresa el correo y la contraseña.");
            return;
        }
        if (validarCredenciales(correo, contrasena)) {
            escenaMenuPrincipal();
        } else {
            mostrarAlerta("Credenciales inválidas", "Correo o contraseña incorrectos.");
        }
    }

    private boolean validarCredenciales(String correo, String contrasena) {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            CallableStatement cs = conexion.prepareCall("call sp_ValidarUsuario(?, ?)");
            cs.setString(1, correo);
            cs.setString(2, contrasena);

            ResultSet rs = cs.executeQuery();
            return rs.next(); // Si hay resultados, el usuario existe

        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: ");
            return false;
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
}
