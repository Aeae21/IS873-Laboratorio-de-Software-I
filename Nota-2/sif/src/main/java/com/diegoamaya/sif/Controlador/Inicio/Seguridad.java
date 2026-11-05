/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.diegoamaya.sif.Controlador.Inicio;

import com.diegoamaya.sif.Modelo.Usuarios.Usuario;
import com.diegoamaya.sif.Modelo.Usuarios.UsuarioBD;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author diego
 */

// HU-02: login and permissions by role with Spring Security (CU-01 and CU-S04)
@Configuration
public class Seguridad {

    // RNF-01: the passwords are saved encrypted with BCrypt
    @Bean
    public PasswordEncoder cifrador() {
        return new BCryptPasswordEncoder();
    }

    // Spring Security looks for the user in the USUARIO table
    @Bean
    public UserDetailsService buscarUsuario(UsuarioBD usuarios) {
        return nombreUsuario -> {
            Usuario u = usuarios.buscarPorNombreUsuario(nombreUsuario);
            if (u == null) {
                throw new UsernameNotFoundException(nombreUsuario);      // MSG-01
            }
            return User.withUsername(u.getNombreUsuario())
                    .password(u.getContrasena())
                    .roles(u.getRol())                                    // ADMINISTRADOR or VENDEDOR
                    .disabled(u.getEstado().equals("INACTIVO"))           // MSG-02
                    .build();
        };
    }

    // RNF-03: what each role can open (permissions matrix of Nota 1)
    @Bean
    public SecurityFilterChain reglas(HttpSecurity http, UsuarioBD usuarios) throws Exception {
        http.authorizeHttpRequests(permisos -> permisos
                .requestMatchers("/login").permitAll()
                .requestMatchers("/usuarios/**", "/tipos/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/productos/nuevo", "/productos/editar/*", "/productos/guardar",
                        "/productos/eliminar/*").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated());     // the rest: Administrador and Vendedor

        http.formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("nombreUsuario")
                .passwordParameter("contrasena")
                // Data is correct: save the user in the session (name and role at the top) and go to the menu
                .successHandler((request, response, datos) -> {
                    request.getSession().setAttribute("usuario", usuarios.buscarPorNombreUsuario(datos.getName()));
                    response.sendRedirect("/");
                })
                // Data is not correct: CU-01 exceptions, in the same order as the use case
                .failureHandler((request, response, error) -> {
                    if (request.getParameter("nombreUsuario").isBlank()) {
                        response.sendRedirect("/login?vacioUsuario");     // MSG-05
                    } else if (request.getParameter("contrasena").isBlank()) {
                        response.sendRedirect("/login?vacioContrasena");  // MSG-05
                    } else if (error instanceof DisabledException) {
                        response.sendRedirect("/login?inactivo");         // MSG-02
                    } else {
                        response.sendRedirect("/login?error");            // MSG-01
                    }
                }));

        http.logout(salir -> salir
                .logoutUrl("/salir")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID"));

        // MSG-04: the Vendedor tries to open a screen of the Administrador
        http.exceptionHandling(e -> e.accessDeniedHandler((request, response, error) -> response.sendRedirect("/?sinPermiso")));

        // MSG-03: the session was closed after 1 hour without activity (RNF-02)
        http.sessionManagement(sesion -> sesion.invalidSessionUrl("/login?expirado"));

        return http.build();
    }
}

