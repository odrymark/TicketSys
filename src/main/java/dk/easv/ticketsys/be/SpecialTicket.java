package dk.easv.ticketsys.be;

public class SpecialTicket {
    private int id;
    private String description;
    private int eventID;
    private byte[] qrCode;
    private String barCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public byte[] getQrCode() {
        return qrCode;
    }

    public void setQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public SpecialTicket(int id, String description, int eventID, byte[] qrCode, String barCode) {
        this.id = id;
        this.description = description;
        this.eventID = eventID;
        this.qrCode = qrCode;
        this.barCode = barCode;


    }
}
