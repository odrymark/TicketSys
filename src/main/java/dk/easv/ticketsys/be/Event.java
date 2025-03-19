package dk.easv.ticketsys.be;

import java.sql.Timestamp;

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
}
