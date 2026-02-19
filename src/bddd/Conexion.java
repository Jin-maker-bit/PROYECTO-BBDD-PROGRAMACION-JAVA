/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bddd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author jintae
 */
public class Conexion {

    public static Connection conn;

    public static void conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:8889/ventaslibreria";
            conn = DriverManager.getConnection(url, "root", "root");
        } catch (ClassNotFoundException | SQLException ex) {
            System.getLogger(Conexion.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public static void cerrarConexion() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.getLogger(Conexion.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public static int[] informes() {
        int[] resultados = new int[3];
        conectar();
        if (conn == null) {
            return resultados;
        }

        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM libros), "
                + "(SELECT SUM(stock) FROM libros), "
                + "(SELECT COUNT(*) FROM ventas_tienda) + (SELECT COUNT(*) FROM ventas_online)";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                resultados[0] = rs.getInt(1);
                resultados[1] = rs.getInt(2);
                resultados[2] = rs.getInt(3);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el número de ventas.\n" + e.getMessage());
        } finally {
            cerrarConexion();
        }
        return resultados;
    }

    public static ArrayList<Object[]> topTresLibros() {
        String consulta = "SELECT l.titulo, COUNT(vt.idVenta) "
                + "FROM libros l "
                + "JOIN ventas_tienda vt ON l.idLibro = vt.idLibro "
                + "GROUP BY l.idLibro, l.titulo "
                + "ORDER BY COUNT(vt.idVenta) DESC "
                + "LIMIT 3";

        ArrayList<Object[]> listaDatos = new ArrayList<>();
        conectar();

        try {
            PreparedStatement comando = conn.prepareStatement(consulta);
            ResultSet rs = comando.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[2];
                fila[0] = rs.getString(1);
                fila[1] = rs.getString(2);
                listaDatos.add(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el número de ventas en tienda.\n" + e.getMessage());
        } finally {
            cerrarConexion();
        }
        return listaDatos;
    }

    public static ArrayList<Object[]> topTresLibrosOnline() {
        String consulta = "SELECT l.titulo, COUNT(vo.idVenta) "
                + "FROM libros l "
                + "JOIN ventas_online vo ON l.idLibro = vo.idLibro "
                + "GROUP BY l.idLibro, l.titulo "
                + "ORDER BY COUNT(vo.idVenta) DESC "
                + "LIMIT 3";

        ArrayList<Object[]> listaDatos = new ArrayList<>();
        conectar();

        try {
            PreparedStatement comando = conn.prepareStatement(consulta);
            ResultSet rs = comando.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[2];
                fila[0] = rs.getString(1);
                fila[1] = rs.getString(2);
                listaDatos.add(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el número de ventas de libros Online.\n" + e.getMessage());
        } finally {
            cerrarConexion();
        }
        return listaDatos;
    }

    public static ArrayList<Object[]> topDiezEditoriales() {
        String consulta = "SELECT e.nombre, COUNT(l.idLibro) "
                + "FROM editoriales e "
                + "JOIN libros l ON e.idEditorial = l.idEditorial "
                + "GROUP BY e.idEditorial, e.nombre "
                + "ORDER BY COUNT(l.idLibro) DESC "
                + "LIMIT 10 ";

        ArrayList<Object[]> listaDatos = new ArrayList<>();
        conectar();

        try {
            PreparedStatement comando = conn.prepareStatement(consulta);
            ResultSet rs = comando.executeQuery();
            while (rs.next()) {
                Object[] fila = new Object[2];
                fila[0] = rs.getString(1);
                fila[1] = rs.getInt(2);   
                listaDatos.add(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar editoriales.\n" + e.getMessage());
        } finally {
            cerrarConexion();
        }
        return listaDatos;
    }

    public static ArrayList<Object[]> facturacionVendedoresActivos() {
        String consulta = "SELECT v.nombre, SUM(vt.precio), e.estado "
                + "FROM vendedores v "
                + "JOIN estados e ON v.idEstado = e.idEstado "
                + "JOIN ventas_tienda vt ON v.codVendedor = vt.codVendedor "
                + "WHERE e.estado = 'Activo' "
                + "GROUP BY v.codVendedor, v.nombre, e.estado "
                + "ORDER BY SUM(vt.precio) DESC";

        ArrayList<Object[]> listaDatos = new ArrayList<>();
        conectar();

        try {
            PreparedStatement comando = conn.prepareStatement(consulta);
            ResultSet rs = comando.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[3];
                fila[0] = rs.getString(1);
                fila[1] = rs.getDouble(2);
                fila[2] = rs.getString(3);
                listaDatos.add(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener facturación.\n" + e.getMessage());
        } finally {
            cerrarConexion();
        }
        return listaDatos;
    }

    public static ArrayList<Object[]> obtenerPlataformas() {

        String sql = "SELECT l.titulo, p.nombre "
                + "FROM plataformas p "
                + "JOIN ventas_online vo ON p.idPlataforma = vo.idPlataforma "
                + "JOIN libros l ON vo.idLibro = l.idLibro "
                + "ORDER BY p.nombre ASC";

        ArrayList<Object[]> lista = new ArrayList<>();
        conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[2];
                fila[0] = rs.getString(1);
                fila[1] = rs.getString(2);
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
        return lista;
    }

    public static ArrayList<Object[]> stockPorSeccion(int seccion) {
        String sql = "SELECT u.ubicacion, SUM(l.stock) "
                + "FROM ubicacion u "
                + "JOIN libros l ON u.ubicacion = l.codUbicacion "
                + "WHERE u.ubicacion LIKE ? "
                + "GROUP BY u.ubicacion, u.descripcion";

        ArrayList<Object[]> lista = new ArrayList<>();
        conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seccion + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[2];
                    fila[0] = rs.getString(1);
                    fila[1] = rs.getInt(2);
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar el informe" + e.getMessage());
        } finally {
            cerrarConexion();
        }
        return lista;
    }

    public static ArrayList<Object[]> librosPorComunidad() {
        String sql = "SELECT le.ccaa, COUNT(l.idLibro) "
                + "FROM lugar_edicion le "
                + "JOIN libros l ON le.idLugar = l.idLugar "
                + "GROUP BY le.ccaa "
                + "ORDER BY COUNT(l.idLibro) DESC";

        ArrayList<Object[]> lista = new ArrayList<>();
        conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[2];
                fila[0] = rs.getString(1);
                fila[1] = rs.getInt(2);
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error en Informe CCAA: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
        return lista;
    }

    public static ArrayList<Object[]> topCincoCiudades() {
        String sql = "SELECT le.lugar, COUNT(l.idLibro) "
                + "FROM lugar_edicion le "
                + "JOIN libros l ON le.idLugar = l.idLugar "
                + "GROUP BY le.lugar "
                + "ORDER BY COUNT(l.idLibro) DESC "
                + "LIMIT 5";

        ArrayList<Object[]> lista = new ArrayList<>();
        conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[2];
                fila[0] = rs.getString(1);
                fila[1] = rs.getInt(2);
                lista.add(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en Top Ciudades:\n" + e.getMessage());
        } finally {
            cerrarConexion();
        }
        return lista;
    }
}
