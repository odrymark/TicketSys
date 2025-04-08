package dk.easv.ticketsys.PL;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import dk.easv.ticketsys.be.Customer;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.SpecialTicket;
import dk.easv.ticketsys.be.TicketType;
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

public class CouponController {

    @FXML private Label couponEvent;
    @FXML private Label Date;
    @FXML private Label couponLocation;
    @FXML private Label couponHolder;
    @FXML private Label holderMail;
    @FXML private ImageView couponBarcode;
    @FXML private ImageView couponQrCode;
    @FXML private Label couponTypeLabel;
    @FXML
    private HBox couponContent;

    private Event event;
    private TicketType couponType;
    private Customer customer;
    private BLLManager bllManager;

    @FXML
    public void initialize() {
        try {
            bllManager = new BLLManager();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing BLLManager in CouponController!");
        }
    }

    public void setEvent(Event event) {
        this.event = event;
        updateUI();
    }

    public void setCouponType(TicketType couponType) {
        this.couponType = couponType;
        updateCouponTypeUI();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        updateUI();
    }

    private void updateCouponTypeUI() {
        if (couponType != null) {
            couponTypeLabel.setText(couponType.getName());
        } else {
            couponTypeLabel.setText("Not Selected");
        }
    }

    private void updateUI() {
        if (event != null) {
            couponEvent.setText(event.getTitle());
            couponLocation.setText(event.getLocation());
            Date.setText(event.getStartDate());
        }

        if (customer != null) {
            couponHolder.setText(customer.getName());
            holderMail.setText(customer.getEmail());
        }

        updateCouponTypeUI();

        String couponInfo = buildCouponInfo();
        generateBarcode(couponInfo);
        generateQRCode(couponInfo);
    }

    private String buildCouponInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Coupon for: ").append(event != null ? event.getTitle() : "N/A").append("\n")
                .append("Location: ").append(event != null ? event.getLocation() : "N/A").append("\n")
                .append("Valid Until: ").append(event != null ? event.getStartDate() : "N/A").append("\n");

        if (customer != null) {
            sb.append("Holder: ").append(customer.getName());
        } else {
            sb.append("Holder: [No Customer]");
        }
        return sb.toString();
    }

    public void generateQRCode(String data) {
        try {
            int width = 150, height = 150;
            BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
            Image qrImage = SwingFXUtils.toFXImage(bufferedImage, null);
            couponQrCode.setImage(qrImage);
        } catch (WriterException e) {
            e.printStackTrace();
            System.out.println("Error generating QR code for coupon.");
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
            couponBarcode.setImage(fxImage);
        } catch (WriterException e) {
            e.printStackTrace();
            System.out.println("Error generating Barcode.");
        }
    }

    private byte[] generateQRCodeData(String data) {
        try {
            int width = 150, height = 150;
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
    public void printCoupon() {
        // Build the coupon information string
        String couponInfo = buildCouponInfo();
        // Generate QR code data based on the coupon info
        byte[] qrCodeData = generateQRCodeData(couponInfo);
        // Generate barcode data based on the coupon info
        String barCodeData = generateBarCodeData(couponInfo);

        String description = "Discount Coupon";
        // Use the event ID if available; otherwise, default to 0
        int eventID = (event != null) ? event.getId() : 0;

        // Create a new SpecialTicket object with an initial ID of 0
        SpecialTicket coupon = new SpecialTicket(
                0,
                description,
                eventID,
                qrCodeData,
                barCodeData
        );

        // Upload the coupon to the database
        int newCouponId = bllManager.uploadNewCoupon(coupon);
        if (newCouponId > 0) {
            coupon.setId(newCouponId);
            System.out.println("Coupon saved with ID: " + newCouponId);

            // Use the event title as the folder name after sanitizing it
            String eventIdentifier = (event != null) ? sanitizeEventTitle(event.getTitle()) : "unknown";
            // Construct the file name
            String fileName = "coupon_" + newCouponId + ".pdf";
            // Save the PDF into the subdirectory using PdfSaver
            PdfSaver.savePdf(couponContent, fileName, eventIdentifier);
            System.out.println("PDF saved for coupon " + newCouponId);

            // Open the saved PDF using the system's default PDF viewer
            File pdfFile = new File("./TicketsPDF/" + eventIdentifier + "/" + fileName);
            if (java.awt.Desktop.isDesktopSupported() &&
                    java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
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
            System.out.println("Error saving coupon");
        }
    }


    private String sanitizeEventTitle(String title) {
        return title.replaceAll("[^a-zA-Z0-9]", "_");
    }


}
