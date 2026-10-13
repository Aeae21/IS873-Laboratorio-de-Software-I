# Sistema de Inventarios y Facturación – ACME Corporation

**Autor:** Diego Amaya  
**Asignaturas:** IS873 Laboratorio de Software · IS953 Administración de Proyectos de Software  
**Universidad Tecnológica de Pereira**

---

## Documentación

| Documento | Enlace |
|---|---|
| Planteamiento del proyecto, requerimientos, casos de uso, MoSCoW y diseño | [PDF – Sistema de Inventarios y Facturación](https://drive.google.com/file/d/1y_59eI5kgEGdb9yR6bzz1Of0dkrWwxMs/view?usp=drive_link) |
| Requerimientos, casos de uso detallados y backlog | [Excel – Sistema de Inventarios y Facturación](https://docs.google.com/spreadsheets/d/1PX9YPwMidkAtZ8XLiIKNp_MuMKh0TXYU/edit?usp=drive_link&ouid=112128559464804222856&rtpof=true&sd=true) |

---

## Prueba de concepto

### Supuesto técnico más riesgoso

> "Al emitir una factura, el sistema descuenta bien las existencias, aunque dos vendedores vendan el mismo producto al mismo tiempo, sin que las existencias queden negativas."

Es el mayor riesgo porque emitir la factura (HU-10, CU-08) une la venta y el inventario en una sola operación. Los CRUD de productos, clientes y usuarios no tienen este problema.

### Qué hace

El producto **ACME-YUN-001 (Yunque 500 kg)** tiene **5 unidades**. Dos vendedores intentan vender **3 unidades cada uno al mismo tiempo**. Solo alcanza para uno.

| Prueba | Cómo funciona | Resultado |
|---|---|---|
| **1. Sin restricción** | Cada vendedor lee las existencias, calcula en Java y guarda el resultado. Los dos leen 5 y los dos guardan 2. | **Error:** se venden 6 de 5 y el sistema dice que quedan 2 (lo real sería -1). |
| **2. Con restricción** | La base de datos resta solo si hay stock suficiente, en un solo paso (`WHERE existencias >= 3`) y dentro de una transacción. El segundo vendedor espera a que el primero haga `commit`. | **Correcto:** se venden 3 de 5 y quedan 2. El inventario cuadra. |

La tabla `producto` tiene además la regla `CHECK (existencias >= 0)` como última barrera.

### Qué valida

- Con la restricción solo se factura una venta y el inventario cuadra (5 - 3 = 2).
- Las existencias nunca quedan negativas.
- Java se conecta y trabaja con PostgreSQL.

### Qué NO valida todavía

- Inicio de sesión, roles y pantallas.
- Ventas con varios productos, número de factura y nota débito.
- Publicación en Render y Clever Cloud.
- Rendimiento con muchos datos.

---

## Estructura

```
├── prueba_concepto.java      # Prueba de concepto (consola)
├── crear_tabla.sql         # Crea la tabla producto con el ACME-YUN-001
├── postgresql-42.7.4.jar    # Driver de PostgreSQL para Java
└── README.md
```

## Cómo ejecutar

### 1. Preparar la base de datos (una sola vez)

1. Tener instalados **Java 17+** y **PostgreSQL**.
2. En pgAdmin, crear la base de datos `sif_poc`.
3. Abrir el **Query Tool** sobre `sif_poc` y ejecutar el archivo `crear_tabla.sql`.

### 2. Configurar la clave

En `prueba_concepto.java`, cambiar la clave por la de su PostgreSQL:

```java
static String clave = "TU_CLAVE";
```

### 3. Ejecutar desde CMD

1. Descargar en una misma carpeta `prueba_concepto.java` y `postgresql-42.7.4.jar`.
2. Abrir el CMD en esa carpeta (escribir `cmd` en la barra de dirección del explorador y presionar Enter).
3. Ejecutar:

```
java -cp postgresql-42.7.4.jar prueba_concepto.java
```

> [!IMPORTANT]
> El `-cp postgresql-42.7.4.jar` es obligatorio: le indica a Java dónde está el driver de PostgreSQL. Si se ejecuta solo `java prueba_concepto.java`, aparece el error `No suitable driver found`.

## Resultado esperado

```
PRUEBA 1: Sin restriccion
Vendedor 1: FACTURADA, 3 unidades
Vendedor 2: FACTURADA, 3 unidades
Vendidas: 6 de 5 | Existencias en el sistema: 2 | Deberian quedar: -1

PRUEBA 2: Con restriccion
Vendedor 1: FACTURADA, 3 unidades
Vendedor 2: RECHAZADA, no hay existencias suficientes
Vendidas: 3 de 5 | Existencias en el sistema: 2 | Deberian quedar: 2
```

> [!NOTE]
> En la Prueba 2, el vendedor que factura puede ser el 1 o el 2, según quién llegue primero. Lo importante es que solo uno factura.
