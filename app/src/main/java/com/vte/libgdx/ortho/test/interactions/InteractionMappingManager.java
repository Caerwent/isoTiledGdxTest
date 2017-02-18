package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent on 13/02/2017.
 */

public class InteractionMappingManager {

    private Json mJson;
    private HashMap<String, InteractionMapping> mMappingList;
    public InteractionMappingManager ()
    {
        mJson = new Json();
        mJson.setIgnoreUnknownFields(true);
        mMappingList = new HashMap<String, InteractionMapping>();
    }

    public void loadMappingFile(String aFilename)
    {

        ArrayList<JsonValue> list = mJson.fromJson(ArrayList.class,
                Gdx.files.internal(aFilename));
        for (JsonValue v : list) {
            InteractionMapping mapping = mJson.readValue(InteractionMapping.class, v);
            mMappingList.put(mapping.id, mapping);
        }
    }

    public InteractionMapping getInterationMapping(String aId)
    {
        return mMappingList.get(aId);
    }
}
