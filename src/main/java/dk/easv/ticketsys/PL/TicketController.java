package dk.easv.ticketsys.PL;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import dk.easv.ticketsys.be.Event;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import javafx.scene.image.ImageView;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TicketController
{
    @FXML private Label ticketDate;
    @FXML private Label ticketTime;
    @FXML private Label ticketLocation;
    @FXML private ImageView imgBarcode;
    @FXML private ImageView imgQRCode;
    @FXML private Label ticketEvent;
    @FXML private Label ticketParticipantName;

    private Event event;





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

        // Create info text after scan QR Code1
        String ticketInfo = buildTicketInfo(event);

        generateBarcode(ticketInfo);
        generateQRCode(ticketInfo);
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
            System.out.println(" Error generating QR code.");
        }
    }

    public void generateBarcode(String data) {
        try {
            int width = 300;
            int height = 80;

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    data,
                    BarcodeFormat.CODE_128,
                    width,
                    height,
                    hints
            );

            BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // terned 90
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
        // TODO add data about customer
        return sb.toString();
    }


    public void printTicket()
    {

    }



}
