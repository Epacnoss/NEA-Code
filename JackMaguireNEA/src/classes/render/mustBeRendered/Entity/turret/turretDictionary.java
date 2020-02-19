package classes.render.mustBeRendered.Entity.turret;

import classes.util.CfgReader.CfgReader;
import main.main;

import java.util.ArrayList;
import java.util.HashMap;

public class turretDictionary { //similar to enemyDictionary

    private HashMap<String, turretTemplate> turrets; //turrets
    private ArrayList<String> names; //names of turrets

    public turretDictionary(String[] fns) {

        turrets = new HashMap<>();
        names = new ArrayList<>(); //init lists

        for(String fnTurret : fns) { //for each fn
            CfgReader reader = new CfgReader(main.TURRETS_LOC + fnTurret); //make a cfgReader

            HashMap<String, Object> playerSees = reader.getModule("playerSees"); //split to hashmaps
            HashMap<String, Object> compSees = reader.getModule("compSees");

            String name = playerSees.get("name").toString(); //name
            String info = playerSees.get("info").toString(); //turret info
            String dmg = playerSees.get("dmg").toString(); //damage
            String fireRate = playerSees.get("fireRate").toString(); //fireRate
            String range = playerSees.get("range").toString(); //range of bullets

            int dmgInt = Integer.parseInt(compSees.get("dmg").toString()); //damage - int form
            int rangeInt = Integer.parseInt(compSees.get("range").toString()); //range - int form
            String fnImg = compSees.get("file").toString(); //image file name
            String fnBulletImage = compSees.get("bulletFile").toString(); //bullet image file name
            int cost = Integer.parseInt(compSees.get("cost").toString()); //cost
            int sellValue = Integer.parseInt(compSees.get("sellValue").toString()); //sale value - if the player sells it - they will get this back
            double fireRateDbl = Double.parseDouble(compSees.get("fireRate").toString()); //fireRate - double form
            int bulletSpd = Integer.parseInt(compSees.get("bulletSpd").toString()); //bullet Speed

            turretTemplate t = new turretTemplate(name, info, dmg, fireRate, range, dmgInt, rangeInt, fnImg, fnBulletImage, cost, sellValue, fireRateDbl, bulletSpd); //new turretTemplate

            turrets.put(name, t); //put in the hashMap
            names.add(name); //put in the name

        }

    }

    public HashMap<String, turretTemplate> getTurrets() { //get all turrets
        return turrets;
    }
    public turretTemplate getTurret (String key) { //get a turret based on key
        return turrets.get(key);
    }
}
