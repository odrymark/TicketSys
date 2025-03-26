package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.be.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TicketController
{
    @FXML
    private Label ticketDate;
    @FXML
    private Label ticketTime;
    @FXML
    private Label ticketLocation;
    private Event event;

    public void getEvent(Event event)
    {
        this.event = event;
        ticketDate.setText(event.getStartDate());
        ticketTime.setText(event.getStartDate());
        ticketLocation.setText(event.getLocation());
    }

    public void printTicket()
    {

    }

}
