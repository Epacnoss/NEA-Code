package classes.render.mustBeRendered.Entity.enemy;

import Gameplay.player.PlayerManager;
import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustBeRendered.Entity.baseEntity.entityType;
import classes.render.mustBeRendered.square.squareCollection;
import classes.util.coordinate.Coordinate;
import classes.util.coordinate.dir;
import main.main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class enemyActual extends Entity { //enemy class

    private static final Color AURA_COLOUR = new Color(50, 163, 200);
    private static final Coordinate IN_TILE_TARGET; //static final variable for target

    public static final long MOVE_GAP = 20; //MS - same as bullet

    private enemyTemplate template; //template for statistics

    private int currentHP, currentSpd; //currentSpd and HP
    private int baseHP; // starting hit points - health

    private boolean hasHit; //have we hit the target
    private boolean isDead; //are we dead

    private squareCollection squares; //the squares
    private int currentStep; //the current step in the squares
    private Coordinate currentCoord; //the current coordinate

    private Thread runThread; //the runThread

    private int distInPx; //distance in pixels each run

    private boolean hasBeenSpawned; //have i been spawned yet

    private boolean auraOn;

    static {
        Coordinate targetInTileTemporaryVariable = Entity.turnFromArrToScrnPlusHalfTile(Coordinate.ZERO, new Coordinate(main.ENEMY_WIDTH / 2, main.ENEMY_HEIGHT / 2));
        targetInTileTemporaryVariable = addHitBoxTolerances(targetInTileTemporaryVariable, new Coordinate(main.ENEMY_WIDTH, main.ENEMY_HEIGHT));
        IN_TILE_TARGET = targetInTileTemporaryVariable;
    }

    public enemyActual(enemyTemplate eTemplate, squareCollection squares, PlayerManager pm) {
        super(squares.getStart(), eTemplate.getFn(), entityType.enemy, IN_TILE_TARGET); // super all important variables

        template = eTemplate; //set variables
        currentHP = template.getHp(); //get hit points from template
        baseHP = currentHP;

        currentSpd = template.getSpd();

        currentStep = 0;
        currentCoord = squares.getEnemyPath().get(currentStep); //get current step in path

        this.squares = squares;

        int av = main.BOUND * 2; //average tile width and height
        auraOn = false;

        distInPx = (currentSpd * av) / SPEED_DIVISOR; //find distance in pixels
        System.out.println("DIP: " + distInPx);
        if(distInPx <= 0)
            distInPx = currentSpd; //if less than 0, default to speed

        hasHit = false; //we haven't hit the goal
        isDead = false; //we aren't dead
        hasBeenSpawned = false; //we haven't been spawned


        Runnable r = () -> {
            if(pm.isDone()) //if the playermanager has died or is done - stop
                return;

            long current = System.currentTimeMillis(); //the current time in ms

            if(pm.isDone()) //again - if the playermanager is done - stop
                return;


            while(!isDone() && !pm.isDone()) { //while we aren't done and neither is the player
                if (currentStep >= squares.getEnemyPath().size()) //if we have reached the final step
                {
                    hasHit = true; //we have hit
                    System.out.println("Ah-Ha! It appeareth that Tybalteth hath won this round, and Romeo hath beeneth 'pwned'");
                    return;
                }
                if (currentHP <= 0) //if we are dead
                {
                    isDead = true; //we are dead
                    System.out.println("Ah-Ha! Tybalt hath been 'pwned', but noteth by fairest Romeo, but by Ninja, the newesth recruitth of Mixerth.");
                    return;
                }

                //region move gap
                long diff = System.currentTimeMillis() - current; //difference between now and previous runthrough - to account for the time taken to move in the move gap
                current = System.currentTimeMillis();

                if (diff < 0)
                    diff = 0;
                if (diff > MOVE_GAP)
                    diff = MOVE_GAP;


                try {
                    TimeUnit.MILLISECONDS.sleep(MOVE_GAP - diff); //wait for the movegap
                } catch (InterruptedException e) {
                    System.out.println("Enemy move gap interrupted");
                }
                //endregion

                currentCoord = squares.getEnemyPath().get(currentStep).clone(); //get the currentCoordinate
                Coordinate onScrnTarget = Entity.turnFromArrToScrnPlusHalfTile(currentCoord); //get the onscreen (in pixels) target
                onScrnTarget = Entity.addHitBoxTolerances(onScrnTarget, CENTRE_OF_HITBOX); //add tolerance for hitbox


                dir direction = getXYOnScrn().directionTo(onScrnTarget); //get direction
                double dist = getXYOnScrn().distTo(onScrnTarget); //get distance

                if (distInPx >= dist) { //If the dist we can go is greater than the dist to go, then we know we can get there
                    setXYInArr(currentCoord); //set the XYInArr coordinate to be right - so we can move to the next step
                    setXYInTile(IN_TILE_TARGET); //move to correct place in tileI
                    currentStep++; //increment step
                    continue;
                }

                switch (direction) {
                    case N -> changeY(-distInPx);
                    case S -> changeY(distInPx);
                    case E -> changeX(distInPx);
                    case W -> changeX(-distInPx);
                } //move


            }

            System.out.println("Enemy hath beeneth `dn`");
        };
        runThread = new Thread(r); //create runthread
        runThread.start(); //start runThread

        hasBeenSpawned = true; //we have been spawned
        main.SOUNDS.get("Spawn.wav").start(); //play the spawn sound
    }

    public void aura () {
        auraOn = true;
    }

    public boolean isDead () {
        return isDead;
    } // are we dead

    @Override
    public boolean isDone () {
        return isDead || hasHit;
    } //are we done - have we hit or are we dead

    public void damage (int dmg) {
        currentHP -= dmg; //damage the enemy - called by bullet
        System.out.println("ah, alas fair game, i may soonest be speaketh my last farewell. " + currentHP);
    }

    public enemyTemplate getTemplate() {
        return template;
    }


    public boolean haveIBeenSpawnedYet () {
        return hasBeenSpawned;
    }

    @Override
    public BufferedImage getImg() { //get the image to be rendered
        BufferedImage base = super.getImg(); //base image
        int newWidth = base.getWidth();
        int newHeight = base.getHeight();
        if(auraOn)
        {
            newWidth += 6;
            newHeight += 6;
        }


        BufferedImage hpBar = new BufferedImage(newWidth, 15, BufferedImage.TYPE_INT_ARGB); //hpbar image

        BufferedImage newOne = new BufferedImage(newWidth, newHeight + 15, BufferedImage.TYPE_INT_ARGB); // final image

        double HPFraction = ((double) currentHP) / ((double) baseHP); //fraction of hp remaining
        int cutOff = ((int) Math.floor(newOne.getWidth() * HPFraction)); //converted to pixels

        int grn = Color.GREEN.getRGB();
        int red = Color.RED.getRGB();

        for (int x = 0; x < hpBar.getWidth(); x++) { //for all of the width
            for (int y = 0; y < 10; y++) { // for all of the height
                if(x <= cutOff)
                    hpBar.setRGB(x, y, grn); //if the x is before the cutoff, draw green
                else
                    hpBar.setRGB(x, y, red); //else draw red
            }
        }

        if(!auraOn) {
            newOne.getGraphics().drawImage(hpBar, 0, 0, null); //draw the hpbar in the new image
            newOne.getGraphics().drawImage(base, 0, 15, null); //draw the base image onto the new image
        } else {
            newOne.getGraphics().drawImage(hpBar, 0, 3, null); //draw the hpbar in the new image
            newOne.getGraphics().drawImage(base, 0, 18, null); //draw the base image onto the new image

            int rgb = AURA_COLOUR.getRGB();
            for (int x = 0; x < 3; x++) { //left
                for (int y = 0; y < newHeight; y++) {
                    newOne.setRGB(x, y, rgb);
                }
            }
            for (int y = newHeight - 4; y < newHeight; y++) { //bottom
                for (int x = 0; x < newWidth; x++) {
                    newOne.setRGB(x, y, rgb);
                }
            }
            for (int x = newWidth - 4; x < newWidth; x++) { //right
                for (int y = 0; y < newHeight; y++) {
                    newOne.setRGB(x, y, rgb);
                }
            }
            for (int y = 0; y < 3; y++) { //top
                for (int x = 0; x < newWidth; x++) {
                    newOne.setRGB(x, y, rgb);
                }
            }
        }


        auraOn = false;
        return newOne; //return it
    }

    public Coordinate getCentreOfHitbox () { //get the centre of the hitbox
        return CENTRE_OF_HITBOX;
    }

    public void stop () {
        isDead = true;
        hasHit = true;
    }
}