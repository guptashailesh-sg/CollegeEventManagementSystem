package in.collegeevents;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/event/register")
public class EventRegistrationServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        HttpSession s=req.getSession(false);
        if(s==null||s.getAttribute("userId")==null){resp.sendRedirect(req.getContextPath()+"/login.jsp");return;}
        if(!"STUDENT".equals(s.getAttribute("role"))){resp.sendError(403);return;}
        long eventId;try{eventId=Long.parseLong(req.getParameter("eventId"));}catch(Exception e){resp.sendError(400);return;}
        try(Connection c=Db.getConnection()){
            c.setAutoCommit(false);
            try(PreparedStatement event=c.prepareStatement("SELECT event_date,registration_deadline,capacity,(SELECT COUNT(*) FROM registrations WHERE event_id=events.id) AS seats FROM events WHERE id=?")){
                event.setLong(1,eventId);
                try(ResultSet rs=event.executeQuery()){
                    if(!rs.next()){c.rollback();resp.sendError(404);return;}
                    Timestamp now=new Timestamp(System.currentTimeMillis());
                    Timestamp deadline=rs.getTimestamp("registration_deadline");
                    Integer capacity=(Integer)rs.getObject("capacity");
                    if(rs.getTimestamp("event_date").before(now)||(deadline!=null&&deadline.before(now))||(capacity!=null&&rs.getInt("seats")>=capacity)){
                        c.rollback();resp.sendRedirect(req.getContextPath()+"/events?status=closed");return;
                    }
                }
            }
            try(PreparedStatement user=c.prepareStatement("SELECT full_name,email,student_id,department FROM users WHERE id=?")){
                user.setLong(1,(Long)s.getAttribute("userId"));
                try(ResultSet rs=user.executeQuery()){
                    if(!rs.next()||rs.getString("student_id")==null||rs.getString("department")==null){c.rollback();resp.sendRedirect(req.getContextPath()+"/events?status=profile");return;}
                    try(PreparedStatement ins=c.prepareStatement("INSERT INTO registrations(event_id,student_user_id,full_name,email,student_id,department) VALUES(?,?,?,?,?,?)")){
                        ins.setLong(1,eventId);ins.setLong(2,(Long)s.getAttribute("userId"));ins.setString(3,rs.getString("full_name"));ins.setString(4,rs.getString("email"));ins.setString(5,rs.getString("student_id"));ins.setString(6,rs.getString("department"));ins.executeUpdate();
                    }
                }
            }
            c.commit();resp.sendRedirect(req.getContextPath()+"/events?status=registered");
        }catch(SQLIntegrityConstraintViolationException e){resp.sendRedirect(req.getContextPath()+"/events?status=duplicate");}
        catch(SQLException e){throw new ServletException("Registration failed. Check database setup.",e);}
    }
}