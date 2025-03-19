package dk.easv.ticketsys.be;

public class TicketType {
    int id;
    String name;
    String description;



    Boolean special;
    public TicketType(int id, String name, Boolean special) {
        this.id = id;
        this.name = name;
        this.special = special;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getSpecial() {
        return special;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSpecial(Boolean special) {
        this.special = special;
    }

    @Override
    public String toString() {
        return name;
    }
}
