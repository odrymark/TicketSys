package dk.easv.ticketsys.be;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String location;
    private String locationGuidence;
    private String description;



    private int eventType;
    private String imgSrc;
    private int createdBy;

    public Event(int id, String title, LocalDateTime startDateTime, LocalDateTime endDateTime, String location, String locationGuidence, String description, String imgSrc, int createdBy) {
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

    public Event(String title, String startDateTime, String location, int eventType, String description) {
        this.title = title;
        this.startDateTime = LocalDateTime.parse(startDateTime);
        this.description = description;
        this.location = location;
        this.eventType = eventType;
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

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
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
}
