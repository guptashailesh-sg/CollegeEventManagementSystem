package in.collegeevents;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    private Db() {}

    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault("CE_DB_URL",
            "jdbc:mysql://localhost:3306/college_event_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Kolkata");
        String user = System.getenv().getOrDefault("CE_DB_USER", "root");
        String password = System.getenv().getOrDefault("CE_DB_PASSWORD", "");

        // Explicitly load Connector/J so driver discovery issues give a clear error.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "MySQL JDBC driver class was not found. Ensure mysql-connector-j is packaged in WEB-INF/lib and redeploy the latest WAR.",
                e
            );
        }

        return DriverManager.getConnection(url, user, password);
    }
}
