package dk.easv.ticketsys.dal;

import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.exceptions.TicketExceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DALManager {
    private final ConnectionManager connectionManager;

    public DALManager() throws TicketExceptions {
        this.connectionManager = new ConnectionManager();
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandSelect = "SELECT * FROM Events";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getTimestamp("startDateTime"),
                        rs.getTimestamp("endDateTime"),
                        rs.getString("location"),
                        rs.getString("locationGuidance"),
                        rs.getString("description"),
                        rs.getString("imgSrc"),
                        rs.getInt("createdBy")
                        )
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return events;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandSelect = "SELECT * FROM Users";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                users.add(new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email"),
                                rs.getString("fullName"),
                                rs.getInt("roleID")
                        )
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return users;
    }


    public int uploadNewEvent(Event event) {
        try (Connection con = connectionManager.getConnection()) {
            int newId = 0;
            String sqlcommandInsert = "INSERT INTO Events (title, startDateTime, endDateTime, location, " +
                    " locationGuidence, description, imgSrc, createdBy)\n" +
                    "OUTPUT INSERTED.ID\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setString(1, event.getTitle());
            pstmtSelect.setTimestamp(2, event.getStartDateTime());
            pstmtSelect.setTimestamp(3, event.getEndDateTime());
            pstmtSelect.setString(4, event.getLocation());
            pstmtSelect.setString(5, event.getLocationGuidence());
            pstmtSelect.setString(6, event.getDescription());
            pstmtSelect.setString(7, event.getImgSrc());
            pstmtSelect.setInt(8, event.getCreatedBy());
            ResultSet rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newId = rs.getInt(1);
            }
            return newId;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
