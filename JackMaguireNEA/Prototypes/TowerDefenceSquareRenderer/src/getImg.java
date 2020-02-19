import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class getImg extends BufferedImage {
    private Dimension wh;

    public getImg (String fileName) {
        super(main.TILE_SIZE, main.TILE_SIZE, ATYPE_INT_RGB);

        wh = new Dimension(main.TILE_SIZE, main.TILE_SIZE);
		setImg(fileName);
    }
	
	
	public void setImg (String newFileName){
		try {
		    URL url = new URL("https://raw.githubusercontent.com/Epacnoss/NEAAssets/master/Tests/SquareRenderer/" + newFileName);

            Image temp = Resource
            temp = temp.getScaledInstance(main.TILE_SIZE, main.TILE_SIZE, Image.SCALE_SMOOTH);

             getGraphics().drawImage(temp, 0, 0, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}


// Source:
// https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
// https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
