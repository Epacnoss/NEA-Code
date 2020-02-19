package Gameplay.player;

import classes.util.CustomActionListeners.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;


public class PlayerManager implements BooleanChangeDispatcher { //PlayerManager class to keep track of the player

    private int money; //bank balance
    private int hearts; //hitpoints

    private int startHearts; //starting hearts

    private List<BooleanChangeListener> listeners; //changeListeners

    private boolean hasWon; //have i won yet


    public PlayerManager(int money, int hearts) {
        this.money = money;
        this.hearts = hearts;
        startHearts = hearts; //init variables

        listeners = new ArrayList<>(); //init listeners

        hasWon = false; //we haven't won

        //for god mode
//        win.addKeyListener(this);
    }

    //region Getters
    public int getMoney() {
        return money;
    }

    public int getHearts() {
        return hearts;
    }
    //endregion

    //region methods
    public boolean buy (int amnt) { //buy things - if we can't buy it: return false, else buy it and return true
        if(amnt > money)
            return false;

        money -= amnt;
        dispatchEvent(); //dispatchevent call - sends message to booleanChangeListeners
        return true;
    }
    public void donateM (int amnt) { //give money back - when enemies die
        money += amnt;
        dispatchEvent(); //dispatchevent call
    }

    public void takeHearts (int amnt) { //take hearts - used when enemies win
        hearts -= amnt;
        dispatchEvent(); //dispatchevent call
    }


    public boolean isDead () { //am i dead yet
        return hearts <= 0;
    }
    public void hasWon () { //I have won!! - called by WaveManager when all enemies are dead
        hasWon = true;
        dispatchEvent();
    }
    public boolean haveIWon () { // have I won?
        return hasWon;
    }

    public boolean isDone () { //am I done yet
        return hasWon || isDead();
    }

    public int startHearts () { //get starting hit points
        return startHearts;
    }
    //endregion

    @Override
    public void addBooleanChangeListener(BooleanChangeListener listener) { //add change listener
        listeners.add(listener);
    }

    private void dispatchEvent() { //dispatch event
        final BooleanChangeEvent event = new BooleanChangeEvent(this); //make a changeEvent
        for (BooleanChangeListener l : listeners) {
            dispatchRunnableOnEventQueue(l, event); //dispatchOnEventQueue for each listener
        }
    }

    private void dispatchRunnableOnEventQueue(final BooleanChangeListener listener, final BooleanChangeEvent event) {
        EventQueue.invokeLater(() -> listener.stateChanged(event)); //invoke a change
    }


    //used to be used for testing purposes to guarantee win or loss
    /*@Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_U -> addHearts(1);
            case KeyEvent.VK_D -> takeHearts(1);
            case KeyEvent.VK_R -> donateM(100);
            case KeyEvent.VK_P -> buy(100);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }*/
}
