/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package org.robertomilian.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.robertomilian.model.DetalleCompra;
import org.robertomilian.model.Producto;
import org.robertomilian.system.Main;

/**
 * FXML Controller class
 *
 * @author Roberto
 */
public class ComprarController extends TablaGuitarrasController implements Initializable{
    private TablaDetalleCompraController detalleCompraController;
    
    private Main principal;


    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    
    @FXML 
    private TableView<DetalleCompra> tablaDetalleTemporal;
    @FXML 
    private TableColumn colIdDetalleOrden, colIdOrden, colIdProducto, colCantidad, colPrecioUnitario;
    
    private ObservableList<DetalleCompra> listaDetalleCompra;
    private int numeroOrden = 1;
    
    
    @FXML
    private TextField txtCantidad;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.inicializarProductos();
        tablaProductos.setOnMouseClicked(e -> {
            if (tipoOperacion == Operacion.NINGUNA) {
                
            }
        });
        configurarColumnas();
        cargarDetallesCompra();
    }    
    
    private void configurarColumnas() {
        colIdDetalleOrden.setCellValueFactory(new PropertyValueFactory<>("idDetalleOrden"));
        colIdOrden.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
    }
    
    private ArrayList<DetalleCompra> obtenerDetallesCompra() {
        ArrayList<DetalleCompra> lista = new ArrayList<>();
        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                            .prepareCall("call sp_listarDetalleCompraEspecifico(?)")) {
            cs.setInt(1, numeroOrden); 
            try (ResultSet rs = cs.executeQuery()) { 
                while (rs.next()) {
                    lista.add(new DetalleCompra(
                            rs.getInt("ID_DETALLE"),
                            rs.getInt("ID_ORDEN"),
                            rs.getInt("ID_PRODUCTO"),
                            rs.getInt("CANTIDAD"),
                            rs.getBigDecimal("PRECIO_UNITARIO")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener detalles de compra: " + e.getMessage());
        }
        return lista;
    }
    
    private void cargarDetallesCompra() {
        listaDetalleCompra = FXCollections.observableArrayList(obtenerDetallesCompra());
        tablaDetalleTemporal.setItems(listaDetalleCompra);
        
    }
    
    
    private DetalleCompra obtenerDatos(){
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        int idDetalleOrden = 0;
        int idOrden = numeroOrden;
        int idProducto = productoSeleccionado.getIdProducto();
        int cantidad = 0;
        try {
            if (!txtCantidad.getText().isEmpty()) {
                int cantidadIngresada = Integer.valueOf(txtCantidad.getText());
                if (cantidadIngresada< productoSeleccionado.getStock()) {
                    cantidad = Integer.parseInt(txtCantidad.getText());
                }else{
                    System.out.println("Usted está queriendo seleccionar más productos de los que tenemos en stock");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en Cantidad: " + e.getMessage());
        }
        BigDecimal precio = productoSeleccionado.getPrecio();
        
        return new DetalleCompra(
                idDetalleOrden,
                idOrden,
                idProducto,
                cantidad,
                precio);
    }
    
    
    public void agregarDetalle(){
        DetalleCompra detalleCompra = obtenerDatos();
        try (CallableStatement cs = Conexion.getInstancia().getConexion()
                                                    .prepareCall("call sp_agregarDetalleCompra(?,?,?,?);")) {
                    cs.setInt(1, detalleCompra.getIdOrden());
                    cs.setInt(2, detalleCompra.getIdProducto());
                    cs.setInt(3, detalleCompra.getCantidad());
                    cs.setBigDecimal(4, detalleCompra.getPrecioUnitario());
                    cs.execute();
                    System.out.println("Detalle de compra agregado con éxito.");
            
        } catch (Exception e) {
        }
        
    }
    
    
    
    public void agregarProducto(){
        
    }
    
    
}
