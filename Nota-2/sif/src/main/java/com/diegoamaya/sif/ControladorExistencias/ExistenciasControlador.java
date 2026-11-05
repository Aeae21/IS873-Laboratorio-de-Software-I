/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.ControladorExistencias;

import com.diegoamaya.sif.Modelo.Productos.ProductoBD;
import com.diegoamaya.sif.Modelo.TiposProducto.TipoProductoBD;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author diego
 */
// HU-06: Check products and stock (Administrador and Vendedor)
@Controller
public class ExistenciasControlador {

    private final ProductoBD productos;
    private final TipoProductoBD tipos;

    public ExistenciasControlador(ProductoBD productos, TipoProductoBD tipos) {
        this.productos = productos;
        this.tipos = tipos;
    }

    // Filters by SKU, product, type and status (RF-05)
    @GetMapping("/existencias")
    public String consultar(@RequestParam(defaultValue = "") String sku,
            @RequestParam(defaultValue = "") String producto,
            @RequestParam(defaultValue = "") String tipo,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {
        model.addAttribute("productos", productos.consultarExistencias(sku, producto, tipo, estado, pagina));
        model.addAttribute("tipos", tipos.listar());
        // The filters go back to the screen, so they stay written and are used when changing page
        model.addAttribute("sku", sku);
        model.addAttribute("producto", producto);
        model.addAttribute("tipo", tipo);
        model.addAttribute("estado", estado);
        model.addAttribute("pagina", pagina);
        return "Vista/Existencias/lista";
    }
}