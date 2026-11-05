/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Controlador.TiposProducto;

import com.diegoamaya.sif.Modelo.TiposProducto.TipoProducto;
import com.diegoamaya.sif.Modelo.TiposProducto.TipoProductoBD;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author diego
 */
// HU-04: Manage product types (only the Administrador)
@Controller
public class TipoProductoControlador {

    private final TipoProductoBD tipos;

    public TipoProductoControlador(TipoProductoBD tipos) {
        this.tipos = tipos;
    }

    @GetMapping("/tipos")
    public String listar(@RequestParam(defaultValue = "1") int pagina, Model model) {
        model.addAttribute("tipos", tipos.listar(pagina));
        model.addAttribute("pagina", pagina);
        return "Vista/TiposProducto/lista";
    }

    @GetMapping("/tipos/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("tipo", new TipoProducto());
        return "Vista/TiposProducto/formulario";
    }

    @GetMapping("/tipos/editar/{id}")
    public String editar(@PathVariable int id, Model model) {
        model.addAttribute("tipo", tipos.buscarPorId(id));
        return "Vista/TiposProducto/formulario";
    }

    @PostMapping("/tipos/guardar")
    public String guardar(@ModelAttribute("tipo") TipoProducto tipo, Model model, RedirectAttributes redirect) {
        String error = null;
        if (tipo.getTipoProducto().isBlank()) {
            error = "Debe diligenciar el campo Tipo de producto.";                       // MSG-05
        } else if (tipos.existeOtro(tipo.getTipoProducto(), tipo.getId())) {
            error = "Ya existe un registro con el mismo Tipo de producto.";              // MSG-06
        }
        if (error != null) {
            model.addAttribute("error", error);
            return "Vista/TiposProducto/formulario";
        }
        tipos.guardar(tipo);
        redirect.addFlashAttribute("mensaje", "Registro guardado correctamente.");      // MSG-20
        return "redirect:/tipos";
    }

    @PostMapping("/tipos/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {

        if (tipos.tieneProductos(id)) {
            redirect.addFlashAttribute("error",
                    "No se puede eliminar porque tiene registros asociados. Puede deshabilitarlo.");   // MSG-18
        } else {
            tipos.eliminar(id);
        }
        return "redirect:/tipos";
    }
}