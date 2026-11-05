/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Modelo.TiposProducto;

/**
 *
 * @author diego
 */
// Data of a product type (TIPO_PRODUCTO table)
public class TipoProducto {

    private Integer id;
    private String tipoProducto;
    private String descripcion;
    private String estado = "ACTIVO";

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(String tipoProducto) { this.tipoProducto = tipoProducto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}