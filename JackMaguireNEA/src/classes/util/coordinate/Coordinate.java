package classes.util.coordinate;

import main.main;

import java.util.StringJoiner;
import java.util.regex.Pattern;

public class Coordinate implements Comparable<Coordinate> { //coordinate class with x and y

    public static final Coordinate ZERO = new Coordinate(0, 0);
    public static final Coordinate NULL_COORD = new Coordinate(Integer.MAX_VALUE, Integer.MAX_VALUE); //NULL_COORD for if a mistake is made with coordinates


    private int x;
    private int y; //x and y coordinates

    private static Pattern COORDINATE_REGEX = Pattern.compile("Coordinate\\[x=[0-9]+, y=[0-9]+\\]");

    public Coordinate(int x, int y) { //override constructor
        this.setX(x);
        this.setY(y);
    }

    public dir directionTo (Coordinate o) { //get the direction to another coordinate

        boolean equal = equals(o); //if they are equal
        if(equal) //if it is equal - just return North
            return dir.N;

        int xDist = getX() - o.getX(); //get the x and y distances
        int yDist = getY() - o.getY();

        boolean xGreater = Math.abs(xDist) > Math.abs(yDist); //see which one is bigger


        if(!xGreater) { //if the y is greater
            if(yDist < 0) //ie they are below us
                return dir.S; // return South
            else
                return dir.N; //if they are above return North
        }else {
            if(xDist < 0) //else return East or West
                return dir.E;
            else
                return dir.W;
        }

    }

    public double distTo (Coordinate o) { //the distance to another coordinate

        if(equals(o))
            return 0;

        int theirX = o.getX();
        int theirY = o.getY();

        int run = dist(getX(), theirX) ^ 2;
        int rise = dist(getY(), theirY) ^ 2; //using pythagoras theorem

        double dist = Math.sqrt(rise + run);

        return dist;
    }

    public Coordinate clone () { //cloning method
        return new Coordinate(getX(), getY());
    }

    private int dist (int a, int b) { //private distance method
        boolean value1Bigger = (a > b);

        return value1Bigger ? a - b : b - a; //one line if statement to get the largest value
    }

    @Override
    public String toString() { //toString method - generated by intelliJ
        return new StringJoiner(", ", Coordinate.class.getSimpleName() + "[", "]")
                .add("x=" + getX())
                .add("y=" + getY())
                .toString();
    }

    public static Coordinate parseFromTS (String tbp) { //parse from ToString
        //new Coordinate(5, 7).toString() returns "Coordinate[x=5, y=7]"

        if(!COORDINATE_REGEX.matcher(tbp).matches())
            return NULL_COORD;

        int xIndexStart = tbp.indexOf('x') + 2; //the start of the x part
        int xIndexEnd = tbp.indexOf('y') - 2; //the end of the x part

        int yIndexStart = tbp.indexOf('y') + 2; // the start of the y part
        int yIndexEnd = tbp.length() - 1; //the end

        String xStr = tbp.substring(xIndexStart, xIndexEnd); //temporary variables with numbers
        String yStr = tbp.substring(yIndexStart, yIndexEnd);

        if(!main.INT_REGEX.matcher(xStr).matches() || !main.INT_REGEX.matcher(yStr).matches()) //regex check
            return NULL_COORD;

        int x = Integer.parseInt(xStr); //parse integers
        int y = Integer.parseInt(yStr);

        return new Coordinate(x, y); //and return them

    }

    @Override
    public boolean equals(Object obj) { //equals method

        if(obj.getClass() != getClass())
            return false;

        try {
            Coordinate obj2 = ((Coordinate) obj);

            return obj2.x == x && obj2.y == y; // if the x and y is equal return true
        } catch (Exception e) {
            return false; // if there is an exception (possibly a casting error) - return false
        }

    }

    @Override
    public int compareTo(Coordinate o) { //comparing method for sorting - prioritise y and then x
        if(equals(o))
            return 0;

        int xComparison = Integer.compare(getX(), o.getX());
        int yComparison = Integer.compare(getY(), o.getY()); //compare the x and y

        if(yComparison != 0) //if the ys aren't equal
            return yComparison; //return that
        else if (xComparison != 0)
            return xComparison; //else return the xComparison
        else
            return 0; //just in case the initial check failed

    }

    //region getters and setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    //endregion
}
