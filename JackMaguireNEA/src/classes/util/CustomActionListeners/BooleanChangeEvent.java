package classes.util.CustomActionListeners;

import java.util.EventObject;

public class BooleanChangeEvent extends EventObject { //change event
    private final BooleanChangeDispatcher dispatcher; //changeDispatcher - the class which implemented it

    public BooleanChangeEvent (BooleanChangeDispatcher dispatcher) { //the event constructor
        super(dispatcher); //super
        this.dispatcher = dispatcher; //set the dispatcher

    }

}
