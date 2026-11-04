/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  diego
 * Created: 3 nov 2026
 */

-- Tables for Sprint 1 (entity relationship model from Nota 1).
-- "IF NOT EXISTS" keeps the data every time the program starts.

CREATE TABLE IF NOT EXISTS usuario (
    id              SERIAL PRIMARY KEY,
    cedula          VARCHAR(20)  NOT NULL UNIQUE,
    nombre_completo VARCHAR(100) NOT NULL,
    nombre_usuario  VARCHAR(50)  NOT NULL UNIQUE,
    correo          VARCHAR(100) NOT NULL UNIQUE,
    contrasena      VARCHAR(100) NOT NULL,          -- encrypted with BCrypt (RNF-01)
    rol             VARCHAR(20)  NOT NULL,          -- ADMINISTRADOR or VENDEDOR
    estado          VARCHAR(10)  NOT NULL DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS tipo_producto (
    id            SERIAL PRIMARY KEY,
    tipo_producto VARCHAR(50)  NOT NULL UNIQUE,
    descripcion   VARCHAR(200),
    estado        VARCHAR(10)  NOT NULL DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS producto (
    id               SERIAL PRIMARY KEY,
    sku              VARCHAR(20)  NOT NULL UNIQUE,
    producto         VARCHAR(100) NOT NULL,
    descripcion      VARCHAR(200),
    tipo_producto_id INTEGER      NOT NULL REFERENCES tipo_producto(id),
    precio_venta     INTEGER      NOT NULL CHECK (precio_venta > 0),
    existencias      INTEGER      NOT NULL DEFAULT 0 CHECK (existencias >= 0),   -- RNF-06
    estado           VARCHAR(10)  NOT NULL DEFAULT 'ACTIVO'
);

-- Movements are registered in Sprint 2 (HU-07).
-- In Sprint 1 they are only read to show entries, exits and in process (HU-06).
-- venta_id and nota_debito_id are added in Sprint 2 and 3, when the VENTA and NOTA_DEBITO tables exist.
CREATE TABLE IF NOT EXISTS movimiento_inventario (
    id          SERIAL PRIMARY KEY,
    numero      VARCHAR(20)  NOT NULL UNIQUE,      -- MOV-000001
    producto_id INTEGER      NOT NULL REFERENCES producto(id),
    tipo        VARCHAR(10)  NOT NULL,              -- ENTRADA or SALIDA
    cantidad    INTEGER      NOT NULL CHECK (cantidad > 0),
    motivo      VARCHAR(100) NOT NULL,
    observacion VARCHAR(200),
    estado      VARCHAR(12)  NOT NULL,              -- EN PROCESO, CONFIRMADO or ANULADO
    origen      VARCHAR(12)  NOT NULL,              -- MANUAL, VENTA or NOTA DEBITO
    usuario_id  INTEGER      NOT NULL REFERENCES usuario(id),
    fecha       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
