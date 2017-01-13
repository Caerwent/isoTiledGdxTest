package com.vte.libgdx.ortho.test.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;

import java.util.ArrayList;

/**
 * Created by vincent on 05/01/2017.
 */

public class DialogsManager {

    private Json _json = new Json();
    private final String DIALOGS_LIST = "data/dialogs/dialogs.json";
    private static DialogsManager _instance = null;

    private OrderedMap<String, GameDialog> mDialogs = new OrderedMap<String, GameDialog>();

    public static DialogsManager getInstance() {
        if (_instance == null) {
            _instance = new DialogsManager();
        }

        return _instance;
    }

    private DialogsManager() {
        _json.setIgnoreUnknownFields(true);
        ArrayList<JsonValue> list = _json.fromJson(ArrayList.class,
                Gdx.files.internal(DIALOGS_LIST));
        for (JsonValue v : list) {
            GameDialog dialog = _json.readValue(GameDialog.class, v);
            mDialogs.put(dialog.getId(), dialog);
        }
    }

    public GameDialog getDialog(String id) {
        if (id == null)
            return null;
        return mDialogs.get(id);
    }

}
