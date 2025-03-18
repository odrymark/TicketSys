package dk.easv.ticketsys.be;

public class Ticket {
    private int id;
    private int eventID;
    private String buyerEmail;
    private byte[] qrCode;
    private String barCode;
    private boolean printed;
    private int ticketType;

    public Ticket(int id, int eventID, String buyerEmail, byte[] qrCode, String barCode, boolean printed, int ticketType) {
        this.id = id;
        this.eventID = eventID;
        this.buyerEmail = buyerEmail;
        this.qrCode = qrCode;
        this.barCode = barCode;
        this.printed = printed;
        this.ticketType = ticketType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
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

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public int getTicketType() {
        return ticketType;
    }

    public void setTicketType(int ticketType) {
        this.ticketType = ticketType;
    }
}
