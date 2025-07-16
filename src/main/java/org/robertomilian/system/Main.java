/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.robertomilian.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.robertomilian.controller.ComprarController;
import org.robertomilian.controller.InicioController;
import org.robertomilian.controller.InicioSesionController;
import org.robertomilian.controller.MenuPrincipalController;
import org.robertomilian.controller.RegistrarseController;
import org.robertomilian.controller.TablaComprasController;
import org.robertomilian.controller.TablaDetalleCompraController;
import org.robertomilian.controller.TablaGuitarrasController;
import org.robertomilian.controller.TablaUsuariosController;

/**
 *
 * @author Roberto
 */
public class Main extends Application{
    
    private static String URL_VIEW = "/view/";
    private Stage escenarioPrincipal;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenario) throws Exception {
        this.escenarioPrincipal = escenario;
        inicio();
        escenario.setTitle("GuitarKinal");
        escenario.show();     
    }
    
    public FXMLLoader cambiarEscena(String fxml, double ancho, double alto){
            FXMLLoader cargadorFXML = null;
        try {
            cargadorFXML = new FXMLLoader(getClass().getResource(URL_VIEW+fxml));
            Parent archivoFXML = cargadorFXML.load();
            Scene escena = new Scene(archivoFXML,ancho,alto);
            escenarioPrincipal.setScene(escena);
        } catch (Exception ex) {
            System.out.println("Error al cambiar: "+ ex.getMessage());
            ex.printStackTrace();
        }
        return cargadorFXML;
    }
    
    public void inicio(){
        InicioController ic = cambiarEscena("InicioView.fxml",894,555).getController();
        ic.setPrincipal(this);
    }
    public void inicioSesion(){
        InicioSesionController isc = cambiarEscena("InicioSesion.fxml",526,713).getController();
        isc.setPrincipal(this);
    }
    public void registrarse(){
        RegistrarseController rc = cambiarEscena("Registrarse.fxml",706,500).getController();
        rc.setPrincipal(this);
    }
    public void menuPrincipal(){
        MenuPrincipalController mpc = cambiarEscena("MenuPrincipal.fxml",600,463).getController();
        mpc.setPrincipal(this);
    }
    public void compras(){
        TablaComprasController tcc = cambiarEscena("TablaComprasView.fxml",985,705).getController();
        tcc.setPrincipal(this);
    }
    public void detalleCompra(){
        TablaDetalleCompraController tdc = cambiarEscena("TablaDetalleCompraView.fxml",985,697).getController();
        tdc.setPrincipal(this);
    }
    public void guitarras(){
        TablaGuitarrasController tgc = cambiarEscena("TablaGuitarrasView.fxml",985,705).getController();
        tgc.setPrincipal(this);
    }
    public void usuarios(){
        TablaUsuariosController tuc = cambiarEscena("TablaUsuariosView.fxml",985,705).getController();
        tuc.setPrincipal(this);
    }
    public void vender(){
        ComprarController cc = cambiarEscena("Comprar.fxml",985,705).getController();
        cc.setPrincipal(this);
    }
}
