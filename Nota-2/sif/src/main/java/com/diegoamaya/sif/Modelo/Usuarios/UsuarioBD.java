/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Modelo.Usuarios;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author diego
 */

// Queries for the USUARIO table.
// BeanPropertyRowMapper changes each row into a Usuario object (nombre_completo -> nombreCompleto).
@Repository
public class UsuarioBD {

    private final JdbcTemplate bd;

    public UsuarioBD(JdbcTemplate bd) {
        this.bd = bd;
    }

    public List<Usuario> listar(int pagina) {
        int desde = Math.max(pagina - 1, 0) * 20;   // RNF-04: 20 records per page
        return bd.query("SELECT * FROM usuario ORDER BY id LIMIT 20 OFFSET ?",
                new BeanPropertyRowMapper<>(Usuario.class), desde);
    }

    public Usuario buscarPorId(int id) {
        return bd.queryForObject("SELECT * FROM usuario WHERE id = ?",
                new BeanPropertyRowMapper<>(Usuario.class), id);
    }

    // Returns null if it does not exist
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        List<Usuario> lista = bd.query("SELECT * FROM usuario WHERE nombre_usuario = ?",
                new BeanPropertyRowMapper<>(Usuario.class), nombreUsuario);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // Counts how many OTHER users have this value in the column (for MSG-06)
    public boolean existeOtro(String columna, String valor, Integer id) {
        int idActual = (id == null) ? 0 : id;
        Integer total = bd.queryForObject("SELECT COUNT(*) FROM usuario WHERE LOWER(" + columna + ") = LOWER(?) AND id <> ?",
                Integer.class, valor, idActual);
        return total > 0;
    }

    public void guardar(Usuario u) {
        if (u.getId() == null) {
            bd.update("INSERT INTO usuario (cedula, nombre_completo, nombre_usuario, correo, contrasena, rol, estado) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    u.getCedula(), u.getNombreCompleto(), u.getNombreUsuario(), u.getCorreo(),
                    u.getContrasena(), u.getRol(), u.getEstado());
        } else {
            bd.update("UPDATE usuario SET cedula = ?, nombre_completo = ?, nombre_usuario = ?, correo = ?, "
                    + "contrasena = ?, rol = ?, estado = ? WHERE id = ?",
                    u.getCedula(), u.getNombreCompleto(), u.getNombreUsuario(), u.getCorreo(),
                    u.getContrasena(), u.getRol(), u.getEstado(), u.getId());
        }
    }

    public void eliminar(int id) {
        bd.update("DELETE FROM usuario WHERE id = ?", id);
    }

    // A user with movements cannot be deleted (MSG-18)
    public boolean tieneMovimientos(int id) {
        Integer total = bd.queryForObject("SELECT COUNT(*) FROM movimiento_inventario WHERE usuario_id = ?",
                Integer.class, id);
        return total > 0;
    }
}
