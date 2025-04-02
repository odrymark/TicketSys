package dk.easv.ticketsys.bll.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfSaver {
    private final static String FILEPATH = "./TicketsPDF/";


    public static void savePdf(Node node, String file) {
        WritableImage writableImage = node.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            File dir = new File(FILEPATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File tempImageFile = File.createTempFile("nodeSnapshot", ".png");
            ImageIO.write(bufferedImage, "png", tempImageFile);

            PDImageXObject pdfImage = PDImageXObject.createFromFile(tempImageFile.getAbsolutePath(), document);

            float scale = Math.min(page.getMediaBox().getWidth() / (float) bufferedImage.getWidth(),
                    page.getMediaBox().getHeight() / (float) bufferedImage.getHeight());
            float imageWidth = bufferedImage.getWidth() * scale;
            float imageHeight = bufferedImage.getHeight() * scale;
            float x = (page.getMediaBox().getWidth() - imageWidth) / 2;
            float y = (page.getMediaBox().getHeight() - imageHeight) / 2;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdfImage, x, y, imageWidth, imageHeight);
            }

            document.save(FILEPATH + file);
            tempImageFile.delete();

        } catch (IOException e) {
            System.err.println("Failed to save Node snapshot as PDF: " + e.getMessage());
        }
    }
}
