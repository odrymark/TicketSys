package dk.easv.ticketsys.be;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Event {
    private int id;
    private String title;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private String location;
    private String locationGuidence;
    private String description;



    private String imgSrc;
    private int createdBy;
    private ArrayList<TicketType> ticketTypes;

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

    public Event(String title, String startDateTime, String location, String description, int createdBy) {
        this.title = title;
        System.out.println("be: " + startDateTime);
        this.startDateTime = Timestamp.valueOf(startDateTime);
        this.description = description;
        this.location = location;
        this.createdBy = createdBy;
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

    public Timestamp getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Timestamp startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Timestamp getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Timestamp endDateTime) {
        this.endDateTime = endDateTime;
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

    public String getStartDate() {
        String formattedTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(getStartDateTime());
        return formattedTime;
    }

    public String getEndDate() {
        String formattedTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(getEndDateTime());
        return formattedTime;
    }


    public String imageSrc() {
        return imgSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imgSrc = imageSrc;
    }

    public String getLocationGuide() {
        return locationGuidence;
    }

    public String getNotes() {
        return description;
    }

    public void setEndDate(String s) {
        this.endDateTime = Timestamp.valueOf(s);
    }

    public void setLocationGuide(String text) {
        this.locationGuidence = text;
    }

    public void setNotes(String text) {
        this.description = text;
    }

    public ArrayList<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    public void setTicketTypes(ArrayList<TicketType> ticketTypes) {
        this.ticketTypes.clear();
        this.ticketTypes.addAll(ticketTypes);
    }

    @Override
    public String toString() {
        return "t: " + getTitle() + " sd: " + getStartDate() + " ed: " + getEndDate() + " et: " + " n: " + getNotes() + " img:" + imageSrc();
    }
}
