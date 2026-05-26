import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SelectDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/Banco_de_Agricultura?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String q = "SELECT id_accion, tipo_accion, estado_accion FROM acciones_personal";
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
                while(rs.next()) {
                    System.out.println("ID: " + rs.getInt(1) + ", Tipo: " + rs.getString(2) + ", Estado: " + rs.getString(3));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
