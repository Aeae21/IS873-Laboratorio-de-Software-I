import java.sql.*;

public class prueba_concepto {

    static String url = "jdbc:postgresql://localhost:5432/sif_poc";
    static String user = "postgres";
    static String password = "password"; // cambiar por la contraseña de PostgreSQL
    static int vendidas = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("PRUEBA 1: Sin restriccion");
        venderAlMismoTiempo(false);

        System.out.println("\nPRUEBA 2: Con restriccion");
        venderAlMismoTiempo(true);
    }

    // Cada vendedor vende al mismo tiempo
    static void venderAlMismoTiempo(boolean conProteccion) throws Exception {
        ejecutar("UPDATE producto SET existencias = 5 WHERE sku = 'ACME-YUN-001'"); // cada prueba inicia con 5
        vendidas = 0;
        Thread vendedor_1 = new Thread(() -> vender("Vendedor 1", conProteccion));
        Thread vendedor_2 = new Thread(() -> vender("Vendedor 2", conProteccion));
        vendedor_1.start();
        vendedor_2.start();
        vendedor_1.join();
        vendedor_2.join();
        System.out.println("Vendidas: " + vendidas + " de 5 | Existencias en el sistema: " + existencias()
                + " | Deberian quedar: " + (5 - vendidas));
    }

    static void vender(String vendedor, boolean conProteccion) {
        try (Connection conexion_bd = DriverManager.getConnection(url, user, password)) {
            conexion_bd.setAutoCommit(false); // Se guarda todo o nada
            Statement st = conexion_bd.createStatement();

            if (!conProteccion) {
                // Lee las existencias, espera y guarda un valor calculado con lo que leyo.
                // Los dos vendedores leen 5 al mismo tiempo y los dos guardan 5 - 3 = 2.
                ResultSet rs = st.executeQuery("SELECT existencias FROM producto WHERE sku = 'ACME-YUN-001'");
                rs.next();
                int existencias = rs.getInt(1);
                Thread.sleep(1000); // el vendedor se demora; el otro lee el mismo valor
                if (existencias < 3) {
                    conexion_bd.rollback();
                    System.out.println(vendedor + ": RECHAZADA, no hay existencias suficientes");
                    return;
                }
                st.executeUpdate("UPDATE producto SET existencias = " + (existencias - 3) + " WHERE sku = 'ACME-YUN-001'");
            } else {
                // La base de datos resta solo si hay stock suficiente, en un solo paso.
                // El segundo vendedor espera a que el primero termine; cuando entra ya quedan 2.
                int filas = st.executeUpdate("UPDATE producto SET existencias = existencias - 3"
                        + " WHERE sku = 'ACME-YUN-001' AND existencias >= 3");
                if (filas == 0) {
                    conexion_bd.rollback();
                    System.out.println(vendedor + ": RECHAZADA, no hay existencias suficientes");
                    return;
                }
                Thread.sleep(1000); // el vendedor se demora; el otro espera su turno
            }

            conexion_bd.commit();
            synchronized (prueba_concepto.class) { vendidas += 3; } // suma segura entre las dos ventas
            System.out.println(vendedor + ": FACTURADA, 3 unidades");
        } catch (Exception e) {
            System.out.println(vendedor + ": ERROR " + e.getMessage());
        }
    }

    static void ejecutar(String sql) throws SQLException {
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            con.createStatement().executeUpdate(sql);
        }
    }

    static int existencias() throws SQLException {
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            ResultSet rs = con.createStatement().executeQuery("SELECT existencias FROM producto WHERE sku = 'ACME-YUN-001'");
            rs.next();
            return rs.getInt(1);
        }
    }
}