package dk.easv.ticketsys.be;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;
    private int roleID;

    public User(int id, String username, String password, String email, String fullName, int roleID) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.roleID = roleID;
        setRole(getRoleName(roleID));
    }

    private String getRoleName(int roleID) {
        switch (roleID) {
            case 3:
                return "Admin";
            case 4:
                return "Event Coordinator";
            case 5:
                return "SuperUser";
        }
        return null;
    }

    public User(int id, String username, String password, String email, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public User(int id, String username, String fullName, int roleID) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.roleID = roleID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return (fullName + " " + username + " " + email + " " + getRole());
    }
}
