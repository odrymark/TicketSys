package dk.easv.ticketsys.bll;

import dk.easv.ticketsys.be.*;
import dk.easv.ticketsys.dal.ChooseFile;
import dk.easv.ticketsys.dal.DALManager;
import dk.easv.ticketsys.exceptions.TicketExceptions;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import java.util.List;

public class BLLManager {
    private final DALManager dalManager = new DALManager();

    public BLLManager() throws TicketExceptions {
    }

    public int uploadNewEvent(Event event){ return dalManager.uploadNewEvent(event); }

    public List<Event> getAllEvents(){
        return dalManager.getAllEvents();
    }

    public List<User> getAllUsers(){
        return dalManager.getAllUsers();
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

    public void deleteUser(User user){
        dalManager.deleteUser(user);
    }

    public int uploadNewTicketType(TicketType ticketType) {
        return dalManager.uploadNewTicketType(ticketType);
    }

    public boolean updateEvent(Event eventToSave) {
        return dalManager.updateEvent(eventToSave);
    }

    public Boolean deleteTicketType(TicketType ticketTypeToDelete) {
        return dalManager.deleteTicketType(ticketTypeToDelete);
    }

    public User getUserByPassword(String username, String password) {

        return dalManager.getUserByPassword(username, password);
    }

    public int insertNewUser(User userToReturn) {
        return dalManager.insertUser(userToReturn);
    }

    public String hashPass(String username, String pass) {
        PasswordHasher hasher = new PasswordHasher();
        String hashedPass = null;
        try {
            hashedPass = hasher.hashPassword(pass, username);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        return hashedPass;
    }

    public boolean userToUpdate(User userToReturn) {
        return dalManager.userUpdate(userToReturn);
    }

    public int uploadNewTicket(Ticket ticket) {
        return dalManager.uploadNewTicket(ticket);
    }

    public int uploadNewCoupon(SpecialTicket coupon){return dalManager.uploadNewCoupon(coupon);}

    public User login(String username, String pass) {
        User user = dalManager.login(username, hashPass(username, pass));
        return user;
    }

    public User loginTest() {
        return dalManager.login("antal", "2Ze8mH0x2u2+C0zRHggCYk7QTZMxmsiKz2FicVAeac8=");
    }

    public boolean checkPassword(String txtUsername, String txtOldPass) {
        User user = dalManager.login(txtUsername, hashPass(txtUsername, txtOldPass));
        return user != null;
    }

    public boolean waitForFile(String fileName, int maxAttempts, int waitMillis) {
        File file = new File("./src/main/resources/dk/easv/ticketsys/Save/" + fileName);
        int attempts = 0;
        while (!file.exists() && attempts < maxAttempts) {
            try {
                Thread.sleep(waitMillis);
                System.out.println("Waiting" + attempts);// Wait before checking again
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            attempts++;
        }
        System.out.println("File exists? " + file.exists());
        return file.exists();
    }
}
