package dk.easv.ticketsys.bll;

import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.TicketType;
import dk.easv.ticketsys.dal.ChooseFile;
import dk.easv.ticketsys.dal.DALManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.stage.Window;

import java.util.ArrayList;

import java.util.List;

public class BLLManager {
    private final DALManager dalManager = new DALManager();

    public BLLManager() throws TicketExceptions {
    }

    public int uploadNewEvent(Event event){
        return dalManager.uploadNewEvent(event);
    }

    public List<Event> getAllEvents(){
        return dalManager.getAllEvents();
    }

    public String chooseFile(Window window){
        ChooseFile fileBrowser = new ChooseFile(window);
        if (fileBrowser.getSelectedFilePath() != null){
            return fileBrowser.getSelectedFilePath();
        }
        return null;
    }

    public ArrayList<TicketType> getTicketTypes() {
        return dalManager.getAllTicketTypes();
    }
    public void deleteEvent(Event event){
        dalManager.deleteEvent(event);
    }

    public int uploadNewTicketType(TicketType ticketType) {
        return dalManager.uploadNewTicketType(ticketType);
    }
}
