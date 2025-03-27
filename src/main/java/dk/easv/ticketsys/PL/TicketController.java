package dk.easv.ticketsys.PL;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.Ticket;
import dk.easv.ticketsys.bll.BLLManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TicketController {
    @FXML private Label ticketDate;
    @FXML private Label ticketTime;
    @FXML private Label ticketLocation;
    @FXML private ImageView imgBarcode;
    @FXML private ImageView imgQRCode;
    @FXML private Label ticketEvent;
    @FXML private Label ticketParticipantName;

    private Event event;
    private BLLManager bllManager;

    @FXML
    public void initialize() {
        try {
            bllManager = new BLLManager();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing BLLManager!");
        }
    }

    public void getEvent(Event event) {
        if (ticketEvent == null || ticketLocation == null || ticketDate == null ||
                ticketTime == null || ticketParticipantName == null) {
            System.err.println("FXML elements are not initialized!");
            return;
        }
        this.event = event;
        ticketDate.setText(event.getStartDate());
        ticketTime.setText(event.getStartDate());
        ticketLocation.setText(event.getLocation());
        ticketEvent.setText(event.getTitle());

        // Build ticket information string from event details
        String ticketInfo = buildTicketInfo(event);

        // Generate and display barcode and QR code images
        generateBarcode(ticketInfo);
        generateQRCode(ticketInfo);
    }

    /**
     * Generates a QR code image from the provided data and sets it in the ImageView.
     */
    public void generateQRCode(String data) {
        try {
            int width = 150;
            int height = 150;
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
            Image qrImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imgQRCode.setImage(qrImage);
        } catch (WriterException e) {
            e.printStackTrace();
            System.out.println("Error generating QR code.");
        }
    }

    /**
     * Generates a barcode image from the provided data,  and sets it in the ImageView.
     */
    public void generateBarcode(String data) {
        try {
            int width = 300;
            int height = 80;
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.CODE_128, width, height, hints);
            BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Rotate the barcode image 90 degrees
            BufferedImage rotatedImage = new BufferedImage(barcodeImage.getHeight(), barcodeImage.getWidth(), barcodeImage.getType());
            for (int y = 0; y < barcodeImage.getHeight(); y++) {
                for (int x = 0; x < barcodeImage.getWidth(); x++) {
                    rotatedImage.setRGB(y, barcodeImage.getWidth() - 1 - x, barcodeImage.getRGB(x, y));
                }
            }

            Image fxImage = SwingFXUtils.toFXImage(rotatedImage, null);
            imgBarcode.setImage(fxImage);
        } catch (WriterException e) {
            e.printStackTrace();
            System.out.println("Error generating Barcode.");
        }
    }

    private String buildTicketInfo(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Event: ").append(event.getTitle()).append("\n")
                .append("Date: ").append(event.getStartDate()).append("\n")
                .append("Location: ").append(event.getLocation());
        // TODO: add customer-specific data if needed.
        return sb.toString();
    }

    private byte[] generateQRCodeData(String data) {
        try {
            int width = 150;
            int height = 150;
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateBarCodeData(String data) {
        try {
            int width = 300;
            int height = 80;
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.CODE_128, width, height, hints);
            BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(barcodeImage, "png", baos);
            byte[] barcodeBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(barcodeBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void printTicket() {
        // Build the ticket information string from the event details
        String ticketInfo = buildTicketInfo(event);

        // Generate raw data for QR code and barcode
        byte[] qrCodeData = generateQRCodeData(ticketInfo);
        String barCodeData = generateBarCodeData(ticketInfo);

        //TODO
        String buyerEmail = "user@example.com";
        int ticketType = 17; // Example ticket type

        // Create a new Ticket object
        Ticket newTicket = new Ticket(0, event.getId(), buyerEmail, qrCodeData, barCodeData, false, ticketType);

        // Save the ticket to the database using BLLManager
        int newTicketId = bllManager.uploadNewTicket(newTicket);
        if (newTicketId > 0) {
            newTicket.setId(newTicketId);
            System.out.println("Ticket saved with ID: " + newTicketId);
        } else {
            System.out.println("Error saving ticket");
        }

        // TODO printing functionality here.
    }
}
