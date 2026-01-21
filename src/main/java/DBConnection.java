import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public Connection getDBConnection() throws SQLException {
        try {
            return DriverManager.getConnection(
                    System.getenv("JDBC_URL"),
                    System.getenv("USERNAME"),
                    System.getenv("PASSWORD"));
        }catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }
}