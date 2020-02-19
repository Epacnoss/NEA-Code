import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;


public class getImgScaledIndep extends BufferedImage {

    private Dimension scale;

    public getImgScaledIndep(String fileName, int w, int h) {
        super(w, h, TYPE_INT_ARGB);
        scale = new Dimension(w, h);
        setImg(fileName);

    }


    public void setImg (String nom){
        try {
            URL url = new URL("https://raw.githubusercontent.com/Epacnoss/NEAAssets/master/Tests/SquareRenderer/" + nom);

            Image temp = ImageIO.read(url);

            temp = temp.getScaledInstance(scale.width, scale.height, SCALE_SMOOTH);

            getGraphics().drawImage(temp, 0, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


// Source:
// https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
// https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
