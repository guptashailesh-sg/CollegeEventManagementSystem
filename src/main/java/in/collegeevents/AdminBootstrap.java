package in.collegeevents;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebListener
public class AdminBootstrap implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) {
        String email = System.getenv("CE_ADMIN_EMAIL");
        String password = System.getenv("CE_ADMIN_PASSWORD");
        if (email == null || email.isBlank() || password == null || password.length() < 12) {
            event.getServletContext().log("Admin bootstrap skipped: set CE_ADMIN_EMAIL and CE_ADMIN_PASSWORD (at least 12 characters) before starting Tomcat.");
            return;
        }
        try (Connection c = Db.getConnection();
             PreparedStatement check = c.prepareStatement("SELECT id FROM users WHERE email=?")) {
            check.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) return;
            }
            String salt = Passwords.newSalt();
            try (PreparedStatement insert = c.prepareStatement(
                    "INSERT INTO users(full_name,email,password_hash,password_salt,role) VALUES(?,?,?,?, 'ADMIN')")) {
                insert.setString(1, "System Administrator");
                insert.setString(2, email.trim().toLowerCase());
                insert.setString(3, Passwords.hash(password, salt));
                insert.setString(4, salt);
                insert.executeUpdate();
                event.getServletContext().log("Initial admin account created for " + email.trim().toLowerCase());
            }
        } catch (Exception e) {
            event.getServletContext().log("Admin bootstrap failed. Check MySQL and CE_DB_* settings.", e);
        }
    }
}