package Gameplay.turrets;

import Gameplay.player.PlayerManager;
import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustBeRendered.Entity.baseEntity.entityType;
import classes.render.mustBeRendered.square.squareCollection;
import classes.render.mustBeRendered.Entity.turret.*;
import classes.util.coordinate.Coordinate;
import main.main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class TurretManager {

    private ArrayList<Entity> turrets; //all the turrets
    private turretDictionary dictionary; //a turretDictionary

    private squareCollection sqc; //all of the squares

    private ArrayList<Coordinate> turretSquaresAll;
    private ArrayList<Coordinate> turretSquaresFree; //all of the free turretSquares, usedTurrets squares, and just all of the squares
    private ArrayList<Coordinate> turretSquaresUsed;

    private PlayerManager pm; //the playerManager

    private TurretFrame tf; //the turretFrame
    private ArrayList<Entity> bullets; //all of the bullets and enemies
    private ArrayList<Entity> enemies;

    private Thread runThread; //the runThread


    public TurretManager(squareCollection sqc_, PlayerManager pm, JFrame window, int lvl) {
        sqc = sqc_; //set the squareCollection

        dictionary = new turretDictionary(main.TURRET_FNS); //create the dictionary

        turretSquaresAll = sqc.getAvailableTurretSquares();
        turretSquaresFree = (ArrayList<Coordinate>) turretSquaresAll.clone(); //set the squares for the turrets
        turretSquaresUsed = new ArrayList<>();

        tf = new TurretFrame(lvl, turretSquaresUsed, turretSquaresFree, new Dimension(main.WINDOW_WIDTH, main.WINDOW_HEIGHT), dictionary.getTurrets().values(), pm, window, this); //create the turretFrame
        this.pm = pm; //set the playerManager

        turrets = new ArrayList<>();
        bullets = new ArrayList<>(); //init the lists
        enemies = new ArrayList<>();

        window.add(tf); //add the turretFrame panel to the window

        Runnable r = () -> { //runnable for the runThread
            while(!pm.isDone()) { //whilst the playerManager hasn't won nor died
                main.quickCoord(turretSquaresUsed); //sort the lists
                main.quickEntity(turrets);


                bullets.clear(); //clear the bullets
                if(turrets.size() > 0) {
                    main.quickEntity(turrets);
                    main.quickCoord(turretSquaresUsed); //sort the lists again


                    for (Entity e : ((ArrayList<Entity>) turrets.clone())) { //for each of the turrets

                        if (e.getType().equals(entityType.turret)) { //if it is a turret
                            turretActual t = ((turretActual) e); //cast it
                            t.setEnemies(enemies); //give it the updated enemies

                            try {
                                for (Entity b : t.getShotsFired())
                                {
                                    bullets.add(b); //add all of the bullets
                                }
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                            }
                        }
                    }
                }

                tf.setTurrets(((ArrayList<Entity>) turrets.clone())); //give the turretFrame the turrets

            }
        };

        runThread = new Thread(r); //start the runThread
        runThread.start();
    }

    public ArrayList<Entity> setEnemiesAndGetTurretsAndBullets (ArrayList<Entity> enemies) { //get all of the turrets and bullets and set the enemies
        this.enemies = enemies; //set the enemies

        ArrayList<Entity> TABS = (ArrayList<Entity>) turrets.clone(); //set a new list to the turrets
        TABS.addAll((Collection<Entity>) bullets.clone()); // add the bullets

        return TABS; //return the list
    }

    protected void buyTurret (Coordinate where, String type) { //buy a turret
        if(!turretSquaresFree.contains(where)) //if the freeSquares don't contain it - return
            return;

        turretActual temp = new turretActual(where, dictionary.getTurret(type), pm); //make a turret

        if(!pm.buy(temp.getTurret().getCost())) //if we can't buy it - return, if we can - buy it and continue
        {
            showNoMoney(tf);
            temp.noLongerWorking();
            return;
        }

        turretSquaresUsed.add(where); //else update the squares
        turretSquaresFree.remove(where);

        System.out.println("TurretManager: " + type + " has been bought.");

        turrets.add(temp); //and add it to the list to be updated and rendered

    }

    protected void sellTurret (turretActual ta) { //sell a turret
        Coordinate where = ta.getXYInArr(); //get where the turret is
        if(!turretSquaresUsed.contains(where)) //check it is in the list, if not return
            return;

        turretSquaresUsed.remove(where); //update the squares
        turretSquaresFree.add(where);
        turrets.remove(ta); //remove from update and render list
        ta.noLongerWorking();

        int moneyBack = ta.getTurret().getSellValue(); //get sale value
        pm.donateM(moneyBack); //give money to playerManager
    }

    protected void upgradeTurret (turretActual ta) {
        Coordinate where = ta.getXYInArr();
        if(!turretSquaresUsed.contains(where))
            return;

        int cost = (ta.getUpgradeFactor() + 1) * ta.getTurret().getCost();
        if(!pm.buy(cost))
        {
            showNoMoney(tf);
            return;
        }

        ta.doUpgrade();
    }

    private static void showNoMoney (JPanel tf) {
        JOptionPane.showMessageDialog(tf,
                "You do not have enough remaining money to do that",
                "No money.",
                JOptionPane.ERROR_MESSAGE);
    }

}