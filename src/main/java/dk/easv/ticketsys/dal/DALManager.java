package dk.easv.ticketsys.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.TicketType;
import dk.easv.ticketsys.be.User;
import dk.easv.ticketsys.exceptions.TicketExceptions;

import java.sql.*;
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
                                rs.getString("locationGuidence"),
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
            String sql = "SELECT u.id, u.username, u.password, u.email, u.fullName, r.roleName " +
                    "FROM Users u " +
                    "JOIN Roles r ON u.roleID = r.id";

            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("fullName"),
                        rs.getString("roleName")
                ));
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
                System.out.println("DAL" + newId);
            }
            return newId;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void deleteEvent(Event event) {
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandDelete = "DELETE FROM Events WHERE id = ?";
            PreparedStatement pstmtDelete = con.prepareStatement(sqlcommandDelete);
            pstmtDelete.setInt(1, event.getId());
            pstmtDelete.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ArrayList<TicketType> getAllTicketTypes() {
        ArrayList<TicketType> ticketTypes = new ArrayList<>();
        try {
            Connection con = connectionManager.getConnection();
            String sqlcommandSelect = "SELECT * FROM TicketTypes ORDER BY title ASC";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                ticketTypes.add(new TicketType(rs.getInt("id"),
                        rs.getString("title"), rs.getBoolean("isSpecial")));
            }
            return ticketTypes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int uploadNewTicketType(TicketType ticketType) {
        int newId = 0;
        try {
            Connection con = connectionManager.getConnection();
            String sqlInsertCommand = "INSERT INTO TicketTypes (title, isSpecial) VALUES (?, ?)";
            PreparedStatement pstmtInsert = con.prepareStatement(sqlInsertCommand, Statement.RETURN_GENERATED_KEYS);
            pstmtInsert.setString(1, ticketType.getName());
            pstmtInsert.setBoolean(2, ticketType.getSpecial());
            int affectedRows = pstmtInsert.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmtInsert.getGeneratedKeys();
                if (rs.next()) {
                    newId = rs.getInt(1);
                }
            }
            return newId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateEvent(Event eventToSave) {
        String sqlcommandUpdate = "UPDATE Events SET title = ?, startDateTime = ?, endDateTime = ?, " +
                "location = ?, locationGuidence = ?, description = ?, imgSrc = ? WHERE id = ?";
        try (Connection con = connectionManager.getConnection();
             PreparedStatement pstmtUpdate = con.prepareStatement(sqlcommandUpdate)) {

            pstmtUpdate.setString(1, eventToSave.getTitle());
            pstmtUpdate.setTimestamp(2, eventToSave.getStartDateTime() != null ? eventToSave.getStartDateTime() : null);
            pstmtUpdate.setTimestamp(3, eventToSave.getEndDateTime() != null ? eventToSave.getEndDateTime() : null);
            pstmtUpdate.setString(4, eventToSave.getLocation());
            pstmtUpdate.setString(5, eventToSave.getLocationGuidence());
            pstmtUpdate.setString(6, eventToSave.getDescription());
            pstmtUpdate.setString(7, eventToSave.getImgSrc());
            pstmtUpdate.setInt(8, eventToSave.getId());

            int affectedRows = pstmtUpdate.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean deleteTicketType(TicketType ticketTypeToDelete) {
        String sqlcommandDelete = "DELETE FROM TicketTypes WHERE id = ?";
        try (Connection con = connectionManager.getConnection();
             PreparedStatement pstmtUpdate = con.prepareStatement(sqlcommandDelete)) {
            pstmtUpdate.setInt(1, ticketTypeToDelete.getId());
            int affectedRows = pstmtUpdate.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public User getUserByPassword(String username, String password)  {
        try (Connection con = connectionManager.getConnection()) {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("fullName"),
                        rs.getInt("roleID")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertUser(User user) {
        try (Connection con = connectionManager.getConnection()) {
            int newId = 0;
            String sqlcommandInsert = "INSERT INTO Users (username, password, email, fullName, " +
                    " roleId)\n" +
                    "OUTPUT INSERTED.ID\n" +
                    "VALUES (?, ?, ?, ?, ?);";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandInsert);
            pstmtSelect.setString(1, user.getUsername());
            pstmtSelect.setString(2, user.getPassword());
            pstmtSelect.setString(3, user.getEmail());
            pstmtSelect.setString(4, user.getFullName());
            pstmtSelect.setInt(5, user.getRoleID());
            ResultSet rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                newId = rs.getInt(1);
                System.out.println("DAL" + newId);
            }
            return newId;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean userUpdate(User user) {
        String sqlcommandUpdate = "UPDATE Users SET username = ?, password = ?, email = ?, " +
                "fullName = ?, roleId = ? WHERE id = ?";
        try (Connection con = connectionManager.getConnection();
             PreparedStatement pstmtUpdate = con.prepareStatement(sqlcommandUpdate)) {

            pstmtUpdate.setString(1, user.getUsername());
            pstmtUpdate.setString(2, user.getPassword());
            pstmtUpdate.setString(3, user.getEmail());
            pstmtUpdate.setString(4, user.getFullName());
            pstmtUpdate.setInt(5, user.getRoleID());
            pstmtUpdate.setInt(6, user.getId());

            int affectedRows = pstmtUpdate.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
