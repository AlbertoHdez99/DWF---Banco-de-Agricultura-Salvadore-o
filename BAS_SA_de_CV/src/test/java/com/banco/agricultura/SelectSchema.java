import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SelectSchema {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/Banco_de_Agricultura?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String q = "SHOW COLUMNS FROM acciones_personal LIKE 'tipo_accion'";
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
                if(rs.next()) {
                    System.out.println("Type: " + rs.getString("Type"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
