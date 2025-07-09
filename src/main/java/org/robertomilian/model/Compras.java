package org.robertomilian.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 *
 * @author Bradley Oliva
 */
public class Compras {

    private int idOrden;
    private int idUsuario;
    private LocalDateTime fechaOrden;
    private BigDecimal totalOrden;
    private String estadoOrden;

    public Compras() {
    }

    public Compras(int idOrden, int idUsuario, LocalDateTime fechaOrden,
                   BigDecimal totalOrden, String estadoOrden) {
        this.idOrden = idOrden;
        this.idUsuario = idUsuario;
        this.fechaOrden = fechaOrden;
        this.totalOrden = totalOrden;
        this.estadoOrden = estadoOrden;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFechaOrden() {
        return fechaOrden;
    }

    public void setFechaOrden(LocalDateTime fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public BigDecimal getTotalOrden() {
        return totalOrden;
    }

    public void setTotalOrden(BigDecimal totalOrden) {
        this.totalOrden = totalOrden;
    }

    public String getEstadoOrden() {
        return estadoOrden;
    }

    public void setEstadoOrden(String estadoOrden) {
        this.estadoOrden = estadoOrden;
    }

    @Override
    public String toString() {
        return idOrden + " | " + idUsuario + " | " + fechaOrden + " | " + totalOrden + " | " + estadoOrden;
    }
}