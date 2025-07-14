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
public class MenuPrincipalController implements Initializable {

    private Main principal;


    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    public void escenaInicio() {
        principal.inicio();
    }
    public void escenaCompras() {
        principal.compras();
    }
    public void escenaDetalleCompras() {
        principal.detalleCompra();
    }
    public void escenaGuitarras() {
        principal.guitarras();
    }
    public void escenaUsuarios() {
        principal.usuarios();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
