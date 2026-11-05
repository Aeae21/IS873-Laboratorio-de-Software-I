/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Controlador.Inicio;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author diego
 */

// HU-02: login screen and main menu.
// Spring Security checks the user and the password (see Seguridad.java).
@Controller
public class InicioControlador {

    // Login screen (mockup from Nota 1)
    @GetMapping("/login")
    public String login() {
        return "Vista/Inicio/login";
    }

    // Main menu: the options change by role (see plantilla.html)
    @GetMapping("/")
    public String inicio() {
        return "Vista/Inicio/inicio";
    }
}
