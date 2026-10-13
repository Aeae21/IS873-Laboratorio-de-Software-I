-- Ejecutar UNA SOLA VEZ en pgAdmin, sobre la base de datos sif_poc (Query Tool)
DROP TABLE IF EXISTS movimiento_inventario, venta, producto CASCADE;

CREATE TABLE producto (
    sku          VARCHAR(20) PRIMARY KEY,
    producto     VARCHAR(100),
    precio_venta INTEGER,
    existencias  INTEGER CHECK (existencias >= 0)
);

INSERT INTO producto VALUES ('ACME-YUN-001', 'Yunque 500 kg', 350000, 5);
