package dk.easv.ticketsys.bll.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfSaver {
    private final static String BASE_FILEPATH = "./TicketsPDF/";

    public static void savePdf(Node node, String file, String eventIdentifier) {
        // Create the subdirectory using the event identifier
        String directoryPath = BASE_FILEPATH + eventIdentifier + "/";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Use SnapshotParameters with transparent fill to capture the node without a white background
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        WritableImage writableImage = node.snapshot(params, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        try (PDDocument document = new PDDocument()) {
            // Create a PDF page with dimensions matching the node
            PDPage page = new PDPage(new PDRectangle((float) writableImage.getWidth(), (float) writableImage.getHeight()));
            document.addPage(page);

            // Save a temporary PNG file (PNG supports transparency)
            File tempImageFile = File.createTempFile("nodeSnapshot", ".png");
            ImageIO.write(bufferedImage, "png", tempImageFile);

            PDImageXObject pdfImage = PDImageXObject.createFromFile(tempImageFile.getAbsolutePath(), document);

            // Draw the image on the page without scaling (occupies entire page)
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdfImage, 0, 0, (float) writableImage.getWidth(), (float) writableImage.getHeight());
            }

            // Save the document in the subdirectory
            document.save(directoryPath + file);
            tempImageFile.delete();

        } catch (IOException e) {
            System.err.println("Failed to save Node snapshot as PDF: " + e.getMessage());
        }
    }
}
