package classes.render.mustBeRendered.square;

import classes.util.resources.ResourceManager;
import main.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.net.URL;

public abstract class Square extends BufferedImage { //abstract square base class
    // the classes in the types package are all just extensions with pre-built file names

    public Square (String fn) {
        super(main.TILE_WIDTH, main.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB); //give the tile width and height

        try {
            URL earl = new URL(main.PATHS_IMAGES_LOC + fn); // get the url
            Image temp = ResourceManager.getImg(earl).getScaledInstance(main.TILE_WIDTH, main.TILE_HEIGHT, Image.SCALE_SMOOTH); //create a scaled image


            getGraphics().drawImage(temp, 0, 0, null); //draw the scaled image

        } catch (Exception e) {
            System.out.println("tHe ImAgE mAkEr iN tHe SqUaRe ClAsS hAs BeEn ViOlAtEd");
            e.printStackTrace();
            System.exit(1); //if there was an exception - exit from the program
        }

    }

}
