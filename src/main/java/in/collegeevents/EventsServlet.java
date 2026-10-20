package in.collegeevents;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/events")
public class EventsServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        HttpSession session=req.getSession(false);
        if(session==null || session.getAttribute("userId")==null){resp.sendRedirect(req.getContextPath()+"/login.jsp");return;}
        List<Map<String,Object>> events=new ArrayList<>();
        String sql="SELECT e.id,e.title,e.category,e.description,e.venue,e.event_date,e.registration_deadline,e.capacity,COUNT(r.id) registered, MAX(CASE WHEN r.student_user_id=? THEN 1 ELSE 0 END) mine FROM events e LEFT JOIN registrations r ON r.event_id=e.id GROUP BY e.id ORDER BY e.event_date";
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
            ps.setLong(1,(Long)session.getAttribute("userId"));
            try(ResultSet rs=ps.executeQuery()){while(rs.next()){
                Map<String,Object> m=new HashMap<>();m.put("id",rs.getLong("id"));m.put("title",rs.getString("title"));m.put("category",rs.getString("category"));
                m.put("description",rs.getString("description"));m.put("venue",rs.getString("venue"));m.put("date",rs.getTimestamp("event_date"));
                m.put("deadline",rs.getTimestamp("registration_deadline"));m.put("capacity",(Integer)rs.getObject("capacity"));m.put("registered",rs.getInt("registered"));m.put("mine",rs.getInt("mine")==1);events.add(m);
            }}
        }catch(SQLException e){throw new ServletException("Could not load events. Check database setup.",e);}
        req.setAttribute("events",events);req.getRequestDispatcher("/events.jsp").forward(req,resp);
    }
}