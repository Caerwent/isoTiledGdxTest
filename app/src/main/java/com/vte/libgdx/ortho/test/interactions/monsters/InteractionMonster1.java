package com.vte.libgdx.ortho.test.interactions.monsters;

import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.interactions.InteractionDef;
import com.vte.libgdx.ortho.test.interactions.InteractionEventAction;
import com.vte.libgdx.ortho.test.interactions.InteractionFollowPath;
import com.vte.libgdx.ortho.test.interactions.InteractionMapping;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.GameSession;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionMonster1 extends InteractionFollowPath {
    private static final String KEY_IS_DESTROYED = "is_destroyed";

    protected boolean mIsDestroyed = false;

    public InteractionMonster1(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.MONSTER;


    }
    public void restoreFromSessionPersistence() {
        Boolean isDestroyed = (Boolean) GameSession.getInstance().getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED);
        if (isDestroyed != null && isDestroyed.booleanValue()) {
            mIsDestroyed = true;
            mMap.getInteractions().removeValue(this, true);
            destroy();
        } else {
            mIsDestroyed = false;
        }

    }

    public void saveInSessionPersistence() {
        GameSession.getInstance().putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED, mIsDestroyed);
    }


    @Override
    protected boolean doActionOnEvent(InteractionEventAction aAction) {
        boolean res = super.doActionOnEvent(aAction);
        if (!res && aAction != null && InteractionEventAction.ActionType.REMOVED.name().equals(aAction.id)) {
            mIsDestroyed=true;
            mMap.getInteractions().removeValue(this, true);
            destroy();
            return true;
        }
        return res;
    }


}
