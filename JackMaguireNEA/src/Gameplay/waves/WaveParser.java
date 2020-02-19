package Gameplay.waves;

import classes.util.CfgReader.CfgReader;
import main.main;

import java.util.ArrayList;
import java.util.HashMap;

public class WaveParser {

    private WaveParser () { //private constructor to avoid instantiation
    }

    public static ArrayList<Wave> getAllWaves(CfgReader reader) //This method gives how many eneemies to give between each 1s gap given by the gamemanger
    {
        String noOfWavesStr = reader.get("enemyGaps", "wavesNo").toString();
        int noOfWaves = 0;

        if(main.INT_REGEX.matcher(noOfWavesStr).matches())
            noOfWaves = Integer.parseInt(noOfWavesStr);

        ArrayList<String> wavesRaw = new ArrayList<>();
        for (int i = 1; i <= noOfWaves; i++) {
            String m = "enemyGaps"; //module
            String p = "wave" + i;

            String rawWave = reader.get(m, p).toString();
            wavesRaw.add(rawWave);
        }

        ArrayList<Wave> enemiesBetweenFinal = new ArrayList<>();

        for (String enemies : wavesRaw) { //At this point, we only have a string like EEEESSEEE, with each different character denoting a different enemy
            char[] chars = enemies.toCharArray();

            HashMap<Character, Integer> hashMap = new HashMap<>();

            for (char c : chars) { // for each character
                int current = 1; //Starting value is one if it doesn't contain it, or 1 + the before if it is new.
                if(hashMap.containsKey(c)) {
                    current += hashMap.remove(c);
                }

                hashMap.put(c, current);
            }

            Wave w = new Wave(hashMap);

            enemiesBetweenFinal.add(w);
        }

        return enemiesBetweenFinal;
    }

}
