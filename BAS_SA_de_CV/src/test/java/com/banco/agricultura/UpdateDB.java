package com.banco.agricultura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UpdateDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/Banco_de_Agricultura?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Update usuario
            String updateUsuario = "UPDATE usuarios SET estado_usuario = 'Activo' WHERE email = 'mcajero@banco.sv'";
            try (PreparedStatement ps = conn.prepareStatement(updateUsuario)) {
                int rows = ps.executeUpdate();
                System.out.println("Usuarios updated: " + rows);
            }
            
            // Update empleado (by finding id_usuario)
            String updateEmpleado = "UPDATE empleados e JOIN usuarios u ON e.id_usuario = u.id_usuario SET e.estado_contratacion = 'Activo' WHERE u.email = 'mcajero@banco.sv'";
            try (PreparedStatement ps = conn.prepareStatement(updateEmpleado)) {
                int rows = ps.executeUpdate();
                System.out.println("Empleados updated: " + rows);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
