
package org.robertomilian.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.robertomilian.model.DetalleCompra;
import org.robertomilian.system.Main;

/**
 * FXML Controller class
 *
 * @author Bradley Oliva
 */


public class FacturaController implements Initializable {

    private Main principal;

    @FXML
    private TableView<DetalleCompra> tblProductosFactura;
    @FXML
    private TableColumn<DetalleCompra, Integer> colIdProductoFactura;
    @FXML
    private TableColumn<DetalleCompra, Integer> colCantidadFactura;
    @FXML
    private TableColumn<DetalleCompra, BigDecimal> colPrecioUnitarioFactura;
    @FXML
    private TableColumn<DetalleCompra, BigDecimal> colSubtotalFactura;

    @FXML
    private Label lblTotalFactura;
    @FXML
    private Label lblNumeroOrden;
    @FXML
    private Label lblFechaFactura;
    @FXML
    private Label lblNitEmpresa;
    @FXML
    private Label lblNombreCliente;
    @FXML
    private Label lblNitCliente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIdProductoFactura.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCantidadFactura.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioUnitarioFactura.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotalFactura.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public void setDatosFactura(ObservableList<DetalleCompra> detalles, BigDecimal total, int numeroOrden, String nitCliente) {
        tblProductosFactura.setItems(detalles);
        lblTotalFactura.setText(String.format("Total: Q%.2f", total));
        lblNumeroOrden.setText("Orden #: " + numeroOrden);
        lblFechaFactura.setText("Fecha: " + java.time.LocalDate.now().toString());
        lblNitEmpresa.setText("NIT Empresa: 1234567-8");
        lblNitCliente.setText("NIT Cliente: " + nitCliente);
    }

    @FXML
    public void regresarAVistaAnterior() {
        if (principal != null) {
            principal.mostrarVistaComprar();
        }
    }
}
