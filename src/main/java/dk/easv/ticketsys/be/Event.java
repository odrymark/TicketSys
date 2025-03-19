package dk.easv.ticketsys.be;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Event {
    private int id;
    private String title;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private String location;
    private String locationGuidence;
    private String description;
    private ArrayList<TicketType> ticketTypes;



    private int eventType;
    private String imgSrc;
    private int createdBy;

    public Event(int id, String title, Timestamp startDateTime, Timestamp endDateTime, String location, String locationGuidence, String description, String imgSrc, int createdBy) {
        this.id = id;
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.locationGuidence = locationGuidence;
        this.description = description;
        this.imgSrc = imgSrc;
        this.createdBy = createdBy;
        this.ticketTypes = new ArrayList<>();
    }

    public Event(String title, String startDateTime, String location, int eventType, String description) {
        this.title = title;

        this.startDateTime = Timestamp.valueOf(LocalDateTime.parse(startDateTime));

        this.description = description;
        this.location = location;
        this.eventType = eventType;
        this.ticketTypes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getStartDateTime() {
        return String.valueOf(startDateTime.toLocalDateTime());
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = Timestamp.valueOf(startDateTime);
    }

    public String getEndDateTime() {
        return String.valueOf(endDateTime);
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = Timestamp.valueOf(endDateTime);
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationGuidence() {
        return locationGuidence;
    }

    public void setLocationGuidence(String locationGuidence) {
        this.locationGuidence = locationGuidence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public void setEndDate(String s) {
        this.endDateTime = Timestamp.valueOf(LocalDateTime.parse(s));
    }

    public void setLocationGuide(String text) {
        this.locationGuidence = text;
    }

    public void setNotes(String text) {
        this.description = text;
    }

    public void setTypeOfEvent(int a) {
        this.eventType = a;
    }

    public void setTicketTypes(ArrayList<TicketType> ticketTypes) {
        this.ticketTypes.addAll(ticketTypes);
    }

    public String getStartDate() {
        return startDateTime.toString();
    }

    public String getEndDate() {
        return endDateTime.toString();
    }

    public String imageSrcProperty() {
        return imgSrc;
    }

    public String getLocationGuide() {
        return locationGuidence;
    }

    public int getTypeOfEvent() {
        return eventType;
    }

    public String getNotes() {
        return description;
    }
}
