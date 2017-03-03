package com.vte.libgdx.ortho.test.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent on 25/02/2017.
 */

public class EffectFactory {

    private final String EFFECTS_FILE = "data/effects/effects.json";
    private Json mJson;
    private HashMap<Effect.Type, Effect> mEffectList;

    private static EffectFactory sInstance;

    public static EffectFactory getInstance() {
        if (sInstance == null) {
            sInstance = new EffectFactory();
        }

        return sInstance;
    }

    public EffectFactory() {
        mJson = new Json();
        mJson.setIgnoreUnknownFields(true);
        mEffectList = new HashMap<Effect.Type, Effect>();
        ArrayList<JsonValue> list = mJson.fromJson(ArrayList.class,
                Gdx.files.internal(EFFECTS_FILE));
        for (JsonValue v : list) {
            Effect effect = mJson.readValue(Effect.class, v);
            effect.init();
            mEffectList.put(effect.id, effect);
        }
    }


    public Effect getEffect(Effect.Type aId) {
        return mEffectList.get(aId);
    }
}
