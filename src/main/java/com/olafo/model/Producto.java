package com.olafo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa una fila de la tabla producto_terminado junto con su categoría.
 */
public class Producto {
    // Atributos privados
    private int idProducto;
    private Categoria categoria;
    private String nombre;
    private String descripcion;
    private BigDecimal precioVenta;
    private int stockActual;
    private boolean activo;
    private LocalDate fechaCreacion;

    // Constructor vacío
    public Producto() {
    }

    // Constructor con parámetros
    public Producto(int idProducto, Categoria categoria, String nombre,
            String descripcion, BigDecimal precioVenta, int stockActual,
            boolean activo, LocalDate fechaCreacion) {
        this.idProducto = idProducto;
        this.categoria = categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", precioVenta=" + precioVenta +
                ", stockActual=" + stockActual +
                ", categoria=" +
                (categoria != null ? categoria.getNombre() : "Sin categoría") +
                ", activo=" + activo +
                '}';
    }
}
