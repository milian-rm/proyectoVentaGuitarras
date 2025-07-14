/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.robertomilian.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.robertomilian.system.Main;

/**
 * FXML Controller class
 *
 * @author Roberto
 */
public class InicioController implements Initializable {
    
    private Main principal;


    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    public void escenaInicioSesion() {
        principal.inicioSesion();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
