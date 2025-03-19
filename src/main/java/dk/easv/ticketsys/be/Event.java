package dk.easv.ticketsys.be;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Event {




    private IntegerProperty id;
    private final StringProperty imageSrc;
    private final StringProperty title;
    private final StringProperty startDate;
    private StringProperty endDate;
    private final StringProperty location;
    private String locationGuide;
    private final IntegerProperty typeOfEvent;
    private ArrayList<Integer> ticketTypes;
    private String notes;
    private final ObservableValue<String> active;

    private final static String IMG_PATH = "src/main/resources/dk/easv/ticket/img/";


    public Event(String title, String startDate, String location, int typeOfEvent, String img) {
        this.title = new SimpleStringProperty(title);
        this.startDate = new SimpleStringProperty(startDate);
        this.location = new SimpleStringProperty(location);
        this.typeOfEvent = new SimpleIntegerProperty(typeOfEvent);
        //imageSrc1 = new SimpleStringProperty(img);
        this.ticketTypes = new ArrayList<>();
        this.endDate = new SimpleStringProperty();

        Random r = new Random();
        int active = r.nextInt(4);
        switch (active) {
            case 0:
                this.active = new SimpleStringProperty("No");
                break;
            case 1:
                this.active = new SimpleStringProperty("Pending");
                break;
            default:
                this.active = new SimpleStringProperty("Yes");

        }
        //this.active = new SimpleStringProperty("true");
        if (img != null) {
            String path = IMG_PATH + img;
            imageSrc = new SimpleStringProperty(path);
        }
        else {
            //this.imageSrc = new SimpleStringProperty("src/main/resources/dk/easv/listview_for_ticket_gui/img/no_img.jpg");
            imageSrc = null;
        }
    }


    //Getters
    public StringProperty imageSrcProperty() {return imageSrc;}
    public StringProperty titleProperty() {
        Random r = new Random();
        int howManyGuests = r.nextInt(100) + 23;
        //return new SimpleStringProperty(title.get() + " (" + howManyGuests + ")");
        return title;
    }
    public StringProperty startDateProperty() { return startDate; }
    public StringProperty endDateProperty() { return endDate; }
    public StringProperty locationProperty() { return location; }
    public IntegerProperty typeOfEventProperty() { return typeOfEvent; }
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getImageSrc() {
        return imageSrc.get();
    }

    public String getTitle() {
        return title.get();
    }


    public String getStartDate() {
        return startDate.get();
    }


    public String getEndDate() {
        return endDate.get();
    }

    public String getLocation() {
        return location.get();
    }


    public String getLocationGuide() {
        return locationGuide;
    }

    public int getTypeOfEvent() {
        return typeOfEvent.get();
    }


    public ArrayList<Integer> getTicketTypes() {
        return ticketTypes;
    }

    public String getNotes() {
        return notes;
    }


    //Setters
    public void setImageSrc(String imageSrc) {
        this.imageSrc.set(imageSrc);
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setStartDate(String startDate) {
        this.startDate.set(startDate);
    }

    public void setEndDate(String endDate) {
        this.endDate.set(endDate);
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public void setLocationGuide(String locationGuide) {
        this.locationGuide = locationGuide;
    }

    public void setTypeOfEvent(int typeOfEvent) {
        this.typeOfEvent.set(typeOfEvent);
    }

    public void setTicketTypes(ArrayList<Integer> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ObservableValue<String> activeProperty() {

        return active;
    }

    @Override
    public String toString() {
        return "t: " + getTitle() + " sd: " + getStartDate() + " ed: " + getEndDate()
                + " loc:" + getLocation() + " locg: " + getLocationGuide()
                + " type: " + getTypeOfEvent() + " notes: " + getNotes();

        /**
         * private final StringProperty imageSrc;
         *     private final StringProperty title;
         *     private final StringProperty startDate;
         *     private Date endDate;
         *     private final StringProperty location;
         *     private String locationGuide;
         *     private final IntegerProperty typeOfEvent;
         *     private ArrayList<Integer> ticketTypes;
         *     private String notes;
         *     private final ObservableValue<String> active;
         */
    }
}

