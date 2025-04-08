package dk.easv.ticketsys.bll.util;

import dk.easv.ticketsys.bll.BLLManager;

import dk.easv.ticketsys.be.Event;

import java.awt.*;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AttendeeWriter {
    public static void exportAttendeesToFile(Event event, BLLManager bllManager) {
        String safeTitle = event.getTitle().replaceAll("[\\\\/:*?\"<>|\\s]", "_");
        File attendeesFolder = new File("attendees" + File.separator + safeTitle);
        if (!attendeesFolder.exists()) {
            attendeesFolder.mkdirs();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);

        File file = new File(attendeesFolder, safeTitle + "_Attendees_" + timestamp + ".txt");

        List<String> attendees = bllManager.getAttendees(event);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Attendees for event: " + event.getTitle());
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();

            for (String email : attendees) {
                writer.write(email);
                writer.newLine();
            }

            System.out.println("Attendees exported successfully to: " + file.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (IOException e) {
            System.err.println("Error writing attendee list: " + e.getMessage());
        }
    }
}
