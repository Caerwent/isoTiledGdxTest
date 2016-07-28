package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectMap;

public class StateComponent implements Component {

    public static final int STATE_INVALID = 0;

    public int id;

    private static ObjectMap<String, Integer> stateIDs = new ObjectMap<String, Integer>();
    private static ObjectMap<Integer, String> stateNames = new ObjectMap<Integer, String>();
    private static int nextState = 1;

    public static int getID(String name) {
        Integer stateID = stateIDs.get(name, null);

        if (stateID == null) {
            stateID = nextState;
            stateIDs.put(name, nextState++);
            stateNames.put(stateID, name);
        }

        return stateID;
    }

    public static String getName(int id) {
        return stateNames.get(id, null);
    }

    public StateComponent() {
        id = STATE_INVALID;
    }

    public StateComponent(StateComponent other) {
        id = other.id;
    }

    public void reset() {
        id = STATE_INVALID;
    }
}
