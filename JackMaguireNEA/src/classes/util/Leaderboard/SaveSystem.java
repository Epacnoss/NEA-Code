package classes.util.Leaderboard;

import main.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;

public class SaveSystem {

    private static final String[] fns = {"high1.txt", "high2.txt", "high3.txt"};

    public static String getHighScore (int lvl) {
        String fn = fns[lvl - 1];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fn));
            String score = reader.readLine();
            reader.close();
            return score;
        } catch (Exception e) {
            return "0"; //we haven't had that high score yet.
        }

    }


    public static void setHighScore (int lvl, int score) {
        String fn = fns[lvl - 1];
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fn));
            writer.write((new Timestamp(System.currentTimeMillis()) + ": " + score));
            writer.close();
        }catch (Exception e) {
            System.out.println("File not readable");
            e.printStackTrace();
        }
    }

}
