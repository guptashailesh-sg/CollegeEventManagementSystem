package in.collegeevents;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/admin/events")
public class AdminEventsServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        if(!admin(req,resp))return;
        List<Map<String,Object>> events=new ArrayList<>();
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement("SELECT e.id,e.title,e.category,e.venue,e.event_date,COUNT(r.id) registrations FROM events e LEFT JOIN registrations r ON r.event_id=e.id GROUP BY e.id ORDER BY e.event_date DESC");ResultSet rs=ps.executeQuery()){
            while(rs.next()){Map<String,Object> m=new HashMap<>();m.put("id",rs.getLong("id"));m.put("title",rs.getString("title"));m.put("category",rs.getString("category"));m.put("venue",rs.getString("venue"));m.put("date",rs.getTimestamp("event_date"));m.put("registrations",rs.getInt("registrations"));events.add(m);}
        }catch(SQLException e){throw new ServletException("Could not load admin dashboard.",e);}
        req.setAttribute("events",events);
        String selected=req.getParameter("participants");
        if(selected!=null) loadParticipants(req,selected);
        req.getRequestDispatcher("/admin-events.jsp").forward(req,resp);
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        if(!admin(req,resp))return;req.setCharacterEncoding("UTF-8");
        String title=v(req,"title"),category=v(req,"category"),description=v(req,"description"),venue=v(req,"venue"),date=v(req,"eventDate"),deadline=v(req,"deadline"),cap=v(req,"capacity");
        if(title.isBlank()||category.isBlank()||description.isBlank()||venue.isBlank()||date.isBlank()){resp.sendRedirect(req.getContextPath()+"/admin/events?status=missing");return;}
        try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement("INSERT INTO events(title,category,description,venue,event_date,registration_deadline,capacity,created_by) VALUES(?,?,?,?,?,?,?,?)")){
            ps.setString(1,title);ps.setString(2,category);ps.setString(3,description);ps.setString(4,venue);ps.setTimestamp(5,Timestamp.valueOf(date.replace("T"," ")+ (date.length()==16?":00":"")));
            if(deadline.isBlank())ps.setNull(6,Types.TIMESTAMP);else ps.setTimestamp(6,Timestamp.valueOf(deadline.replace("T"," ")+(deadline.length()==16?":00":"")));
            if(cap.isBlank())ps.setNull(7,Types.INTEGER);else ps.setInt(7,Integer.parseInt(cap));
            ps.setLong(8,(Long)req.getSession(false).getAttribute("userId"));ps.executeUpdate();
            resp.sendRedirect(req.getContextPath()+"/admin/events?status=created");
        }catch(Exception e){throw new ServletException("Could not create event. Verify the date and capacity.",e);}
    }
    private void loadParticipants(HttpServletRequest req,String id)throws ServletException{
        try{long eventId=Long.parseLong(id);List<Map<String,Object>> people=new ArrayList<>();
            try(Connection c=Db.getConnection();PreparedStatement ps=c.prepareStatement("SELECT full_name,email,student_id,department,registered_at FROM registrations WHERE event_id=? ORDER BY registered_at")){
                ps.setLong(1,eventId);try(ResultSet rs=ps.executeQuery()){while(rs.next()){Map<String,Object> p=new HashMap<>();p.put("name",rs.getString("full_name"));p.put("email",rs.getString("email"));p.put("studentId",rs.getString("student_id"));p.put("department",rs.getString("department"));p.put("registered",rs.getTimestamp("registered_at"));people.add(p);}}
            }req.setAttribute("participants",people);req.setAttribute("selectedEvent",eventId);
        }catch(Exception e){throw new ServletException("Could not load participant list.",e);}
    }
    private boolean admin(HttpServletRequest req,HttpServletResponse resp)throws IOException{
        HttpSession s=req.getSession(false);if(s==null||s.getAttribute("userId")==null){resp.sendRedirect(req.getContextPath()+"/login.jsp");return false;}
        if(!"ADMIN".equals(s.getAttribute("role"))){resp.sendError(403,"Administrator access required.");return false;}return true;
    }
    private String v(HttpServletRequest r,String k){String x=r.getParameter(k);return x==null?"":x.trim();}
}