package Gameplay.waves;

import java.util.HashMap;

public class Wave { //a wave

    private HashMap<Character, Integer> wave; //the enemy initals and the number

    public Wave(HashMap<Character, Integer> wave) { //constructor
        this.wave = wave;
    }

    public HashMap<Character, Integer> getWave() { //getter
        return wave;
    }
}