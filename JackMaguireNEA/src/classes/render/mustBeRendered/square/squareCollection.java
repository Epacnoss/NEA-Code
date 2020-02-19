package classes.render.mustBeRendered.square;

import classes.util.CfgReader.CfgReader;
import classes.util.coordinate.Coordinate;
import main.main;

import java.util.ArrayList;

public class squareCollection {

    private Square[][] squares;
    private ArrayList<Coordinate> enemyPath;
    private ArrayList<Coordinate> availableTurretSquares;
    private Coordinate start, end;
    private CfgReader reader;

    private squareParser sqp;

    public squareCollection(squareParser sqp_) {
        this.sqp = sqp_;

        squares = sqp.getSquares();
        reader = sqp.getR();
        enemyPath = getCoordinates(reader.get("importantLocations", "path").toString());
        availableTurretSquares = getCoordinates(reader.get("importantLocations", "turrets").toString());
        start = enemyPath.get(0);
        end = enemyPath.get(enemyPath.size() - 1);

    }


    public Square[][] getSquares() {
        return squares;
    }

    public ArrayList<Coordinate> getEnemyPath() {
        return enemyPath;
    }

    public ArrayList<Coordinate> getAvailableTurretSquares() {
        return availableTurretSquares;
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }

    public CfgReader getReader() {
        return reader;
    }

    private static ArrayList<Coordinate> getCoordinates (String toBeParsed) {
        ArrayList<Coordinate> path = new ArrayList<>();

        char[] chars = toBeParsed.toCharArray();

        for (int i = 0; i < chars.length; i+=4) { //The Coords are 2 digits long each, with a full one being 4, so we go in 4-length bits
            String xStr = chars[i] + "" + chars[i + 1]; //string in the middle to separate characters as otherwise it gives an int
            String yStr = chars[i + 2] + "" + chars[i + 3];

            if(!main.INT_REGEX.matcher(xStr).matches() || !main.INT_REGEX.matcher(yStr).matches()) //check regex
                continue;


            int x = Integer.parseInt(xStr);
            int y = Integer.parseInt(yStr);



            Coordinate it = new Coordinate(x, y);
            path.add(it);

        }

        return path;
    }

    public squareCollection clone () {
        return new squareCollection(sqp.clone());
    }
}
