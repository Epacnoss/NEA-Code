package classes.render.mustBeRendered.square;

import classes.util.CfgReader.CfgReader;
import classes.render.mustBeRendered.square.types.*;
import classes.util.coordinate.Coordinate;
import main.main;

import java.util.HashMap;

public class squareParser { //parser for squareCollection

    private Square[][] squares; //all the squares
    private HashMap<Character, String> fns; //the square file names
    private CfgReader r; //the file reader
    private Coordinate satan; //the enemy start
    private Coordinate home; //the enemy end / player home

    private squareParser(CfgReader r, Square[][] squares, HashMap<Character, String> fns, Coordinate satan, Coordinate home) //private constructor for cloning
    {
        this.r = r.clone();
        this.squares = squares.clone();
        this.fns = ((HashMap<Character, String>) fns.clone());
        this.satan = satan.clone();
        this.home = home.clone();
    }


    public squareParser(CfgReader r) { //normal constructor
        this.r = r; //get CfgReader

        int w = main.NUM_OF_TILES_WIDTH; //width of tiles
        int h = main.NUM_OF_TILES_HEIGHT; //height of tiles
        fns = new HashMap<>(); //init fileNames
        setUpHashMap(); //set up the hashMpa

        char[] tbp = r.get("mapDeets", "map").toString().toCharArray(); //tbp - to be parsed, the squares
        squares = new Square[w][h]; //init array


        int x = 0; //x coordinate
        int y = 0; //y coordinate

        for (char c : tbp) { //for each character in the stuff to be parsed
            String fn = fns.get(c); //get the fileName
            Square newBoi; //temp for the new square

            Coordinate ici = new Coordinate(x, y); //where we are now

            switch (fn) { //switch statement based on file name for creating new square
                case "general_big.png":
                    newBoi = new pathSquare();
                    break;
                case "happy_big.png":
                    newBoi = new homeBase();
                    home = ici;
                    break;
                case "satan_big.png":
                    newBoi = new enemyStartSquare();
                    satan = ici;
                    break;
                case "turret_base_big.png":
                    newBoi = new turretSquare();
                    break;
                case "nothing_big.png":
                    newBoi = new nothingSquare();
                    break;
                default:
                    newBoi = new cornerSquare(fn);
                    break;
            }

            squares[x][y] = newBoi; //set square at x and y to be our new square

            x++; //increment x

            if (x == w) //if x is at end, rollover and increment y
            {
                x = 0;
                y++;
            }
        }
    }


    public squareParser clone () { //cloning method
        return new squareParser(r, squares, fns, satan, home);
    }

    private void setUpHashMap () { //sets up the hashMap
        HashMap<String, Object> mod = r.getModule("refs"); //refs mean references

        Object[] chars = mod.keySet().toArray(); //characters referrring to below.
        Object[] fnsFromR = mod.values().toArray(); //these file names

        for (int i = 0; i < chars.length; i++) { //for each of the character objects
            char c = chars[i].toString().charAt(0); //convert to character
            String fn = fnsFromR[i].toString(); //get file name

            fns.put(c, fn); //add to hashMap
        }
    }

    public Square[][] getSquares() { //get the squares
        return squares;
    }

    public CfgReader getR() { //get the config reader
        return r;
    }
}
