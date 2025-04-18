package dk.easv.ticketsys.PL;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import dk.easv.ticketsys.be.Customer;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.Ticket;
import dk.easv.ticketsys.bll.BLLManager;
import dk.easv.ticketsys.bll.util.PdfSaver;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    @FXML
    private Label ticketParticipantMeil;

    @FXML
    private HBox ticketContent;

    private Event event;
    private Customer customer;
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

    private void updateUI() {
        if (event != null) {
            ticketDate.setText(event.getStartDate());
            ticketTime.setText(event.getEndDate());
            ticketLocation.setText(event.getLocation());
            ticketEvent.setText(event.getTitle());
        }

        if (customer != null) {
            ticketParticipantName.setText(customer.getName());
            ticketParticipantMeil.setText(customer.getEmail());
        }

        if (event != null && customer != null) {
            String ticketInfo = buildTicketInfo(event);
            generateBarcode(ticketInfo);
            generateQRCode(ticketInfo);
        }
    }


    public void getEvent(Event event) {
        this.event = event;
        updateUI();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        updateUI();
    }


    public void printTicket() {
        // Build the ticket information string
        String ticketInfo = buildTicketInfo(event);
        // Generate QR code and barcode data based on the ticket info
        byte[] qrCodeData = generateQRCodeData(ticketInfo);
        String barCodeData = generateBarCodeData(ticketInfo);

        // Use buyer's email if available; otherwise, use a default value
        String buyerEmail = (customer != null) ? customer.getEmail() : "unknown@unknown";
        // Specify the ticket type (modify as needed)
        int ticketType = 17;

        // Create a new Ticket object with an initial ID of 0
        Ticket newTicket = new Ticket(
                0,
                event.getId(),
                buyerEmail,
                qrCodeData,
                barCodeData,
                false,
                ticketType
        );
        newTicket.setCustomerId(customer.getId());


        // Upload the ticket to the database
        int newTicketId = bllManager.uploadNewTicket(newTicket);
        if (newTicketId > 0) {
            newTicket.setId(newTicketId);
            System.out.println("Ticket saved with ID: " + newTicketId);

            // Use the event title as the folder identifier after sanitizing it
            String folderName = (event != null) ? sanitizeEventTitle(event.getTitle()) : "unknown";
            // Construct the file name
            String fileName = "ticket_" + newTicketId + ".pdf";
            // Call PdfSaver to save the PDF file
            PdfSaver.savePdf(ticketContent, fileName, folderName);
            System.out.println("PDF saved for ticket " + newTicketId);

            // Open the PDF file using the system's default PDF viewer
            File pdfFile = new File("./TicketsPDF/" + folderName + "/" + fileName);
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
                try {
                    java.awt.Desktop.getDesktop().open(pdfFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error opening PDF file.");
                }
            } else {
                System.out.println("Desktop not supported or OPEN action not available.");
            }
        } else {
            System.out.println("Error saving ticket");
        }
    }


    private String sanitizeEventTitle(String title) {
        return title.replaceAll("[^a-zA-Z0-9]", "_");
    }


    private String buildTicketInfo(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Event: ").append(event.getTitle()).append("\n")
                .append("Date: ").append(event.getStartDate()).append("\n")
                .append("Location: ").append(event.getLocation()).append("\n");
        if (customer != null) {
            sb.append("Holder: ").append(customer.getName());
        } else {
            sb.append("Holder: [Not Selected]");
        }
        return sb.toString();
    }

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
}
