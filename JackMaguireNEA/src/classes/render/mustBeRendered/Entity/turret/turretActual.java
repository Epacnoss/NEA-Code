package classes.render.mustBeRendered.Entity.turret;

import Gameplay.player.PlayerManager;
import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustBeRendered.Entity.baseEntity.entityType;
import classes.render.mustBeRendered.Entity.enemy.enemyActual;
import classes.render.mustBeRendered.Entity.turret.bullet.bulletActual;
import classes.util.coordinate.Coordinate;
import main.main;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class turretActual extends Entity { //turret class

    private turretTemplate turret; //template
    private ArrayList<Entity> shotsFired; //shots that have been fired

    private long differenceMs; //target firing difference
    private boolean hasToWait;

    private ArrayList<Entity> entities; //enemies to hit

    private Thread runThread; //runThread
    private boolean stillWorking;

    public turretActual(Coordinate XYInArr, turretTemplate turret, PlayerManager pm) {
        super(XYInArr, turret.getFn(), entityType.turret, new Coordinate(main.TURRET_X_ON_TILE, main.TURRET_Y_ON_TILE)); //super
        shotsFired = new ArrayList<>(); //init shots
        this.turret = turret; //set template
        stillWorking = true;


        differenceMs = ((long) Math.floor(turret.getDiffBetweenFiring())); //set difference
        hasToWait = false;

        entities = new ArrayList<>(); //init entities


        Runnable r = () -> { //runnable for thread

            while(!pm.isDone() && stillWorking) { //whilst the playermanager isn't done
                try {
                    if(hasToWait)
                        TimeUnit.MILLISECONDS.sleep(differenceMs); //wait for ability to shoot
                    else
                        hasToWait = true;

                } catch (InterruptedException e) {
                    System.out.println("Cooldown violated.");
                }

                ArrayList<enemyActual> enemies = enemyFilter.filterEnemies(((ArrayList<Entity>) entities.clone()), turret.getRangeInt(), getXYInArr().clone()); //get enemies, and filter them

                if(enemies.size() != 0) //if there aren't 0 enemies to hit
                {
                    enemyActual e = enemies.get(0); //pick one
                    bulletActual b = new bulletActual(getXYInArr().clone(), turret.getBullet_fn(), e, turret.getDmgInt(), turret.getBulletSpd(), turret.getRangeInt()); //create a bullet
                    shotsFired.add(b); //add bullet to list
                }else
                    hasToWait = false;


                for (Entity entity : ((ArrayList<Entity>) shotsFired.clone())) //for all of the bullets
                {
                    bulletActual ba = ((bulletActual) entity); //temporary variable that is a bulletActual

                    if(ba.isDone()) //if it is done
                    {
                        shotsFired.remove(entity); //remove it from render list
                        System.out.println("Bullet removed");
                    }
                }
            }
        };

        runThread = new Thread(r);
        runThread.start(); //start thread
    }

    public void setEnemies (ArrayList<Entity> enemies) { //update enemies
        entities = enemies;
    }

    public ArrayList<Entity> getShotsFired() { //get render list
        return shotsFired;
    }

    public turretTemplate getTurret() { //get template
        return turret;
    }

    public void noLongerWorking () {
        stillWorking = false;
    }

    @Override
    public String toString() { //toString with coordinate, name, cost, and resale value
        String coord = getXYInArr().toString();
        String name = getTurret().getName();
        int cost = getTurret().getCost();
        int resaleValue = getTurret().getSellValue();

        StringJoiner joiner = new StringJoiner(", ", turretActual.class.getSimpleName() + "[", "]");

        joiner.add(coord);
        joiner.add("name='" + name + "'");
        joiner.add("initalCost=" + cost);
        joiner.add("sellValue=" + resaleValue);

        return joiner.toString();
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
