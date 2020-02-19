package main;

import javax.swing.*;

import static main.main.lvl1;

public class ApexTurrets {

    public static void main(String[] args) { //main method
        if(JOptionPane.showConfirmDialog(null, "Would you like to play a game of Apex Turrets?", "Apex Turrets?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
            lvl1();
        else
            System.exit(0);
    }

}
