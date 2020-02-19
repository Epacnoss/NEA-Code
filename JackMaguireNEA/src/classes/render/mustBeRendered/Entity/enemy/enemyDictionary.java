package classes.render.mustBeRendered.Entity.enemy;

import classes.util.CfgReader.CfgReader;
import main.main;

import java.util.HashMap;

public class enemyDictionary {

    private HashMap<Character, enemyTemplate> enemies;

    public enemyDictionary(String[] fns, String[] imgFns) { //enemyDictionary - all of the enemies in a hashmap

        enemies = new HashMap<>(); //create the hashmap

        CfgReader[] readers = new CfgReader[fns.length]; //create the cfgReaders array

        for (int i = 0; i < readers.length; i++) {
            readers[i] = new CfgReader(main.ENEMIES_LOC + fns[i]); //create each cfgReader
        }

        for (int i = 0; i < fns.length; i++) { //for each of the files
            CfgReader reader = readers[i]; //get the relevant cfgReader

            HashMap<String, Object> playerSees = reader.getModule("playerSees"); //split it into the two hashMaps
            HashMap<String, Object> compSees = reader.getModule("compSees");

            //get all relevant information
            String name = playerSees.get("name").toString(); //name of enemy
            String info = playerSees.get("info").toString(); //enemy info
            String heartsLost = playerSees.get("heartsLost").toString(); //hearts lost if enemy gets through

            int spd = Integer.parseInt(compSees.get("spd").toString()); //speed
            int hp = Integer.parseInt(compSees.get("hp").toString()); //hitpoints
            int heartsCost = Integer.parseInt(compSees.get("heartsLost").toString()); //as in the PlayerSees
            char intial = compSees.get("initial").toString().charAt(0); //initial for waveSpawnder
            int moneyBack = Integer.parseInt(compSees.get("moneyBack").toString()); //money back if the enemy dies


            String fn = imgFns[i]; //file name for image

            enemyTemplate e = new enemyTemplate(name, spd, info, heartsLost, hp, heartsCost, intial, fn, moneyBack); //new enemyTemplate

            enemies.put(intial, e); //put it in the hashMap
        }
    }

    public enemyTemplate getEnemy(char key) {
        return enemies.get(key);
    } //get an enemy


}
