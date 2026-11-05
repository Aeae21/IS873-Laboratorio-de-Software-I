/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Controlador.Productos;

import com.diegoamaya.sif.Modelo.Productos.Producto;
import com.diegoamaya.sif.Modelo.Productos.ProductoBD;
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
// HU-05: Manage products (Administrador). The Vendedor can only see the list.
// On the screen the product is called "item" because there is already a field called "producto".
@Controller
public class ProductoControlador {

    private final ProductoBD productos;
    private final TipoProductoBD tipos;

    public ProductoControlador(ProductoBD productos, TipoProductoBD tipos) {
        this.productos = productos;
        this.tipos = tipos;
    }

    // Administrador and Vendedor
    @GetMapping("/productos")
    public String listar(@RequestParam(defaultValue = "1") int pagina, Model model) {
        model.addAttribute("productos", productos.listar(pagina));
        model.addAttribute("pagina", pagina);
        return "Vista/Productos/lista";
    }

    @GetMapping("/productos/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("item", new Producto());
        model.addAttribute("tipos", tipos.listar());
        return "Vista/Productos/formulario";
    }

    @GetMapping("/productos/editar/{id}")
    public String editar(@PathVariable int id, Model model) {
        model.addAttribute("item", productos.buscarPorId(id));
        model.addAttribute("tipos", tipos.listar());
        return "Vista/Productos/formulario";
    }

    @PostMapping("/productos/guardar")
    public String guardar(@ModelAttribute("item") Producto p, Model model, RedirectAttributes redirect) {
        String error = null;
        if (p.getSku().isBlank()) {
            error = "Debe diligenciar el campo SKU.";                                   // MSG-05
        } else if (p.getProducto().isBlank()) {
            error = "Debe diligenciar el campo Producto.";
        } else if (p.getTipoProductoId() == null) {
            error = "Debe diligenciar el campo Tipo de producto.";
        } else if (p.getPrecioVenta() == null || p.getPrecioVenta() <= 0) {
            error = "El precio de venta debe ser mayor a cero.";                         // MSG-07
        } else if (productos.existeOtro(p.getSku(), p.getId())) {
            error = "Ya existe un registro con el mismo SKU.";                          // MSG-06
        } else {
            TipoProducto tipo = tipos.buscarPorId(p.getTipoProductoId());
            if (tipo.getEstado().equals("INACTIVO")) {
                error = "El tipo de producto está inactivo y no se puede usar.";         // MSG-10
            }
        }
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("tipos", tipos.listar());
            return "Vista/Productos/formulario";
        }
        productos.guardar(p);
        redirect.addFlashAttribute("mensaje", "Registro guardado correctamente.");      // MSG-20
        return "redirect:/productos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {

        if (productos.tieneMovimientos(id)) {
            redirect.addFlashAttribute("error",
                    "No se puede eliminar porque tiene registros asociados. Puede deshabilitarlo.");   // MSG-18
        } else {
            productos.eliminar(id);
        }
        return "redirect:/productos";
    }
}