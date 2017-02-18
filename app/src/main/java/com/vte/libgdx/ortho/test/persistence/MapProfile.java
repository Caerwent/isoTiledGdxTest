package com.vte.libgdx.ortho.test.persistence;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent on 20/01/2017.
 */

public class MapProfile {
    public HashMap<String, ArrayList<Vector2>> items;
    public ArrayList<String> openChessList;

    public MapProfile()
    {
        items = new HashMap<>();
        openChessList = new ArrayList<>();
    }


}
