/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;
import java.lang.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Jorge
 */
public  class MySQL {
    public static Connection conn = null;
    public static void conectar (){
    try {

    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/satin?useSSL=true", "root", "root") ;
    System.out.println("Conexion exitosa");



} catch (SQLException ex) {
    // handle any errors
    JOptionPane.showMessageDialog(null, "Error de conexión.", "Advertencia", JOptionPane.WARNING_MESSAGE);
    System.out.println("SQLException: " + ex.getMessage());
    System.out.println("SQLState: " + ex.getSQLState());
    System.out.println("VendorError: " + ex.getErrorCode());
}
        
    }
    
    public static void desconectar (){
        try {
        conn.close();
        } catch (SQLException ex) {
    // handle any errors
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
        }

    }
        public ResultSet select(String query) {
        //método para todas las consultas del sistema, recibe query.
        ResultSet rs = null;
        try {
            Statement s = conn.createStatement();
            rs = s.executeQuery(query);

        } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error de conexión.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
        System.out.println("Consulta exitosa");
        return rs;
    }
        
        public boolean stmt(String sql){
            //método para insert, update, delete.
        boolean b= false;
        
        try {
            Statement stmt = conn.createStatement();
            System.out.println(sql);
            stmt.executeUpdate(sql);
            //el booleano indica si la inserción fue correcta o no.
            b=true;
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return b;
        }
}
