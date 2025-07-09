/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.robertomilian.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Roberto
 */
public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenario) throws Exception {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("/view/InicioView.fxml"));
        
        Parent raiz = cargador.load();
        Scene escena = new Scene(raiz);
        escenario.setScene(escena);
        escenario.show();        
    }
}
