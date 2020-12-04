package classes.render.mustRender;

import classes.util.CfgReader.CfgReader;
import Gameplay.player.PlayerManager;
import Gameplay.waves.waveManager;
import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustBeRendered.Entity.enemy.enemyActual;
import classes.render.mustBeRendered.square.Square;
import classes.util.Leaderboard.SaveSystem;
import main.main;

import javax.swing.*;

import classes.render.mustBeRendered.square.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class canvas extends JComponent { //canvas class to render everything

    private static final Predicate<Entity> NULL_PREDICATE = e -> e == null; //nullPredicate - a check that returns true if the entity is null

    private squareCollection sqc; //squares
    private ArrayList<Entity> entities; //all entites

    private boolean finishedRendering; //has it finished rendering

    private boolean playerIsDead; //is the player dead
    private boolean deathStarted; //has the death sequence started
    private Thread deadThread; //death thread

    private boolean playerHasWon; //has the player won
    private boolean winStarted; // has the win sequence started
    private Thread winThread; //win thread


    private BufferedImage vCanvas; //temporary canvas

    private static final BufferedImage[] RED_IMGS; //shades of red for loss
    private static final BufferedImage[] GREEN_IMGS; //shades of green for victory
    private static final long DIFFERENCE_BETWEEN_COLOUR_CHANGE = 0; //difference between colour changes - ms
    private static final long DIFFERENCE_BETWEEN_SQUARES = 0; //difference between square changes - ms

    static { //static initializer
        Color[] reds = new Color[256]; // create reds temp for colours
        Color[] grns = new Color[256]; // create greens temp for colours
        for (int i = 0; i < reds.length; i++)
            reds[i] = new Color(i, 0, 0); //for each of the reds - add a slowly redder tone
        for (int i = 0; i < grns.length; i++)
            grns[i] = new Color(0, i, 0); //same for all of the greens

        RED_IMGS = new BufferedImage[256]; //init the actual reds
        GREEN_IMGS = new BufferedImage[256]; //init the actual greens

        for (int i = 0; i < RED_IMGS.length; i++) {
            BufferedImage red = new BufferedImage(main.TILE_WIDTH, main.TILE_HEIGHT, BufferedImage.TYPE_INT_RGB); //create a temporary red bufferedImage
            BufferedImage grn = new BufferedImage(main.TILE_WIDTH, main.TILE_HEIGHT, BufferedImage.TYPE_INT_RGB); //create a temporary green bufferedImage

            for (int y = 0; y < main.TILE_HEIGHT; y++) { //for the whole width
                for (int x = 0; x < main.TILE_WIDTH; x++) { //for the whole height
                    red.setRGB(x, y, reds[i].getRGB()); // for all of the image - set the rgb at the x and y to be either red or green
                    grn.setRGB(x, y, grns[i].getRGB());
                }
            }

            RED_IMGS[i] = red; //set the red or green image
            GREEN_IMGS[i] = grn;
        }

        System.out.println("IMGS CREATED");
    }


    public canvas (int stage, PlayerManager pm, waveManager wm) { //constructor for the canvas class
        CfgReader sqpCfg = new CfgReader(main.MAPS_LOC + "stg" + stage + ".cfg"); //create new cfgReader for the map details for the squareParser
        squareParser sqp = new squareParser(sqpCfg); //create a new squareParser using the cfgReader
        sqc = new squareCollection(sqp); //create a new squareCollection using the squareParser
        entities = new ArrayList<>(); //init entities list

        finishedRendering = true; //we aren't currently rendering so finishedRendering is true

        playerIsDead = false; //the player cannot start dead
        deathStarted = false; //without the player having died - the death cannot have started

        pm.addBooleanChangeListener(e -> { //adding a changeListener for if the player is dead or has won
            if(pm.isDead()) //if the player manager is dead - set the local death variable true
                playerIsDead = true;
            if(pm.haveIWon()) //if the player manager has won - set the local win variable true
                playerHasWon = true;
        });

        Runnable dead = () -> { //death runnable

            int score = wm.getEnemiesSpawned() * (pm.startHearts() - pm.getHearts()); //get the score

            StringJoiner lossTxtBuilder = new StringJoiner("\n");
            lossTxtBuilder.add("You lost...");
            lossTxtBuilder.add("Score: " + score);
            String txt = lossTxtBuilder.toString(); //loss text

            dieOrWin(RED_IMGS, txt);

        };
        deadThread = new Thread(dead); // create deathThread

        Runnable won = () -> { //win runnable - same as deathThread, only difference is message and colour
            int score = pm.getMoney() + pm.getHearts() * 10;
            SaveSystem.setHighScore(stage, score);

            StringJoiner winTxtBuilder = new StringJoiner("\n");
            winTxtBuilder.add("You won!");
            winTxtBuilder.add("Score: " + score);
            String txt = winTxtBuilder.toString(); //win message

            dieOrWin(GREEN_IMGS, txt);

        };
        winThread = new Thread(won); //create winThread
    }

    public void setEntities (ArrayList<Entity> entities_) { //set entites
        finishedRendering = false; //we need to render so we say we haven't finished
        entities.clear(); //clear existing list
        entities = (ArrayList<Entity>) entities_.clone(); //clone new list and set it to the entities list
    }

    @Override
    protected void paintComponent(Graphics g) { //draw method
        super.paintComponent(g); //super
        finishedRendering = false; //if it hasn't already been set - we have not finished rendering

        if(!playerIsDead && !playerHasWon) { //if the player isn't dead and hasn't won
            vCanvas = new BufferedImage( //set the vCanvas
                    (getWidth() == 0 ? main.WINDOW_WIDTH : getWidth()),
                    (getHeight() == 0 ? main.WINDOW_HEIGHT : getHeight()),
                    BufferedImage.TYPE_INT_ARGB);

            int xOnScrn = 0; //x and y on screen
            int yOnScrn = 0;

            int w = main.TILE_WIDTH; //tile widths and heights
            int h = main.TILE_HEIGHT;


            Square[][] squares = sqc.getSquares(); //get all of the squares from the squareCollection

            for(int yInArr = 0; yInArr < squares[0].length; yInArr++) { // for all of the squares
                for (int xInArr = 0; xInArr < squares.length; xInArr++) {

                    Image img = squares[xInArr][yInArr]; //draw the square image - we don't need a getImg because the square extends from BufferedImage
                    vCanvas.getGraphics().drawImage(img, xOnScrn, yOnScrn, null); //draw the image

                    xOnScrn += w; //add the width of a square/tile to the onscrn x

                }
                xOnScrn = 0; //at the end, reset the x and add the height to the y
                yOnScrn += h;
            }

            ArrayList<Entity> temp = ((ArrayList<Entity>) entities.clone()); //temporary cloned list to avoid concurrentModificationExceptions

            temp.removeIf(NULL_PREDICATE); // remove all entities if they follow the nullpredicate - if they are null

            if(temp.size() != 0) { //if we have more than one entity
                for (Entity entity : temp) {

                    try {
                        Image img = null; //get the image of the enemy

                        switch (entity.getType()) {
                            case enemy -> { //check that if the enemy hasn't been spawned yet - don't render it
                                enemyActual casted = ((enemyActual) entity);

                                if(casted.haveIBeenSpawnedYet())
                                    img = entity.getImg();

                                break;
                            }
                            default -> { //else just get the image
                                img = entity.getImg();
                                break;
                            }
                        }

                        if (img != null) { //if we have an image - it could be null if they enemy wasn't spawned
                            int x = entity.getXYOnScrn().getX();
                            int y = entity.getXYOnScrn().getY(); //get the x and y

                            vCanvas.getGraphics().drawImage(img, x, y, null); //draw the image
                        }
                    } catch (Exception e) {
                        System.err.println("ENTITY NOT FOUND"); //catch all exceptions
                        e.printStackTrace();
                    }
                }
            }

        }else {
            if(!deathStarted && !winStarted)
            {
                if(playerIsDead) {
                    deathStarted = true;
                    deadThread.start();
                } else if (playerHasWon) {
                    winStarted = true;
                    winThread.start();
                }
            }
        }

        if(g != null)
            g.drawImage(vCanvas, 0, 0, null);

        repaint();

        finishedRendering = true;
    }

    public boolean hasFinishedRendering () {
        return finishedRendering;
    }

    private void dieOrWin(BufferedImage[] imgs, String txt) {
        vCanvas.getGraphics().drawString(txt, main.TILE_WIDTH, main.TILE_HEIGHT);

        for (int y = 0; y < main.NUM_OF_TILES_HEIGHT; y++) {
            for (int x = 0; x < main.NUM_OF_TILES_WIDTH; x++) {
                for (int i = 0; i < 256; i++) {
                    vCanvas.getGraphics().drawImage(imgs[i], x * main.TILE_WIDTH, y * main.TILE_HEIGHT, null); //draw image
                    vCanvas.getGraphics().drawString(txt, main.TILE_WIDTH, main.TILE_HEIGHT);

                    repaint();

                    try {
                        TimeUnit.MILLISECONDS.sleep(DIFFERENCE_BETWEEN_COLOUR_CHANGE);
                    } catch (InterruptedException e) {
                        System.out.println("DIFF BETWEEN COLOUR CHANGE INTERRUPTED");
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(DIFFERENCE_BETWEEN_SQUARES);
                } catch (InterruptedException e) {
                    System.out.println("DIFF BETWEEN SQUARES CHANGE INTERRUPTED");
                }
            }
        }
    }
}


