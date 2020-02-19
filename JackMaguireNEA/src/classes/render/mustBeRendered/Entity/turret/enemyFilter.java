package classes.render.mustBeRendered.Entity.turret;

import classes.render.mustBeRendered.Entity.baseEntity.Entity;
import classes.render.mustBeRendered.Entity.baseEntity.entityType;
import classes.render.mustBeRendered.Entity.enemy.enemyActual;
import classes.util.coordinate.Coordinate;

import java.util.ArrayList;

public class enemyFilter {

    private enemyFilter () { //private constructor to avoid instantiation
    }

    private static ArrayList<enemyActual> filterEnemiesByType (ArrayList<Entity> entities) { //filter by type so we only get enemyActuals
        ArrayList<enemyActual> newOnes = new ArrayList<>(); //new list

        for (Entity e : entities) { //for each of the enemies
            if(e.getType().equals(entityType.enemy)) // if they ARE an enemy
                newOnes.add((enemyActual) e); //add them to the new list
        }

        return newOnes;
    }

    private static ArrayList<enemyActual> filterEnemiesByRange (ArrayList<enemyActual> enemies, int range, Coordinate ici) { //filter by range
        ArrayList<enemyActual> newOnes = new ArrayList<>(); //new list

        for (enemyActual e : enemies) { //for each
            if (e.getXYInArr().distTo(ici) <= range) //if they are in our range
                newOnes.add(e); //add to new list
        }

        return newOnes;
    }

    private static ArrayList<enemyActual> filterEnemiesBySpawned (ArrayList<enemyActual> enemies) { //filter by if they have been spawned
        for(enemyActual e : ((ArrayList<enemyActual>) enemies.clone())) { //for each
            if(!e.haveIBeenSpawnedYet()) //if they haven't been spawned yet
                enemies.remove(e); //remove them
        }

        return enemies; //return original list in case reference wasn't kept
    }

    protected static ArrayList<enemyActual> filterEnemies (ArrayList<Entity> entities, int range, Coordinate xy) { //filter by all mechanisms
        ArrayList<enemyActual> enemyActuals = filterEnemiesByType(entities); // filter by type
        enemyActuals = filterEnemiesByRange(enemyActuals, range, xy); //filter by range
        enemyActuals = filterEnemiesBySpawned(enemyActuals); //filter by spawned

        return enemyActuals; //return them
    }
}
