package classes.render.mustBeRendered.Entity.baseEntity;

import Gameplay.turrets.TurretFrame;
import classes.util.coordinate.Coordinate;
import classes.util.resources.ResourceManager;
import main.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.StringJoiner;

public abstract class Entity implements Comparable<Entity> { //Entity class - easier to collect all movement and rendering stuff into one abstract class

    protected static final int SPEED_DIVISOR = 250; //for movement

    protected Coordinate CENTRE_OF_HITBOX; //centre of hitbox for rendering in correct place

    private BufferedImage img; //img to render
    private Coordinate XYInArr, XYInTile; //XYInArr - In Tiles, XYInTile - In the current tile
    private entityType type; //Type of entity - used for getting url and centre of hitbox
    private String fqdn; //url

    private Image base; //base image

    public Entity(Coordinate XYInArr, String fn, entityType type, Coordinate XYInTile) {
        this.XYInArr = XYInArr;
        this.XYInTile = XYInTile;
        this.type = type;


        fqdn = "";

        switch (type) {
            case enemy:
                fqdn = main.ENEMY_IMAGES_LOC + fn; //using main.main file locations to get image
                CENTRE_OF_HITBOX = new Coordinate(main.ENEMY_WIDTH / 2, main.ENEMY_HEIGHT / 2); //using main.main for centre of hitbox
                break;
            case bullet:
                fqdn = main.BULLET_IMAGE_LOC + fn;
                CENTRE_OF_HITBOX = new Coordinate(main.BULLET_WIDTH / 2, main.BULLET_HEIGHT / 2);
                break;
            case turret:
                fqdn = main.TURRET_IMAGES_LOC + fn;
                CENTRE_OF_HITBOX = new Coordinate(main.TURRET_WIDTH / 2, main.TURRET_HEIGHT / 2);
            default:
                fqdn = fn;
                CENTRE_OF_HITBOX = Coordinate.ZERO.clone();
        }

        try {
            URL url = new URL(fqdn); //creating new url

            base = ResourceManager.getImg(url);

            if(base == null)
                throw new Exception("Img not found");
        } catch (Exception e) {
            base = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }

        Dimension wh = getWHOnType(type); //wh == width and height
        base = base.getScaledInstance(wh.width, wh.height, Image.SCALE_SMOOTH); // scaling image to correct size
        img = new BufferedImage(wh.width, wh.height, BufferedImage.TYPE_INT_ARGB); // creating the bufferedimage
        img.getGraphics().drawImage(base, 0, 0, null); //drawing the image
    }

    //region getters and setters
    public entityType getType() {
        return type;
    }

    public BufferedImage getImg() {
        return img;
    }

    public Coordinate getXYInArr() {
        return XYInArr;
    }

    public Coordinate getXYInTile() {
        return XYInTile;
    }

    public Coordinate getXYOnScrn () {
        int x;
        int y;
        try {
            int bigX = main.TILE_WIDTH * XYInArr.getX(); //x in tileS
            int bigY = main.TILE_HEIGHT * XYInArr.getY();

            int smallX = XYInTile.getX(); //x in tile
            int smallY = XYInTile.getY();

            x = bigX + smallX; //combined
            y = bigY + smallY;
        } catch (Exception e) {
            return Coordinate.NULL_COORD;
        }

        return new Coordinate(x, y);
    }
    //endregion

    protected void changeX (int dst) { //change the x, for movement
        int newXInTile = getXYInTile().getX() + dst; //start x in the tile
        int newXInArr = getXYInArr().getX(); // start x in array

        int oldYTile = getXYInTile().getY();
        int oldYArr = getXYInArr().getY();

        if(newXInTile < 0) { // checks to avoid rendering in wrong place and keeping right xyInArr
            newXInTile += main.TILE_WIDTH; // We can do this because if XInTile < 0, then by definition the dst is less than 0
            newXInArr--;

            if(newXInTile < 0)
                newXInTile = 0;
        }
        else if (newXInTile >= main.TILE_WIDTH)
        {
            newXInTile -= main.TILE_WIDTH;
            newXInArr++;

            if(newXInTile >= main.TILE_WIDTH)
                newXInTile = main.TILE_WIDTH - 1;
        }

        if(newXInArr < 0)
            newXInArr = 0;
        else if (newXInArr >= main.NUM_OF_TILES_WIDTH)
            newXInArr = main.NUM_OF_TILES_WIDTH - 1;

        XYInArr = new Coordinate(newXInArr, oldYArr);
        XYInTile = new Coordinate(newXInTile, oldYTile);
    }

