package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.vte.libgdx.ortho.test.interactions.monsters.InteractionMonster1;
import com.vte.libgdx.ortho.test.map.GameMap;

/**
 * Created by vincent on 08/02/2017.
 */

public class InteractionFactory {

    private static InteractionFactory sInstance = new InteractionFactory();

    static public InteractionFactory getInstance() {
        return sInstance;
    }

    private Json mJson;

    public InteractionFactory() {
        mJson = new Json();
        mJson.setIgnoreUnknownFields(true);
    }

    public IInteraction createInteractionInstance(float x, float y, InteractionMapping aMapping, GameMap aMap) {
        if (aMapping == null) {
            return null;
        }

        InteractionDef def = mJson.fromJson(InteractionDef.class, Gdx.files.internal("data/interactions/" + aMapping.template));

        if (def.type.compareTo("MONSTER1") == 0) {
            InteractionMonster1 interaction = new InteractionMonster1(def, x, y, aMapping, aMap);
            return interaction;
        }
        else if (def.type.compareTo("ACTIVATOR1") == 0) {
            InteractionActivator interaction = new InteractionActivator(def, x, y, aMapping);
            return interaction;
        }
        return null;
    }
}
