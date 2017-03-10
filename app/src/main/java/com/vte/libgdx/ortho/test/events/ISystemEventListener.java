package com.vte.libgdx.ortho.test.events;

import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.map.MapTownPortalInfo;

/**
 * Created by vincent on 27/01/2017.
 */

public interface ISystemEventListener {

    public void onNewMapRequested(String aMapId, MapTownPortalInfo aTownPortalInfo);

    public void onMapLoaded(GameMap aMap);

    public void onNewSelectedEffect(Effect.Type aEffectType);
}
