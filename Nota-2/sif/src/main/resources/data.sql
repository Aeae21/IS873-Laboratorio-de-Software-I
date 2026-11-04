/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  diego
 * Created: 3 nov 2026
 */

-- Test data for the demo.
-- "ON CONFLICT DO NOTHING" avoids repeating the data every time the program starts.

-- Users. There is no self sign-up, so the first Administrador is created here.
-- Passwords: admin123 and vendedor123 (saved encrypted with BCrypt)
INSERT INTO usuario (cedula, nombre_completo, nombre_usuario, correo, contrasena, rol, estado) VALUES
('1001', 'Diego Amaya', 'admin', 'admin@acme.com',
 '$2a$10$ZAEYSZ2UmBwlLwx8E8Czs.w7/cVhZlbT74mitpgaUGgsDS4fJfyC6', 'ADMINISTRADOR', 'ACTIVO'),
('1002', 'Pedro Picapiedra', 'vendedor1', 'vendedor1@acme.com',
 '$2a$10$LJtw51wnfSa3swxs7lWdD.HDVkRVdHeS5nZTlqaR2d.Z97ibgj.Oi', 'VENDEDOR', 'ACTIVO')
ON CONFLICT DO NOTHING;

-- Product types
INSERT INTO tipo_producto (tipo_producto, descripcion, estado) VALUES
('Herramientas', 'Herramientas pesadas', 'ACTIVO'),
('Transporte', 'Vehículos y artefactos para moverse', 'ACTIVO'),
('Trampas', 'Trampas para atrapar correcaminos', 'ACTIVO')
ON CONFLICT DO NOTHING;

-- Products (each one looks for the id of its type; if the type does not exist, it is not inserted)
INSERT INTO producto (sku, producto, descripcion, tipo_producto_id, precio_venta, existencias, estado)
SELECT 'ACME-YUN-001', 'Yunque 500 kg', 'Yunque de hierro', id, 350000, 12, 'ACTIVO'
FROM tipo_producto WHERE tipo_producto = 'Herramientas' ON CONFLICT DO NOTHING;

INSERT INTO producto (sku, producto, descripcion, tipo_producto_id, precio_venta, existencias, estado)
SELECT 'ACME-PAT-010', 'Patines cohete', 'Patines con propulsión', id, 1200000, 8, 'ACTIVO'
FROM tipo_producto WHERE tipo_producto = 'Transporte' ON CONFLICT DO NOTHING;

INSERT INTO producto (sku, producto, descripcion, tipo_producto_id, precio_venta, existencias, estado)
SELECT 'ACME-AGU-003', 'Agujero portátil', 'Agujero que se pega en cualquier superficie', id, 90000, 0, 'ACTIVO'
FROM tipo_producto WHERE tipo_producto = 'Trampas' ON CONFLICT DO NOTHING;

-- Example movements (stock = confirmed entries - confirmed exits).
-- They are registered in the system in Sprint 2; here there are only a few to test the query.
INSERT INTO movimiento_inventario (numero, producto_id, tipo, cantidad, motivo, estado, origen, usuario_id)
SELECT 'MOV-000001', p.id, 'ENTRADA', 12, 'Compra a proveedor', 'CONFIRMADO', 'MANUAL', u.id
FROM producto p, usuario u WHERE p.sku = 'ACME-YUN-001' AND u.nombre_usuario = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO movimiento_inventario (numero, producto_id, tipo, cantidad, motivo, estado, origen, usuario_id)
SELECT 'MOV-000002', p.id, 'ENTRADA', 10, 'Compra a proveedor', 'CONFIRMADO', 'MANUAL', u.id
FROM producto p, usuario u WHERE p.sku = 'ACME-PAT-010' AND u.nombre_usuario = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO movimiento_inventario (numero, producto_id, tipo, cantidad, motivo, estado, origen, usuario_id)
SELECT 'MOV-000003', p.id, 'SALIDA', 2, 'Producto dañado', 'CONFIRMADO', 'MANUAL', u.id
FROM producto p, usuario u WHERE p.sku = 'ACME-PAT-010' AND u.nombre_usuario = 'admin' ON CONFLICT DO NOTHING;

INSERT INTO movimiento_inventario (numero, producto_id, tipo, cantidad, motivo, estado, origen, usuario_id)
SELECT 'MOV-000004', p.id, 'ENTRADA', 50, 'Compra a proveedor', 'EN PROCESO', 'MANUAL', u.id
FROM producto p, usuario u WHERE p.sku = 'ACME-YUN-001' AND u.nombre_usuario = 'admin' ON CONFLICT DO NOTHING;
