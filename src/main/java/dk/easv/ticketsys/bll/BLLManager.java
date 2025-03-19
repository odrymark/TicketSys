package dk.easv.ticketsys.bll;

import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.dal.ChooseFile;
import dk.easv.ticketsys.dal.DALManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.stage.Window;

public class BLLManager {
    private final DALManager dalManager = new DALManager();

    public BLLManager() throws TicketExceptions {
    }

    public int uploadNewEvent(Event event){
        return dalManager.uploadNewEvent(event);
    }

    public String chooseFile(Window window){
        ChooseFile fileBrowser = new ChooseFile(window);
        if (fileBrowser.getSelectedFilePath() != null){
            return fileBrowser.getSelectedFilePath();
        }
        return null;
    }

}
