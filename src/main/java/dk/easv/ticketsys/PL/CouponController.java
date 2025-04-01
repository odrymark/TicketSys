package dk.easv.ticketsys.PL;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import dk.easv.ticketsys.be.Event;
import dk.easv.ticketsys.be.SpecialTicket;
import dk.easv.ticketsys.be.TicketType;
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

public class CouponController {

    @FXML private Label couponEvent;
    @FXML private Label validDate;
    @FXML private Label couponLocation;
    @FXML private Label couponHolder;
    @FXML private Label holderMail;    // Displays the generated coupon code
    @FXML private Label termsLabel;
    @FXML private ImageView couponBarcode;
    @FXML private ImageView couponQrCode;

    // Label to display the selected coupon type
    @FXML private Label couponTypeLabel;

    private Event event;
    private TicketType couponType;
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

    private void updateCouponTypeUI() {
        if (couponType != null) {
            couponTypeLabel.setText(couponType.getName());
        } else {
            couponTypeLabel.setText("Not Selected");
        }
    }

    /**
     * Updates the coupon UI
     */
    private void updateUI() {
        if (event != null) {
            couponEvent.setText(event.getTitle());
            couponLocation.setText(event.getLocation());
            validDate.setText(event.getStartDate());
            couponHolder.setText("Customer Name");
          //TODO mail from data
            holderMail.setText("mail");

            updateCouponTypeUI();

            String couponInfo = buildCouponInfo(event);
            generateBarcode(couponInfo);
            generateQRCode(couponInfo);
        }
    }

    private String buildCouponInfo(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Coupon for: ").append(event.getTitle()).append("\n")
                .append("Location: ").append(event.getLocation()).append("\n")
                .append("Valid Until: ").append(event.getStartDate());
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
            int width = 300, height = 80;
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
        String couponInfo = buildCouponInfo(event);
        byte[] qrCodeData = generateQRCodeData(couponInfo);
        String barCodeData = generateBarCodeData(couponInfo);

        String description = "Discount Coupon";
        int eventID = event.getId();

        SpecialTicket coupon = new SpecialTicket(0, description, eventID, qrCodeData, barCodeData);
        int newCouponId = bllManager.uploadNewCoupon(coupon);
        if (newCouponId > 0) {
            coupon.setId(newCouponId);
            System.out.println("Coupon saved with ID: " + newCouponId);
        } else {
            System.out.println("Error saving coupon");
        }
    }
}
