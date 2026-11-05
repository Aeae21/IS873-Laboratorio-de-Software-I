/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Controlador.Usuarios;

import com.diegoamaya.sif.Modelo.Usuarios.Usuario;
import com.diegoamaya.sif.Modelo.Usuarios.UsuarioBD;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author diego
 */
// HU-03: Manage users (only the Administrador)
@Controller
public class UsuarioControlador {

    private final UsuarioBD usuarios;
    private final PasswordEncoder cifrador;

    public UsuarioControlador(UsuarioBD usuarios, PasswordEncoder cifrador) {
        this.usuarios = usuarios;
        this.cifrador = cifrador;
    }

    @GetMapping("/usuarios")
    public String listar(@RequestParam(defaultValue = "1") int pagina, Model model) {
        model.addAttribute("usuarios", usuarios.listar(pagina));
        model.addAttribute("pagina", pagina);
        return "Vista/Usuarios/lista";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "Vista/Usuarios/formulario";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editar(@PathVariable int id, Model model) {
        Usuario usuario = usuarios.buscarPorId(id);
        usuario.setContrasena("");    // the password is never shown (RNF-01)
        model.addAttribute("usuario", usuario);
        return "Vista/Usuarios/formulario";
    }

    @PostMapping("/usuarios/guardar")
    public String guardar(Usuario usuario, @SessionAttribute("usuario") Usuario administrador, Model model,
            RedirectAttributes redirect) {
        String error = validar(usuario, administrador);
        if (error != null) {
            model.addAttribute("error", error);
            return "Vista/Usuarios/formulario";
        }
        if (usuario.getContrasena().isBlank()) {
            // When editing without a new password, it keeps the old one
            usuario.setContrasena(usuarios.buscarPorId(usuario.getId()).getContrasena());
        } else {
            usuario.setContrasena(cifrador.encode(usuario.getContrasena()));   // RNF-01
        }
        usuarios.guardar(usuario);
        redirect.addFlashAttribute("mensaje", "Registro guardado correctamente.");   // MSG-20
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminar(@PathVariable int id, @SessionAttribute("usuario") Usuario administrador,
            RedirectAttributes redirect) {
        if (id == administrador.getId()) {
            // The Administrador cannot delete their own user
            redirect.addFlashAttribute("error", "No tiene permiso para realizar esta operación.");   // MSG-04
        } else if (usuarios.tieneMovimientos(id)) {
            redirect.addFlashAttribute("error",
                    "No se puede eliminar porque tiene registros asociados. Puede deshabilitarlo.");   // MSG-18
        } else {
            usuarios.eliminar(id);
        }
        return "redirect:/usuarios";
    }

    // Checks the form data. Returns the error message, or null if everything is OK.
    private String validar(Usuario u, Usuario administrador) {
        // MSG-05: required fields
        if (u.getCedula().isBlank()) return "Debe diligenciar el campo Cédula.";
        if (u.getNombreCompleto().isBlank()) return "Debe diligenciar el campo Nombre completo.";
        if (u.getNombreUsuario().isBlank()) return "Debe diligenciar el campo Nombre de usuario.";
        if (u.getCorreo().isBlank()) return "Debe diligenciar el campo Correo.";
        if (u.getId() == null && u.getContrasena().isBlank()) return "Debe diligenciar el campo Contraseña.";

        // MSG-24: password with at least 8 characters
        if (!u.getContrasena().isBlank() && u.getContrasena().length() < 8) {
            return "El formato del campo Contraseña no es válido.";
        }

        // MSG-06: cedula, user name and email cannot be repeated
        if (usuarios.existeOtro("cedula", u.getCedula(), u.getId())) return "Ya existe un registro con el mismo Cédula.";
        if (usuarios.existeOtro("nombre_usuario", u.getNombreUsuario(), u.getId())) return "Ya existe un registro con el mismo Nombre de usuario.";
        if (usuarios.existeOtro("correo", u.getCorreo(), u.getId())) return "Ya existe un registro con el mismo Correo.";

        // MSG-25: the Administrador cannot disable their own user
        if (u.getEstado().equals("INACTIVO") && administrador.getId().equals(u.getId())) {
            return "No puede deshabilitar su propio usuario.";
        }
        return null;
    }
}
