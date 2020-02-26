package main;

import classes.util.CfgReader.CfgReader;
import Gameplay.player.PlayerManager;
import Gameplay.turrets.TurretManager;
import Gameplay.waves.waveManager;
import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustRender.canvas;
import classes.render.mustBeRendered.square.squareParser;
import classes.render.mustBeRendered.square.squareCollection;
import classes.util.coordinate.Coordinate;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class main {

    //For ease of file locations and packages, I have uploaded all images to a public github repo, the URL of which can be found as BASE_LOCATION on line 33. TODO: Update this line

    public static boolean windowHasStarted = false; //has the window been set visible yet

    public static final Pattern INT_REGEX = Pattern.compile("[0-9]+"); //regex for integer strings - any numbers with at least one digit
    public static final Pattern DBL_REGEX = Pattern.compile("[0-9]+\\.[0-9]+"); //regex for doubles - any numbers (>= 1 digit), a decimal point, and then more numbers

    //region URL locations
    private static final String BASE_LOCATION = "https://raw.githubusercontent.com/Epacnoss/NEAAssets/master/Actual/"; //base location of git repo for assets
    private static final String IMAGES_LOC = BASE_LOCATION + "images/"; //images location

    public static final String ENEMIES_LOC = BASE_LOCATION + "enemies/"; //enemy config locations
    public static final String TURRETS_LOC = BASE_LOCATION + "turrets/"; //turret config locations
    public static final String WAVES_LOC = BASE_LOCATION + "waves/"; //wave config locations
    public static final String MAPS_LOC = BASE_LOCATION + "maps/"; //map config location

    public static final String ENEMY_IMAGES_LOC = IMAGES_LOC + "enemies/"; //enemy images location
    public static final String TURRET_IMAGES_LOC = IMAGES_LOC + "turrets/"; //turret images location
    public static final String BULLET_IMAGE_LOC = IMAGES_LOC + "bullets/";

    public static final String PATHS_IMAGES_LOC = IMAGES_LOC + "paths/"; //square images location

    public static final String ICON_LOCATIONS = IMAGES_LOC + "icns/"; //icon location
    public static final String AUDIO_LOCATIONS = BASE_LOCATION + "audio/"; //sound location
    //endregion

    //region UI sizes
    private static final CfgReader stage; //stage config reader
    public static final int NUM_OF_TILES_WIDTH; //num of tiles width
    public static final int NUM_OF_TILES_HEIGHT; //num of tiles height

    /*
    squares example (S = Square, | = boundary y axis, _ = boundary x axis)


    _____________
    |S|S|S|S|S|S|
    _____________
    |S|S|S|S|S|S|
    _____________

    NUM_OF_TILES_WIDTH = 5
    NUM_OF_TILES_HEIGHT = 1
     */

    public static final int WINDOW_WIDTH; //overall window width and height
    public static final int WINDOW_HEIGHT;

    private static Dimension size; //Dimension - using above width and height

    public static final int TILE_HEIGHT; //px - window width / num of tiles width and same for height
    public static final int TILE_WIDTH;

    public static final int BOUND; //close enough bound

    //region widths and heights of entities
    public static final int TURRET_WIDTH;
    public static final int TURRET_HEIGHT;

    public static final int BULLET_WIDTH;
    public static final int BULLET_HEIGHT;

    public static final int ENEMY_WIDTH;
    public static final int ENEMY_HEIGHT;
    //endregion

    public static final int TURRET_X_ON_TILE; //turret x and y on a tile
    public static final int TURRET_Y_ON_TILE;

    //endregion

    //region fn arrays
    public static final String[] TURRET_FNS = {"wizard.cfg", "dropTower.cfg"}; // turret, enemy and enemy image file names
    public static final String[] ENEMY_FNS = {"fastButWeak.cfg", "slowButStrong.cfg"};

    public static final String[] ENEMY_IMG_FNS = {"skeleton_big.png", "bigButSlow_big.png"};
    //endregion

    public static final HashMap<String, Clip> SOUNDS; //all of the audio


    protected static boolean lvl (int no) { // level one method
        AtomicBoolean resultGiven = new AtomicBoolean(false);
        AtomicBoolean result = new AtomicBoolean(false);

        CfgReader level = new CfgReader(WAVES_LOC + "lvl" + no + ".cfg");
        String moneyStr = level.get("playerGets", "money").toString(); //money and hearts from level config file
        String heartsStr = level.get("playerGets", "hp").toString();

        int money = 1;
        int hearts = 1; //temporary variables - if they didn't match - we have these as a backup

        if(INT_REGEX.matcher(moneyStr).matches() && INT_REGEX.matcher(heartsStr).matches()) //if the regex doesn't match - just defaults else use string values
        {
            money = Integer.parseInt(moneyStr); //parse
            hearts = Integer.parseInt(heartsStr);
        }

        //region main window
        JFrame window = new JFrame("Apex Turrets"); // create JFrame

        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //set default close operation
        window.setLayout(new GridLayout(1, 2)); //set layout - one col for canvas, one for TurretFrame

        System.out.println("WINDOW CREATED: " + new Timestamp(System.currentTimeMillis()));


        PlayerManager pm = new PlayerManager(money, hearts); //create playerManager
        System.out.println("PM DONE: " + new Timestamp(System.currentTimeMillis()));

        squareCollection sqc = new squareCollection(new squareParser(new CfgReader(main.MAPS_LOC + "stg1.cfg"))); //create squares
        System.out.println("SQC DONE: " + new Timestamp(System.currentTimeMillis()));

        waveManager waves = new waveManager("lvl" + no + ".cfg", sqc, pm, window); //create waves
        System.out.println("WAVES DONE: " + new Timestamp(System.currentTimeMillis()));

        canvas c = new canvas(1, pm, waves); //create canvas
        window.add(c); //add to window
        System.out.println("CANVAS DONE: " + new Timestamp(System.currentTimeMillis()));

        TurretManager tm = new TurretManager(sqc, pm, window, no); //create turretManager and so turretFrame
        System.out.println("TM DONE: " + new Timestamp(System.currentTimeMillis()));

        window.pack(); //pack window
        window.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize()); //set size - full screen size
        window.setVisible(true); //set visible

        System.out.println("WINDOW DONE: " + new Timestamp(System.currentTimeMillis()));
        //endregion

        Thread runThread = new Thread(() -> { //runThread

            long current = System.currentTimeMillis(); //current time

            long delay = 10; //delay

            System.out.println("RUN THREAD STARTED - ACTUAL");
            windowHasStarted = true; //window has started = true - stuff can happen

            while (window.isVisible()) { //while (isVisible) - do this for the entire time we can see the window

                c.paint(c.getGraphics()); //call the paint function

                while(!c.hasFinishedRendering()) {
                    continue; //render
                }

                if (System.currentTimeMillis() - current > delay) { //if the gap is greater than the delay

                    ArrayList<Entity> enemyActuals = ((ArrayList<Entity>) waves.getEntites().clone()); //get the enemies
                    ArrayList<Entity> turretActuals = ((ArrayList<Entity>) tm.setEnemiesAndGetTurretsAndBullets(enemyActuals).clone()); //get the turrets

                    ArrayList<Entity> finalEnties = new ArrayList<>(); //create a new list
                    finalEnties.addAll(turretActuals);
                    finalEnties.addAll(enemyActuals); //add turrets and entities

                    c.setEntities(finalEnties); //set the entities

                    current = System.currentTimeMillis(); //reset current
                }
            }

            int playAgain = JOptionPane.showConfirmDialog(null, "Would you like to play again?");
            if(playAgain == JOptionPane.YES_OPTION)
            {
                result.set(true);
                resultGiven.set(true);
                System.out.println("Its replay time");
                return;
            }
            else
            {
                System.out.println("Au revoir");
                result.set(false);
                resultGiven.set(true);
                System.exit(0);
            }
        });

        runThread.start(); //start RunThread

        System.out.println("MAIN RUN THREAD STARTED: " + new Timestamp(System.currentTimeMillis()));

        while(!resultGiven.get())
            continue;

        return result.get();
    }

    static { //static initializer - for all the public static final variables that depend on each other, and for temp variables to space out code
        System.out.println("MAIN STATIC STARTING");

        //map is always stage 1 so...
        //always 3 levels so.....
        stage = new CfgReader(MAPS_LOC + "stg" + 1 + ".cfg"); //start the config reader

        NUM_OF_TILES_WIDTH = Integer.parseInt(stage.get("mapDeets", "rows").toString()); // get the tile widths and heights
        NUM_OF_TILES_HEIGHT = Integer.parseInt(stage.get("mapDeets", "cols").toString());


        Dimension wholeScreenSize = Toolkit.getDefaultToolkit().getScreenSize(); //get the screen size

        int wTemp = wholeScreenSize.width / 2; //get half of the width
        int hTemp = wholeScreenSize.height;   // and the full height


        WINDOW_WIDTH = wTemp - (wTemp % NUM_OF_TILES_WIDTH); //create the window width and height
        WINDOW_HEIGHT = hTemp - (hTemp % NUM_OF_TILES_WIDTH); //minus the remainder so the tiles line up with the edge
        size = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT); //create the size


        TILE_WIDTH = Math.floorDiv(WINDOW_WIDTH, NUM_OF_TILES_WIDTH); //set the tile width and height
        TILE_HEIGHT = Math.floorDiv(WINDOW_HEIGHT, NUM_OF_TILES_HEIGHT);

        BOUND = (TILE_WIDTH + TILE_HEIGHT) / 4; //create the bound

        TURRET_WIDTH = TILE_WIDTH / 3 * 2; // set the widths and heights for the images
        TURRET_HEIGHT = TILE_HEIGHT / 3 * 2;

        BULLET_WIDTH = TURRET_WIDTH * 2 / 3;
        BULLET_HEIGHT = TURRET_HEIGHT * 2 / 3;

        ENEMY_WIDTH = TILE_WIDTH * 3 / 2;
        ENEMY_HEIGHT = TILE_HEIGHT * 3 / 2;

        TURRET_X_ON_TILE = (TILE_WIDTH - TURRET_WIDTH) / 2;
        TURRET_Y_ON_TILE = (TILE_HEIGHT - TURRET_HEIGHT) / 2;

        //region sounds
        final String[] sounds = new String[] {"Spawn.wav"}; //get the sounds - currently there is but one
        SOUNDS = new HashMap<>(); //init hashMap

        for (String fn : sounds) { //for all of the sounds
            Clip c; //create a temporary clip variable
            try {
                URL url = new URL(AUDIO_LOCATIONS + fn); //get the URL

                AudioInputStream AIS = AudioSystem.getAudioInputStream(url); //get the AudioInputStream

                c = AudioSystem.getClip();
                c.open(AIS); //get the clip

                c.addLineListener(event -> { //track resets when done
                    long frames = AIS.getFrameLength(); //get the frames
                    double l = (frames+0.0) / AIS.getFormat().getFrameRate(); //get the length

                    long l2 = ((long) Math.floor(l)); //floor it

                    try {
                        TimeUnit.SECONDS.sleep(l2); // sleep for that length
                    } catch (InterruptedException e) {
                        System.out.println("Audio Sleeper interrupted.");
                    }

                    c.stop(); //stop it
                    c.setMicrosecondPosition(0l); //reset track

                });

                SOUNDS.put(fn, c); //add it
            } catch (Exception e) {
                System.err.println("Mamma mia - there is no sound....");
            }


        }

        //endregion

        System.out.println("MAIN STATIC DONE");
    }

    //region sorters
    public static ArrayList<Coordinate> quickCoord (ArrayList<Coordinate> listBase) //quick sorter for coordinates - same algorithm for the entites but just switch what we are comparing
    {
        ArrayList<Coordinate> list = ((ArrayList<Coordinate>) listBase.clone()); //clone the list to avoid concurrentModificationExceptions

        if(list.size() > 1) // if there is more than one element
        {

            Coordinate pivot = list.get(list.size() / 2); //get the pivot point - the centre
            ArrayList<Coordinate> less, more, equal;
            less = new ArrayList<>();
            more = new ArrayList<>(); //init arrayLists for more, less, and equal
            equal = new ArrayList<>();

            for(Coordinate x : list) //for each coordinate - x
            {
                if(x.compareTo(pivot) < 0) //if it is less - add it to the less list
                    less.add(x);
                else if (x.compareTo(pivot) == 0) //if it is equal - add it to the equal list
                    equal.add(x);
                else //else - it must be more - add it to the more list
                    more.add(x);

            }

            less = quickCoord(less); //recursively sort the less and more lists using this algorithm
            more = quickCoord(more);

            list.clear(); //clear the list

            list.addAll(less);
            list.addAll(equal);
            list.addAll(more); //add the less, more, and equal lists
        }

        return list; //return the sorted list, or just the singular item in a list for the later stages of recursion, or just few items to sort
    }

    public static ArrayList<Entity> quickEntity (ArrayList<Entity> listOriginal)
    {
        ArrayList<Entity> list = ((ArrayList<Entity>) listOriginal.clone());

        if(list.size() > 1)
        {

            Entity pivot = list.get(list.size() / 2);
            ArrayList<Entity> less, more, equal;
            less = new ArrayList<>();
            more = new ArrayList<>();
            equal = new ArrayList<>();

            for(Entity x : list)
            {
                if(x.compareTo(pivot) < 0)
                    less.add(x);
                else if (x.compareTo(pivot) == 0)
                    equal.add(x);
                else
                    more.add(x);

            }

            less = quickEntity(less);
            more = quickEntity(more);
            list.clear();

            list.addAll(less);
            list.addAll(equal);
            list.addAll(more);
        }

        return list;
    }
    //endregion
}
