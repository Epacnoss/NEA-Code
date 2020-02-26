package classes.render.mustBeRendered.Entity.turret.bullet;

import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustBeRendered.Entity.baseEntity.entityType;
import classes.render.mustBeRendered.Entity.enemy.enemyActual;
import classes.util.coordinate.Coordinate;
import classes.util.coordinate.dir;
import main.main;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class bulletActual extends Entity { //bullet class


    private static final long MOVE_GAP = 10; //gap between movements to slow down - we need to move slow, but without the gap, it is hard

    private boolean hit; //have i hit the enemy
    private boolean dead; //am i no longer needed
    private enemyActual enemyToHit;
    private int dmg; //damage to enemy
    private int spd; //speed

    private Thread runThread; //runThread

    public bulletActual(Coordinate startXYInArr, String fn, enemyActual enemy, int dmg_, int spd_, int range) {
        super(startXYInArr, fn, entityType.bullet, new Coordinate(main.TILE_WIDTH / 2, main.TILE_HEIGHT / 2));

        hit = false; //we haven't hit the enemy yet
        enemyToHit = enemy;
        dmg = dmg_;
        spd = spd_;

        int avImg = (getImg().getWidth() + getImg().getHeight()) / 4; //images are oversized and mainly blank, so the image size must be scaled down
        System.out.println(avImg);


        Runnable r = () -> {

            int av = main.BOUND * 2; //average Tile w and h size
            int distInPx = (spd * av) / SPEED_DIVISOR; //getting the distance in pixels
            if(distInPx <= 0)
                distInPx = spd; //if the distance in pixels is less than 0, default to the speed

            while(!isDone()) { //while we aren't done yet

                //region checks
                if(!enemyToHit.haveIBeenSpawnedYet()) //if the enemy hasn't been spawned yet - edge case but better than phantom bullets and enemies 'not spawning'
                {
                    dead = true; //we are dead
                    System.out.println("Wait - the what! Maybe I can see the future.");
                    return; //close loop
                }

                if(getXYInArr().equals(enemyToHit.getXYInArr()) || hit){ //if we are on the same tile or have hit
                    if(getXYInTile().distTo(enemyToHit.getXYInTile()) < main.BOUND || hit) //if we are close enough to the enemy or have hit.
                    {
                        enemyToHit.damage(dmg); //'hit' and damage the enmemy
                        hit = true; //we have hit, so the loop can close
                        System.out.println("Bullet and Enemy in a tree, K-I-L-L-I-N-G");
                        return;  //close loop
                    }
                }

                if(enemyToHit.isDone()) //if the enemy is done
                {
                    dead = true; //we are dead
                    System.out.println("Oof size: Medium");
                    return; //close loop
                }

                if(startXYInArr.distTo(enemyToHit.getXYInArr()) >= range + 1) //if the enemy is out of range, we use the start as they can't go too far from the turret.
                {
                    dead = true; //we are dead
                    System.out.println("Oof size: Large");
                    return; //close loop
                }
                //endregion

                try {
                    TimeUnit.MILLISECONDS.sleep(MOVE_GAP); //try to wait for the move gap

                } catch (Exception e) {
                    e.printStackTrace();
                }


                Coordinate onScrnTarget = Entity.turnFromArrToScrnPlusHalfTile(enemyToHit.getXYInArr(), Coordinate.ZERO); //get the target coordinate - accouting for centre of hitbox. Also we use 0, because we don't need a half-square tolerance, just to turn it from Arr to Scrn
                onScrnTarget = Entity.addHitBoxTolerances(onScrnTarget, CENTRE_OF_HITBOX); // add tolerances for hitbox



                dir direction = getXYOnScrn().directionTo(onScrnTarget); //find the direction
                double dist = getXYOnScrn().distTo(onScrnTarget); //get the distance to the enemy

                if(distInPx >= dist) { //If the dist we can go is greater than the dist to go, then we know we can get there
                    setXYInArr(enemyToHit.getXYInArr()); //set xyInArr of the enemy
                    setXYInTile(enemyToHit.getCentreOfHitbox()); //set xyInTile of the enemy centre of hitbox
                    hit = true; //we have hit
                    continue;
                }

                switch (direction) {
                    case N -> changeY(-distInPx); //JDK 13 (experimental) syntax
                    case S -> changeY(distInPx);
                    case E -> changeX(distInPx);
                    case W -> changeX(-distInPx);
                } //move

            }
        };

        runThread = new Thread(r);
        runThread.start(); //start the runThread

        Thread mouseCheckerThread = new Thread(() -> {
            while(!isDone()) {
                Point current = MouseInfo.getPointerInfo().getLocation();
                int x = current.x;
                int y = current.y; //get mouse position

                double dist = getXYOnScrn().distTo(new Coordinate(x, y));

                if(dist <= avImg / 2)
                    enemyToHit.aura();

                if(enemyToHit.isDone())
                    dead = true;

            }
        });
        mouseCheckerThread.start();
    }

    @Override
    public boolean isDone() {
        return hit || dead;
    }
}