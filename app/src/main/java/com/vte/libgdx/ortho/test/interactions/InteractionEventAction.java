package com.vte.libgdx.ortho.test.interactions;

import java.util.ArrayList;

/**
 * Created by vincent on 13/02/2017.
 */

public class InteractionEventAction {
    public static enum ActionType {
        SET_STATE,
        WAKEUP,
        OPEN,
        CLOSE,
        REMOVED;
    }
    public String id;
    public String value;
    public ArrayList<InteractionEvent> inputEvents;

}
