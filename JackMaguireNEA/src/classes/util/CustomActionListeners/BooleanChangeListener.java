package classes.util.CustomActionListeners;

import java.util.EventListener;

public interface BooleanChangeListener extends EventListener { //the listener
    void stateChanged (BooleanChangeEvent event); //the stateChanged listener event
}