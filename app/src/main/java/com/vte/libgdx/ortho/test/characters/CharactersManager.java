package com.vte.libgdx.ortho.test.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

/**
 * Created by vincent on 05/01/2017.
 */

public class CharactersManager {
    private Json _json = new Json();
    private final String CHARACTERS_LIST = "data/characters/npj.json";
    private static CharactersManager _instance = null;
    private CharactersFactory mFactory;

    public static CharactersManager getInstance() {
        if (_instance == null) {
            _instance = new CharactersManager();
        }

        return _instance;
    }

    private CharactersManager() {
        _json.setIgnoreUnknownFields(true);
        _json.addClassTag("charactersFactory", CharactersFactory.class);
        _json.setElementType(CharactersFactory.class, "characters", CharacterDef.class);
        mFactory = _json.fromJson(CharactersFactory.class, Gdx.files.internal(CHARACTERS_LIST));
    }

    public CharactersFactory getCharactersFactory() {
        return mFactory;
    }


}
