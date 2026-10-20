package in.collegeevents;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns={"/login","/register"})
public class AuthServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if ("/register".equals(req.getServletPath())) register(req, resp); else login(req, resp);
    }
    private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = value(req.getParameter("email")).toLowerCase();
        String password = value(req.getParameter("password"));
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,full_name,email,password_hash,password_salt,role,student_id,department FROM users WHERE email=?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && Passwords.matches(password, rs.getString("password_salt"), rs.getString("password_hash"))) {
                    HttpSession session = req.getSession(true);
                    session.setAttribute("userId", rs.getLong("id"));
                    session.setAttribute("name", rs.getString("full_name"));
                    session.setAttribute("email", rs.getString("email"));
                    session.setAttribute("role", rs.getString("role"));
                    session.setAttribute("studentId", rs.getString("student_id"));
                    session.setAttribute("department", rs.getString("department"));
                    resp.sendRedirect(req.getContextPath() + ( "ADMIN".equals(rs.getString("role")) ? "/admin/events" : "/events"));
                    return;
                }
            }
            req.setAttribute("error", "Email or password is incorrect.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException("Unable to sign in. Check database setup.", e); }
    }
    private void register(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name=value(req.getParameter("name")), email=value(req.getParameter("email")).toLowerCase();
        String studentId=value(req.getParameter("studentId")), dept=value(req.getParameter("department")), password=value(req.getParameter("password"));
        if (name.isBlank() || email.isBlank() || studentId.isBlank() || dept.isBlank() || password.length()<10) {
            req.setAttribute("error", "Complete every field. Password must be at least 10 characters.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp); return;
        }
        String salt=Passwords.newSalt();
        try (Connection c=Db.getConnection(); PreparedStatement ps=c.prepareStatement(
             "INSERT INTO users(full_name,email,student_id,department,password_hash,password_salt,role) VALUES(?,?,?,?,?,?, 'STUDENT')")) {
            ps.setString(1,name); ps.setString(2,email); ps.setString(3,studentId); ps.setString(4,dept);
            ps.setString(5,Passwords.hash(password,salt)); ps.setString(6,salt); ps.executeUpdate();
            resp.sendRedirect(req.getContextPath()+"/login.jsp?registered=1");
        } catch (SQLIntegrityConstraintViolationException e) {
            req.setAttribute("error","That email or student/college ID is already registered.");
            req.getRequestDispatcher("/register.jsp").forward(req,resp);
        } catch (SQLException e) { throw new ServletException("Unable to create account. Check database setup.",e); }
    }
    private String value(String s){return s==null?"":s.trim();}
}