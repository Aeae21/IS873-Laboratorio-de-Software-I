/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Modelo.Productos;

/**
 *
 * @author diego
 */

// Data of a product (PRODUCTO table)
public class Producto {

    private Integer id;
    private String sku;
    private String producto;
    private String descripcion;
    private Integer tipoProductoId;
    private String nombreTipo;          // name of the type, it comes with a JOIN to show it
    private Integer precioVenta;
    private Integer existencias = 0;    // a new product starts at 0
    private String estado = "ACTIVO";

    // Only for the stock query (HU-06); they are calculated with the movements table
    private Integer entradas;
    private Integer salidas;
    private Integer enProceso;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getTipoProductoId() { return tipoProductoId; }
    public void setTipoProductoId(Integer tipoProductoId) { this.tipoProductoId = tipoProductoId; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public Integer getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Integer precioVenta) { this.precioVenta = precioVenta; }

    public Integer getExistencias() { return existencias; }
    public void setExistencias(Integer existencias) { this.existencias = existencias; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getEntradas() { return entradas; }
    public void setEntradas(Integer entradas) { this.entradas = entradas; }

    public Integer getSalidas() { return salidas; }
    public void setSalidas(Integer salidas) { this.salidas = salidas; }

    public Integer getEnProceso() { return enProceso; }
    public void setEnProceso(Integer enProceso) { this.enProceso = enProceso; }
}