    protected void changeY (int dst) {
        int newYInTile = getXYInTile().getY() + dst;
        int newYInArr = getXYInArr().getY();

        int oldXTile = getXYInTile().getX();
        int oldXArr = getXYInArr().getX();

        if(newYInTile < 0) {
            newYInTile += main.TILE_HEIGHT; // We can do this because if XInTile < 0, then by definition the dst is less than 0
            newYInArr--;

            if(newYInTile < 0)
                newYInTile = main.TILE_HEIGHT;
        }
        else if (newYInTile > main.TILE_HEIGHT)
        {
            newYInTile -= main.TILE_HEIGHT;
            newYInArr++;

            if(newYInTile >= main.TILE_HEIGHT)
                newYInTile = main.TILE_HEIGHT - 1;
        }

        if(newYInArr < 0)
            newYInArr = 0;
        else if (newYInArr >= main.NUM_OF_TILES_HEIGHT)
            newYInArr = main.NUM_OF_TILES_HEIGHT - 1;

        XYInArr = new Coordinate(oldXArr, newYInArr);
        XYInTile = new Coordinate(oldXTile, newYInTile);
    }

    private static Dimension getWHOnType (entityType type) { //get image width and height based on type
        switch (type) {
            case enemy:
                return new Dimension(main.ENEMY_WIDTH, main.ENEMY_HEIGHT); //No break statement needed as it becomes an unreachable statement
            case bullet:
                return new Dimension(main.BULLET_WIDTH, main.BULLET_HEIGHT);
            default:
                return new Dimension(main.TURRET_WIDTH, main.TURRET_HEIGHT);
        }
    }


    @Override
    public int compareTo(Entity o) { //compare to other entites - for sorting
        if(equals(o))
            return 0;

        return getXYOnScrn().compareTo(o.getXYOnScrn());
    }

    @Override
    public String toString() { //tostring method
        return new StringJoiner(", ", Entity.class.getSimpleName() + "[", "]")
                .add("XYInArr=" + XYInArr)
                .add("XYInTile=" + XYInTile)
                .add("type=" + type)
                .add("fqdn='" + fqdn + "'")
                .toString();
    }

    protected void setXYInTile(Coordinate XYInTile) { //set xyInArr and xyInTile
        this.XYInTile = XYInTile;
    }

    protected void setXYInArr(Coordinate XYInArr) {
        this.XYInArr = XYInArr;
    }


    protected static Coordinate turnFromArrToScrnPlusHalfTile (Coordinate original) { //get xy to be rendered to get the centre of the tile
        return turnFromArrToScrnPlusHalfTile(original, new Coordinate(main.TILE_WIDTH, main.TILE_HEIGHT));
    }

    protected static Coordinate turnFromArrToScrnPlusHalfTile (Coordinate original, Coordinate overrideHalf) { //same as above but with an overrided half
        int bigX = original.getX() * main.TILE_WIDTH;
        int bigY = original.getY() * main.TILE_HEIGHT;

        int smallX = overrideHalf.getX() / 2;
        int smallY = overrideHalf.getY() / 2;

        int x = bigX + smallX;
        int y = bigY + smallY;

        Coordinate fin = new Coordinate(x, y);

        return fin;
    }

    protected static Coordinate addHitBoxTolerances (Coordinate onScrn, Coordinate HITBOX) { //add tolerances for hitboxes, making the enetites render in the right place
        int currentX = onScrn.getX();
        int currentY = onScrn.getY();

        int addedX = -HITBOX.getX();
        int addedY = -HITBOX.getY();

        onScrn.setX(currentX + addedX);
        onScrn.setY(currentY + addedY);

        return onScrn;
    }

    public abstract boolean isDone ();
}