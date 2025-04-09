package dk.easv.ticketsys.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import dk.easv.ticketsys.be.*;
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
                String sqlcommandSelect2 = "SELECT t.id, t.title, t.isSpecial\n" +
                        "FROM EventTicket et\n" +
                        "JOIN TicketTypes t ON et.ticket_type_id = t.id WHERE event_id = ? ";
                PreparedStatement pstmtSelect2 = con.prepareStatement(sqlcommandSelect2);
                pstmtSelect2.setInt(1, rs.getInt("id"));
                ResultSet rs2 = pstmtSelect2.executeQuery();
                ArrayList<TicketType> ticketTypes = new ArrayList<>();
                while (rs2.next()) {
                    ticketTypes.add(new TicketType(
                            rs2.getInt("id"),
                            rs2.getString("title"),
                            rs2.getBoolean("isSpecial")
                    ));}
                events.getLast().setTicketTypes(ticketTypes);
                ticketTypes.clear();
                //rs2.close();
            }


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return events;
    }

    public List<Event> getAllEvents(int userId) {
        List<Event> events = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandSelect = "DECLARE @UserId INT = " + userId + ";" +
                    "DECLARE @UserRole NVARCHAR(100);\n" +
                    "\n" +
                    "-- Get the user's role\n" +
                    "SELECT @UserRole = r.roleName\n" +
                    "FROM dbo.Users u\n" +
                    "JOIN dbo.Roles r ON u.roleID = r.id\n" +
                    "WHERE u.id = @UserId;\n" +
                    "\n" +
                    "-- Return events depending on the role\n" +
                    "IF @UserRole = 'Admin'\n" +
                    "BEGIN\n" +
                    "    SELECT e.*\n" +
                    "    FROM dbo.Events e;\n" +
                    "END\n" +
                    "ELSE IF @UserRole = 'Coordinator'\n" +
                    "BEGIN\n" +
                    "    SELECT e.*\n" +
                    "    FROM dbo.Events e\n" +
                    "    JOIN dbo.EventCoordinators ec ON e.id = ec.eventID\n" +
                    "    WHERE ec.coordinatorID = @UserId;\n" +
                    "END\n";
            String sqlcommandSelect3 = "DECLARE @UserId INT = " + userId + ";\n" +
                    "DECLARE @UserRole NVARCHAR(100);\n" +
                    "SELECT DISTINCT \n" +
                    "    e.id,\n" +
                    "    e.title,\n" +
                    "    e.startDateTime,\n" +
                    "    e.endDateTime,\n" +
                    "    e.location,\n" +
                    "    e.locationGuidence,\n" +
                    "    e.description,\n" +
                    "    e.imgSrc,\n" +
                    "    e.createdBy\n" +
                    "FROM dbo.Events e\n" +
                    "         LEFT JOIN dbo.EventCoordinators ec ON e.id = ec.eventID\n" +
                    "WHERE\n" +
                    "    (\n" +
                    "        -- If user is admin, show all\n" +
                    "        (SELECT r.roleName FROM dbo.Users u\n" +
                    "                                    JOIN dbo.Roles r ON u.roleID = r.id\n" +
                    "         WHERE u.id = @UserId) = 'Admin'\n" +
                    "            OR (\n" +
                    "            e.startDateTime > SYSDATETIME() AND\n" +
                    "            e.id IN (\n" +
                    "                SELECT eventID FROM dbo.EventCoordinators\n" +
                    "                WHERE coordinatorID = @UserId\n" +
                    "            )\n" +
                    "            )\n" +
                    "        )\n" +
                    "ORDER BY e.startDateTime;\n";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect3);
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
                String sqlcommandSelect2 = "SELECT t.id, t.title, t.isSpecial\n" +
                        "FROM EventTicket et\n" +
                        "JOIN TicketTypes t ON et.ticket_type_id = t.id WHERE event_id = ? ";
                PreparedStatement pstmtSelect2 = con.prepareStatement(sqlcommandSelect2);
                pstmtSelect2.setInt(1, rs.getInt("id"));
                ResultSet rs2 = pstmtSelect2.executeQuery();
                ArrayList<TicketType> ticketTypes = new ArrayList<>();
                while (rs2.next()) {
                    ticketTypes.add(new TicketType(
                            rs2.getInt("id"),
                            rs2.getString("title"),
                            rs2.getBoolean("isSpecial")
                    ));}
                events.getLast().setTicketTypes(ticketTypes);
                ticketTypes.clear();
                //rs2.close();
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
            if (event.getTicketTypes().size() > 0) {

                String sql = "INSERT INTO EventTicket (event_id, ticket_type_id) VALUES (?, ?)";
                String sqlCoordinator = "INSERT INTO EventCoordinators (coordinatorID, eventID) VALUES (?, ?)";
                try (PreparedStatement pstmtt = con.prepareStatement(sql);
                     PreparedStatement pstmtCoordinator = con.prepareStatement(sqlCoordinator)) {
                    con.setAutoCommit(false);
                    pstmtCoordinator.setInt(1, event.getCreatedBy());
                    pstmtCoordinator.setInt(2, newId);
                    for (TicketType ticketType : event.getTicketTypes()) {
                        pstmtt.setInt(1, newId);
                        pstmtt.setInt(2, ticketType.getId());
                        pstmtt.addBatch(); // Add insert statement to the batch
                    }
                    pstmtt.executeBatch();
                    pstmtCoordinator.execute();
                    con.commit();
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return newId;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
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

    public void deleteUser(User user) {
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandDelete = "DELETE FROM Users WHERE id = ?";
            PreparedStatement pstmtDelete = con.prepareStatement(sqlcommandDelete);
            pstmtDelete.setInt(1, user.getId());
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

            if (eventToSave.getTicketTypes().size() > 0) {
                String deleteAllBefore = "DELETE FROM EventTicket WHERE event_id = ?";
                String sql = "INSERT INTO EventTicket (event_id, ticket_type_id) VALUES (?, ?)";

                try (PreparedStatement pstmtDelete = con.prepareStatement(deleteAllBefore);
                        PreparedStatement pstmt = con.prepareStatement(sql);) {
                    con.setAutoCommit(false); // Disable auto-commit for batch processing
                    pstmtDelete.setInt(1, eventToSave.getId());
                    pstmtDelete.executeUpdate();
                    for (TicketType ticketType : eventToSave.getTicketTypes()) {
                        pstmt.setInt(1, eventToSave.getId());
                        pstmt.setInt(2, ticketType.getId());
                        pstmt.addBatch(); // Add insert statement to the batch
                    }

                    pstmt.executeBatch();
                    con.commit();
                    con.setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
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
    public int uploadNewTicket(Ticket ticket) {
        int newId = 0;
        try (Connection con = connectionManager.getConnection()) {
            String sqlInsertCommand = "INSERT INTO Tickets (eventID, buyerEmail, qrCode, barCode, printed, ticketType) " +
                    "OUTPUT INSERTED.ID " +
                    "VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement pstmtInsert = con.prepareStatement(sqlInsertCommand);
            pstmtInsert.setInt(1, ticket.getEventID());
            pstmtInsert.setString(2, ticket.getBuyerEmail());
            pstmtInsert.setBytes(3, ticket.getQrCode());
            pstmtInsert.setString(4, ticket.getBarCode());
            pstmtInsert.setBoolean(5, ticket.isPrinted());
            pstmtInsert.setInt(6, ticket.getTicketType());
            ResultSet rs = pstmtInsert.executeQuery();
            if (rs.next()) {
                newId = rs.getInt(1);
            }
            return newId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertCustomer(String name, String email) {
        int newId = 0;
        try (Connection con = connectionManager.getConnection()) {
            String sql = "INSERT INTO Customers (name, email) OUTPUT INSERTED.ID VALUES (?, ?);";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                newId = rs.getInt(1);
            }
            return newId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            String sql = "SELECT id, name, email FROM Customers;";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                customers.add(new Customer(id, name, email));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    public boolean deleteCustomer(int id) {
        try (Connection con = connectionManager.getConnection()) {
            String sql = "DELETE FROM Customers WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //TODO join with customer  mail , name(?)
    public int uploadNewCoupon(SpecialTicket coupon) {
        int newId = 0;
        try (Connection con = connectionManager.getConnection()) {
            String sqlInsertCommand = "INSERT INTO SpecialTickets (description, eventID, qrCode, barCode) " +
                    "OUTPUT INSERTED.ID " +
                    "VALUES (?, ?, ?, ?);";
            PreparedStatement pstmtInsert = con.prepareStatement(sqlInsertCommand);
            pstmtInsert.setString(1, coupon.getDescription());
            pstmtInsert.setInt(2, coupon.getEventID());
            pstmtInsert.setBytes(3, coupon.getQrCode());
            pstmtInsert.setString(4, coupon.getBarCode());
            ResultSet rs = pstmtInsert.executeQuery();
            if (rs.next()) {
                newId = rs.getInt(1);
            }
            return newId;
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

    public User login(String username, String pass) {
        User userToLogIn = null;
        String sqlcommandUpdate = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection con = connectionManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sqlcommandUpdate)) {
            pstmt.setString(1, username);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userToLogIn = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("fullName"),
                        rs.getInt("roleID"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return userToLogIn;
    }

    public List<String> getBuyerEmailsByTicketId(Event event) {
        List<String> emails = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandSelect = "SELECT buyerEmail FROM Tickets WHERE eventId = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            pstmtSelect.setInt(1, event.getId());
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                emails.add(rs.getString("buyerEmail")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return emails;
    }

    public ArrayList<User> getAllCoordinators() {
        ArrayList<User> users = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            String sqlcommandSelect = "SELECT * FROM Users WHERE roleID = ?";
            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            pstmtSelect.setInt(1, 4);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("fullName"),
                        rs.getInt("roleID")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public ArrayList<User> getActiveCoordinators(int eventId) {
        ArrayList<User> users = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {

            String sqlcommandSelect = "SELECT \n" +
                    "    u.id AS userID,\n" +
                    "    u.fullName,\n" +
                    "    u.username,\n" +
                    "    r.roleID\n" +
                    "FROM dbo.EventCoordinators ec\n" +
                    "JOIN dbo.Users u ON ec.coordinatorID = u.id\n" +
                    "WHERE ec.eventID = ?;\n";

            PreparedStatement pstmtSelect = con.prepareStatement(sqlcommandSelect);
            pstmtSelect.setInt(1, eventId);
            ResultSet rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("fullName"),
                        rs.getInt("roleID")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
    }
