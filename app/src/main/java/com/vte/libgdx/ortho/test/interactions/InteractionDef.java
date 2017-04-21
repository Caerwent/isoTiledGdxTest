package com.vte.libgdx.ortho.test.interactions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent on 08/02/2017.
 */

public class InteractionDef {

    public String type;
    public String atlas;
    public IInteraction.Persistence persistence;
    public String defaultState;
    public boolean isClickable;
    public boolean isRendable;
    public boolean isMovable;
    public ArrayList<InteractionState> states;
    public ArrayList<InteractionEventAction> eventsAction;
    public ArrayList<InteractionEvent> outputEvents;
    public HashMap properties;
}
