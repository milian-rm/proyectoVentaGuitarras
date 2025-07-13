/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.robertomilian.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.robertomilian.controller.InicioController;
import org.robertomilian.controller.InicioSesionController;
import org.robertomilian.controller.RegistrarseController;

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
        InicioController ic = cambiarEscena("InicioView.fxml",950,720).getController();
        ic.setPrincipal(this);
    }
    public void inicioSesion(){
        InicioSesionController isc = cambiarEscena("InicioSesion.fxml",950,720).getController();
        isc.setPrincipal(this);
    }
    public void registrarse(){
        RegistrarseController rc = cambiarEscena("Registrarse.fxml",950,720).getController();
        rc.setPrincipal(this);
    }
}
