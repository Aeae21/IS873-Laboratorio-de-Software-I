/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Modelo.Productos;

import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author diego
 */

// Queries for the PRODUCTO table
@Repository
public class ProductoBD {

    private final JdbcTemplate bd;

    // Product + the name of its type
    private static final String CONSULTA = "SELECT p.*, t.tipo_producto AS nombre_tipo "
            + "FROM producto p JOIN tipo_producto t ON t.id = p.tipo_producto_id ";

    public ProductoBD(JdbcTemplate bd) {
        this.bd = bd;
    }

    public List<Producto> listar(int pagina) {
        int desde = Math.max(pagina - 1, 0) * 20;   // RNF-04: 20 records per page
        return bd.query(CONSULTA + "ORDER BY p.sku LIMIT 20 OFFSET ?", new BeanPropertyRowMapper<>(Producto.class), desde);
    }

    public Producto buscarPorId(int id) {
        return bd.queryForObject(CONSULTA + "WHERE p.id = ?", new BeanPropertyRowMapper<>(Producto.class), id);
    }

    // MSG-06: another product with the same SKU
    public boolean existeOtro(String sku, Integer id) {
        int idActual = (id == null) ? 0 : id;
        Integer total = bd.queryForObject("SELECT COUNT(*) FROM producto WHERE LOWER(sku) = LOWER(?) AND id <> ?",
                Integer.class, sku, idActual);
        return total > 0;
    }

    // Stock is not changed here: it starts at 0 and changes with the movements (Sprint 2)
    public void guardar(Producto p) {
        if (p.getId() == null) {
            bd.update("INSERT INTO producto (sku, producto, descripcion, tipo_producto_id, precio_venta, existencias, estado) "
                    + "VALUES (?, ?, ?, ?, ?, 0, ?)",
                    p.getSku(), p.getProducto(), p.getDescripcion(), p.getTipoProductoId(), p.getPrecioVenta(), p.getEstado());
        } else {
            bd.update("UPDATE producto SET sku = ?, producto = ?, descripcion = ?, tipo_producto_id = ?, "
                    + "precio_venta = ?, estado = ? WHERE id = ?",
                    p.getSku(), p.getProducto(), p.getDescripcion(), p.getTipoProductoId(), p.getPrecioVenta(),
                    p.getEstado(), p.getId());
        }
    }

    public void eliminar(int id) {
        bd.update("DELETE FROM producto WHERE id = ?", id);
    }

    // A product with movements cannot be deleted (MSG-18)
    public boolean tieneMovimientos(int id) {
        Integer total = bd.queryForObject("SELECT COUNT(*) FROM movimiento_inventario WHERE producto_id = ?",
                Integer.class, id);
        return total > 0;
    }

    // HU-06: stock query with filters. An empty filter is not used.
    // Entries and exits: CONFIRMADO movements. In process: EN PROCESO movements.
    public List<Producto> consultarExistencias(String sku, String producto, String tipo, String estado, int pagina) {
        int desde = Math.max(pagina - 1, 0) * 20;   // RNF-04: 20 records per page
        String sql = "SELECT p.*, t.tipo_producto AS nombre_tipo, "
                + "(SELECT COALESCE(SUM(cantidad), 0) FROM movimiento_inventario m "
                + "   WHERE m.producto_id = p.id AND m.tipo = 'ENTRADA' AND m.estado = 'CONFIRMADO') AS entradas, "
                + "(SELECT COALESCE(SUM(cantidad), 0) FROM movimiento_inventario m "
                + "   WHERE m.producto_id = p.id AND m.tipo = 'SALIDA' AND m.estado = 'CONFIRMADO') AS salidas, "
                + "(SELECT COALESCE(SUM(cantidad), 0) FROM movimiento_inventario m "
                + "   WHERE m.producto_id = p.id AND m.estado = 'EN PROCESO') AS en_proceso "
                + "FROM producto p JOIN tipo_producto t ON t.id = p.tipo_producto_id "
                + "WHERE LOWER(p.sku) LIKE LOWER(?) AND LOWER(p.producto) LIKE LOWER(?) "
                + "AND (? = '' OR t.tipo_producto = ?) AND (? = '' OR p.estado = ?) "
                + "ORDER BY p.sku LIMIT 20 OFFSET ?";
        return bd.query(sql, new BeanPropertyRowMapper<>(Producto.class),
                "%" + sku + "%", "%" + producto + "%", tipo, tipo, estado, estado, desde);
    }
}
