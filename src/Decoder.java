import org.hxnry.rsp.watcher_server.api.util.ImageDecoder;
import org.hxnry.rsp.watcher_server.gui.encodes.Encodes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Decoder {

    public static void main(String[] args) {

       save("bet55", Encodes.bet_icon);
       save("whale55", Encodes.whale_icon);

    }

    public static BufferedImage save(String name, String encoded) {
        try {
            BufferedImage image = ImageDecoder.decodeToImage(encoded);
            File file = new File("C:\\Users\\Hxnry\\Documents\\Hxnry\\Iconify\\" + name + ".png");
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
