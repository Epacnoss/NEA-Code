package classes.util.resources;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

public class ResourceManager { //imageManager - to mean less calls to get images from the web

    private static HashMap<URL, Image> ALL_IMAGES; //all the images
    private static HashMap<URL, ImageIcon> ALL_ICONS;

    static {
        ALL_IMAGES = new HashMap<>(); //init hashMap
        ALL_ICONS = new HashMap<>();
    }

    private ResourceManager () { //private constructor to avoid instantiation
    }

    public static Image getImg (URL url) { //getImg method
        if(ALL_IMAGES.containsKey(url)) //if we already have it - return it.
            return clone(ALL_IMAGES.get(url));

        //else - grab it and return it

        Image img; //img temp
        try {
            img = ImageIO.read(url); //read in image
            ALL_IMAGES.put(url, img); // put in hashMap
        } catch (Exception e) {
            return null; //if we can't find it return null
        }

        return getImg(url);
    }

    private static Image clone (Image original) { //cloning method to avoid modification of base image
        BufferedImage newOne = new BufferedImage(original.getWidth(null), original.getHeight(null), BufferedImage.TYPE_INT_ARGB); //new image
        newOne.getGraphics().drawImage(original, 0, 0, null); //draw image on top
        return newOne; //return new image
    }


    public static ImageIcon getIcon (URL url) {
        if(ALL_ICONS.containsKey(url))
            return clone(ALL_ICONS.get(url));

        ImageIcon temp;
        try {
            temp = new ImageIcon(url);
            ALL_ICONS.put(url, temp);
        }catch (Exception e) {
            return null;
        }

        return getIcon(url);
    }

    private static ImageIcon clone (ImageIcon original) {
        Image img = original.getImage();
        img = clone(img);
        ImageIcon newOne = new ImageIcon(img);
        return newOne;
    }


}
