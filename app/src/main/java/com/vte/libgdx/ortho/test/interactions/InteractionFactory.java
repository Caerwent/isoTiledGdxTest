package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
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

    public InteractionHero createInteractionHero()
    {
        InteractionMapping mapping = new InteractionMapping();
        mapping.template = "hero.json";
        mapping.id="hero";
        InteractionDef def = mJson.fromJson(InteractionDef.class, Gdx.files.internal("data/interactions/" + mapping.template));
        InteractionHero hero = new InteractionHero(def,0,0,mapping,null,null);

        return hero;

    }
    public IInteraction createInteractionInstance(float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        if (aMapping == null) {
            return null;
        }

        InteractionDef def = mJson.fromJson(InteractionDef.class, Gdx.files.internal("data/interactions/" + aMapping.template));

        if (def.type.compareTo("MONSTER1") == 0) {
            InteractionMonster1 interaction = new InteractionMonster1(def, x, y, aMapping, aProperties, aMap);
            return interaction;
        }
        else if (def.type.compareTo("ACTIVATOR1") == 0) {
            InteractionActivator interaction = new InteractionActivator(def, x, y, aMapping, aProperties, aMap);
            return interaction;
        }
        else if (def.type.compareTo("CHESS") == 0) {
            InteractionChess interaction = new InteractionChess(def, x, y, aMapping, aProperties, aMap);
            return interaction;
        }
        else if (def.type.compareTo("PORTAL") == 0) {
            InteractionPortal interaction = new InteractionPortal(def, x, y, aMapping, aProperties, aMap);
            return interaction;
        }
        else if (def.type.compareTo("NPC") == 0) {
            InteractionNPC interaction = new InteractionNPC(def, x, y, aMapping, aProperties, aMap);
            return interaction;
        }
        return null;
    }
}
