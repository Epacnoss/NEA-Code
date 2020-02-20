package classes.render.mustBeRendered.Entity.turret;

import Gameplay.player.PlayerManager;
import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustBeRendered.Entity.baseEntity.entityType;
import classes.render.mustBeRendered.Entity.enemy.enemyActual;
import classes.render.mustBeRendered.Entity.turret.bullet.bulletActual;
import classes.util.coordinate.Coordinate;
import main.main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class turretActual extends Entity { //turret class

    public static final int MAX_UPGRADES = 10;

    private turretTemplate turret; //template
    private ArrayList<Entity> shotsFired; //shots that have been fired

    private long differenceMs; //target firing difference
    private boolean hasToWait;
    private int damage;

    private ArrayList<Entity> entities; //enemies to hit

    private Thread runThread; //runThread
    private boolean stillWorking;

    private int upgradeFactor;
    private Random rnd;

    public turretActual(Coordinate XYInArr, turretTemplate turret, PlayerManager pm) {
        super(XYInArr, turret.getFn(), entityType.turret, new Coordinate(main.TURRET_X_ON_TILE, main.TURRET_Y_ON_TILE)); //super
        shotsFired = new ArrayList<>(); //init shots
        this.turret = turret; //set template
        stillWorking = true;

        upgradeFactor = 1;
        rnd = new Random();
        damage = turret.getDmgInt();


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
                    bulletActual b = new bulletActual(getXYInArr().clone(), turret.getBullet_fn(), e, damage, turret.getBulletSpd(), turret.getRangeInt()); //create a bullet
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
        int resaleValue = getTurret().getSellValue();

        return new StringJoiner(", ", niceToCamelCase(name) + "[", "]")
                .add(coord)
                .add("damage=" + damage)
                .add("cooldown=" + differenceMs + "ms")
                .add("lvl=" + upgradeFactor)
                .add("sellFor=" + resaleValue)
                .toString();
    }

    @Override
    public boolean isDone() {
        return false;
    }

    public void doUpgrade () {
        upgradeFactor++;
        int factor = 0;
        while(factor == 0)
            factor = rnd.nextInt(upgradeFactor);
        boolean upgradeDamage = rnd.nextBoolean();
        boolean multiply = rnd.nextBoolean();

        if(factor == 1 && upgradeDamage)
            multiply = false;
        if(!upgradeDamage)
            factor *= 100;

        if(multiply) {
            if (upgradeDamage)
                damage *= factor;
            else
                differenceMs /= factor;
        } else {
            if(upgradeDamage)
                damage += factor;
            else
                differenceMs -= factor;
        }
    }

    public int getUpgradeFactor() {
        return upgradeFactor;
    }

    @Override
    public BufferedImage getImg() {
        BufferedImage base = super.getImg();
        BufferedImage newOne = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        newOne.getGraphics().drawImage(base, 0, 0, null);
        newOne.getGraphics().drawString("Lvl: " + upgradeFactor, 5, main.TILE_HEIGHT / 2);
        return newOne;
    }

    private static String niceToCamelCase(String original) {
        String origin = original;
        if(origin.contains("'")) {
            while(origin.contains("'"))
            {
                String temporary = origin;
                origin = "";
                int apostrophePos = temporary.indexOf("'");

                origin = origin + temporary.substring(0, apostrophePos);
                origin += temporary.substring(apostrophePos + 1); //to avoid all apostrophes
            }
        }
        String[] wds = origin.split(" ");
        String finalStr = "";
        for(int i = 0; i < wds.length; i++) {
            String newVersion = "";
            String wd = wds[i];

            if(i == 0)
                newVersion = wd.toLowerCase();
            else {
                String letter1 = (wd.charAt(0) + "").toUpperCase();
                String rest = wd.substring(1).toLowerCase();
                newVersion = letter1 + rest;
            }

            finalStr += newVersion;
        }

        return finalStr;
    }
}