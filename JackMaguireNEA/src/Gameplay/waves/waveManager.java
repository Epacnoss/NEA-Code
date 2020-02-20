package Gameplay.waves;

import classes.util.CfgReader.CfgReader;
import Gameplay.player.PlayerManager;
import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustBeRendered.Entity.enemy.enemyActual;
import classes.render.mustBeRendered.Entity.enemy.enemyDictionary;
import classes.render.mustBeRendered.Entity.enemy.enemyTemplate;
import classes.render.mustBeRendered.square.squareCollection;
import main.main;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class waveManager { //class for spawning in enemies

    public static final long START_GAP = 30; //S - gap before first enemy spawns to let player build turret

    private ArrayList<ArrayList<Character>> wavesInBetterForm; //Arraylist (all waves) of arraylists (individual waves)

    private ArrayList<Entity> enemyActuals; //all of the enemies

    private int enemiesSpawned; //the enemies spawned
    private int totEnemies; //the total enemies that will be spawned

    private long enemyDist; //the time between enemies spawning
    private long waveDist; //the time between waves happening

    private enemyDictionary enemyDictionary; //the dictionary of enemies
    private squareCollection sqc; //the squares

    private Thread runThread; //the run and check threads
    private Thread checkThread;

    private boolean startWaitDone;

    public waveManager(String fnOfWave, squareCollection sqc_, PlayerManager pm, JFrame win) { //constructor
        startWaitDone = false;
        Thread waiterThread = new Thread(() -> {
            for (long l = START_GAP; l > 0; l--) {
                try {
                    TimeUnit.SECONDS.sleep(1); //sleep the starting gap - printing the seconds remaining every second
                    System.out.println(l + "s remaining, in start wait");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            startWaitDone = true;
        });
        waiterThread.start();

        CfgReader reader = new CfgReader(main.WAVES_LOC + fnOfWave); //create cfgReader for the wave config file
        ArrayList<Wave> waves = WaveParser.getAllWaves(reader); //use the waveParser to get the waves
        enemyActuals = new ArrayList<>(); //init enemies

        enemiesSpawned = 0; //init enemiesSpawned and totEnemies
        totEnemies = 0;

        String enemyDistStr = reader.get("enemyGaps", "enemyGap").toString();
        String waveDistStr = reader.get("enemyGaps", "waveGap").toString(); //get gaps between enemies and waves in string

        if(main.INT_REGEX.matcher(enemyDistStr).matches())
            enemyDist = Integer.parseInt(enemyDistStr); //if we can - parse int
        else
            enemyDist = 1000; //else just set to 1 second

        if(main.INT_REGEX.matcher(waveDistStr).matches()) //same - but with the wave difference
            waveDist = Integer.parseInt(waveDistStr);
        else
            waveDist = 3000;

        enemyDictionary = new enemyDictionary(main.ENEMY_FNS, main.ENEMY_IMG_FNS); //create enemyDictionary
        sqc = sqc_; //set squares

        wavesInBetterForm = new ArrayList<>(); //init waves in easier form
        System.out.println("Lets get the party started in here!");

        for (int i = 0; i < waves.size(); i++) { //for each of the waves from the waveParser
            HashMap<Character, Integer> oldWave = waves.get(i).getWave(); //get the hashMap from the wave
            Object[] keySet = oldWave.keySet().toArray(); //now we have an obj array, of the enemies

            ArrayList<Character> thisWave = new ArrayList<>(); //create this wave

            for (int j = 0; j < keySet.length; j++) { //for each of the keys
                char c = keySet[j].toString().charAt(0); //Obj to char

                int no = oldWave.get(c); // the no of enemies of that type

                for (int k = 0; k < no; k++) { //for each in that
                    thisWave.add(c); //add another enemy to this wave
                    totEnemies++; //increment total enemes
                }

            }

            wavesInBetterForm.add(thisWave); //add this wave
        }

        Runnable checker = () -> { //runnable for checkThread - checks whether the enemies are all dead and the waves are all dead
            while(!pm.haveIWon() && win.isVisible()) {
                check(enemyActuals, pm, win); //check method - checks for enemy deaths / wins
                if(hasThePCWon()) //if the player character has won
                    pm.hasWon(); // tell them that they have
            }
        };

        checkThread = new Thread(checker); //create a thread to check for the player
        Runnable runnable = () -> {
            System.out.println("running");

            while(!startWaitDone) //whilst we haven't had the start wait yet
            {
                try {
                    TimeUnit.MILLISECONDS.sleep(200); //just a standard gap
                } catch (InterruptedException e) {
                    System.out.println("Enemy start wait sleeper interrupted");
                }
            }

            checkThread.start(); // start the checker thread
            for (ArrayList<Character> thisWave : wavesInBetterForm) { //for each wave
                try {
                    TimeUnit.MILLISECONDS.sleep(waveDist); //sleep for the wave gap
                } catch (InterruptedException e) {
                    System.out.println("Wave wait interrupted");
                }

                for(Character c : thisWave) { //for each enemy of this wave
                    try {
                        TimeUnit.MILLISECONDS.sleep(enemyDist); //sleep for each enemy the gap
                    } catch (InterruptedException e) {
                        System.out.println("Enemy wait interrupted.");
                    }

                    enemyTemplate eT = enemyDictionary.getEnemy(c); //get the template from the dictionary
                    enemyActual eA = new enemyActual(eT, sqc.clone(), pm); //create the enemy


                    System.out.println("Enemy Started");

                    enemyActuals.add(eA); //add to render list
                    enemiesSpawned++; //increment number spawned


                    if(pm.isDead()) //check for player death - we would use .isDone, but here, it cannot be done, as either the enemy cannot yet be dead (excluding extreme circumstances), so the player cannot have yet won
                        return;

                }

            }

        };

        runThread = new Thread(runnable); //create thread
        runThread.start(); //and start it
    }

    public ArrayList<Entity> getEntites () { //get the render list
        return enemyActuals;
    }

    private static void check (ArrayList<Entity> enemyActuals, PlayerManager pm, JFrame win) {
        if(!win.isVisible()) {
            for(Entity e : enemyActuals) {
                enemyActual casted = ((enemyActual) e);
                casted.stop();
            }
            enemyActuals.clear();
        }

        for(Entity e : ((ArrayList<Entity>) enemyActuals.clone())) //Cloned to avoid ConcurrentModificationExceptions
        {
            enemyActual eA = ((enemyActual) e); //cast it
            if(eA.isDone()) { //if it is done:
                enemyActuals.remove(e); //take it off the rendering list

                boolean isDead = eA.isDead(); //did it die
                if(!isDead) { //if it didn't - if it reached the end
                    pm.takeHearts(eA.getTemplate().getHeartsCost()); //remove hearts from the player
                    System.out.println("Tybalteth's victory was reported, and we no got him :-(");
                }
                else //if it did die - from the bullets
                {
                    int moneyGained = eA.getTemplate().getMoneyBack(); // get the money back from enemy death
                    pm.donateM(moneyGained); //give money to the player
                    System.out.println("Ladies and Gentlemen. We got him!");
                }
            }
        }
    }

    public int getEnemiesSpawned() { //get the number of enemies spawned
        return enemiesSpawned;
    }

    private boolean hasThePCWon () { //checker method for if the player has won (PC referring to Player Character)
        if(enemiesSpawned == totEnemies) { //if we have spawned all of the enemies
            for (Entity enemyActual : enemyActuals) { // for each of the enemies remaining
                if(enemyActual.isDone())
                    return false;
            }

            return true; //if all of the enemies have been gone through and all are done or there are none, and we have spawned all of the enemies
        }
        return false; //if we haven't gone through all of the enemies yet - return false
    }
}