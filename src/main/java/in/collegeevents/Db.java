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
        return DriverManager.getConnection(url, user, password);
    }
}