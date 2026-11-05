/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Modelo.TiposProducto;

import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


/**
 *
 * @author diego
 */
// Queries for the TIPO_PRODUCTO table
@Repository
public class TipoProductoBD {

    private final JdbcTemplate bd;

    public TipoProductoBD(JdbcTemplate bd) {
        this.bd = bd;
    }

    // All the types, for the lists to choose a type
    public List<TipoProducto> listar() {
        return bd.query("SELECT * FROM tipo_producto ORDER BY tipo_producto",
                new BeanPropertyRowMapper<>(TipoProducto.class));
    }

    // One page of types, for the screen of types
    public List<TipoProducto> listar(int pagina) {
        int desde = Math.max(pagina - 1, 0) * 20;   // RNF-04: 20 records per page
        return bd.query("SELECT * FROM tipo_producto ORDER BY tipo_producto LIMIT 20 OFFSET ?",
                new BeanPropertyRowMapper<>(TipoProducto.class), desde);
    }

    public TipoProducto buscarPorId(int id) {
        return bd.queryForObject("SELECT * FROM tipo_producto WHERE id = ?",
                new BeanPropertyRowMapper<>(TipoProducto.class), id);
    }

    // MSG-06: another type with the same name
    public boolean existeOtro(String nombre, Integer id) {
        int idActual = (id == null) ? 0 : id;
        Integer total = bd.queryForObject("SELECT COUNT(*) FROM tipo_producto WHERE LOWER(tipo_producto) = LOWER(?) AND id <> ?",
                Integer.class, nombre, idActual);
        return total > 0;
    }

    public void guardar(TipoProducto t) {
        if (t.getId() == null) {
            bd.update("INSERT INTO tipo_producto (tipo_producto, descripcion, estado) VALUES (?, ?, ?)",
                    t.getTipoProducto(), t.getDescripcion(), t.getEstado());
        } else {
            bd.update("UPDATE tipo_producto SET tipo_producto = ?, descripcion = ?, estado = ? WHERE id = ?",
                    t.getTipoProducto(), t.getDescripcion(), t.getEstado(), t.getId());
        }
    }

    public void eliminar(int id) {
        bd.update("DELETE FROM tipo_producto WHERE id = ?", id);
    }

    // A type with products cannot be deleted (MSG-18)
    public boolean tieneProductos(int id) {
        Integer total = bd.queryForObject("SELECT COUNT(*) FROM producto WHERE tipo_producto_id = ?",
                Integer.class, id);
        return total > 0;
    }
}