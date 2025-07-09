package org.robertomilian.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 *
 * @author Bradley Oliva
 */
public class Productos {

    private int idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private BigDecimal precio;
    private int stock;
    private String categoria;
    private String marca;
    private LocalDateTime fechaCreacion;

    public Productos() {
    }

    public Productos(int idProducto, String nombreProducto, String descripcionProducto,
                     BigDecimal precio, int stock, String categoria, String marca,
                     LocalDateTime fechaCreacion) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.marca = marca;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return idProducto + " | " + nombreProducto + " | " + categoria + " | " + marca + " | " + precio;
    }
}