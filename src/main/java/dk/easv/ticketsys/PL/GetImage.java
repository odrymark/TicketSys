package dk.easv.ticketsys.PL;

import dk.easv.ticketsys.Main;
import dk.easv.ticketsys.be.Event;

import java.io.InputStream;

public class GetImage {

    public InputStream getImage(Event event) {
        InputStream imageStream = null;
        System.out.println("GetImg: " + event.getImgSrc());
        if (event.getImgSrc() != null && !event.getImgSrc().isEmpty()) {
            imageStream = Main.class.getResourceAsStream(event.getImgSrc());
        }
        if (imageStream == null)
            imageStream = Main.class.getResourceAsStream("/dk/easv/ticketsys/Images/noImg.jpg");
        return imageStream;
    }


}
