package com.vte.libgdx.ortho.test.items;

import com.badlogic.gdx.maps.Map;
import com.vte.libgdx.ortho.test.box2d.MapBodyManager;

/**
 * Created by gwalarn on 27/11/16.
 */

public class ItemsManager {
    static private ItemsManager s_instance;

    public static ItemsManager getInstance() {
        return s_instance;
    }

    public static synchronized ItemsManager createInstance() {
        s_instance = new ItemsManager();
        return s_instance;
    }

    public ItemsManager()
    {

    }
}
