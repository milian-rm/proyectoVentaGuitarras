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
import org.robertomilian.controller.FacturaController;
import org.robertomilian.model.DetalleCompra;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.robertomilian.database.Conexion;
import java.math.BigDecimal;
import javafx.collections.ObservableList;

/**
 *
 * @author Roberto
 */
public class Main extends Application{
    
    private static String URL_VIEW = "/view/";
    private Stage escenarioPrincipal;
    
    private int idUsuarioActual = 1; 

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
            cargadorFXML = new FXMLLoader(getClass().getResource(URL_VIEW + fxml));
            Parent archivoFXML = cargadorFXML.load();
            Scene escena = new Scene(archivoFXML, ancho, alto);
            escenarioPrincipal.setScene(escena);
        } catch (Exception ex) {
            System.out.println("Error al cambiar escena: "+ ex.getMessage());
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
        MenuPrincipalController mpc = cambiarEscena("MenuPrincipal.fxml",600,613).getController();
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
        try {
            int nuevaOrdenId = crearNuevaOrdenYObtenerId(idUsuarioActual); 
            
            if (nuevaOrdenId != -1) {
                FXMLLoader loader = cambiarEscena("Comprar.fxml",1337,679);
                ComprarController cc = loader.getController();
                cc.setPrincipal(this);
                
                cc.iniciarNuevaCompra(nuevaOrdenId);
            } else {
                System.out.println("Error: No se pudo crear una nueva orden de compra. Revisa logs.");
            }

        } catch (Exception e) {
            System.out.println("Error en vender(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int crearNuevaOrdenYObtenerId(int p_idUsuario) {
        int idGenerado = -1;
        try (Connection conn = Conexion.getInstancia().getConexion();
             CallableStatement cs = conn.prepareCall("{CALL sp_crearOrdenVacia(?, ?)}")) { 
            
            cs.setInt(1, p_idUsuario);
            cs.registerOutParameter(2, java.sql.Types.INTEGER);

            cs.execute();
            idGenerado = cs.getInt(2);

            System.out.println("Main: Nueva orden de compra creada con ID: " + idGenerado + " para usuario " + p_idUsuario);
        } catch (SQLException e) {
            System.out.println("Error al crear nueva orden de compra en la DB: " + e.getMessage());
            e.printStackTrace();
        }
        return idGenerado;
    }
     public void mostrarVistaFactura(ObservableList<DetalleCompra> detallesCompra, BigDecimal totalFactura, int numeroOrden, String nitCliente) {
        FXMLLoader loader = cambiarEscena("FacturaView.fxml", 800, 600);
        FacturaController fc = loader.getController();
        fc.setPrincipal(this);
        fc.setDatosFactura(detallesCompra, totalFactura, numeroOrden, nitCliente);
    }

    public void mostrarVistaComprar() {
        vender();
    }
}