package com.vte.libgdx.ortho.test.events;

import com.vte.libgdx.ortho.test.map.GameMap;

/**
 * Created by vincent on 27/01/2017.
 */

public interface IGameEventListener {

    public void onNewMapRequested(String aMapId);
    public void onMapLoaded(GameMap aMap);
}
