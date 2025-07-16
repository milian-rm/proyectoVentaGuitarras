package org.robertomilian.model;

import java.math.BigDecimal;

/**
 *
 * @author Bradley Oliva
 */
public class DetalleCompra {

    private int idDetalleOrden;
    private int idOrden;
    private int idProducto;
    private int cantidad;
    private BigDecimal precioUnitario;

    public DetalleCompra() {
    }

    public DetalleCompra(int idDetalleOrden, int idOrden, int idProducto,
                         int cantidad, BigDecimal precioUnitario) {
        this.idDetalleOrden = idDetalleOrden;
        this.idOrden = idOrden;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getIdDetalleOrden() {
        return idDetalleOrden;
    }

    public void setIdDetalleOrden(int idDetalleOrden) {
        this.idDetalleOrden = idDetalleOrden;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    // obtener subtotal
    public BigDecimal getSubtotal() {
        if (precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(new BigDecimal(cantidad));
    }
    

    @Override
    public String toString() {
        return idDetalleOrden + " | Orden: " + idOrden + " | Producto: " + idProducto + " | Cantidad: " + cantidad + " | Precio Unitario: " + precioUnitario;
    }
}