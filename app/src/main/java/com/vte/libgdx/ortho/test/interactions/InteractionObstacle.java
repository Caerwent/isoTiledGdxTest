package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.GameSession;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionObstacle extends Interaction {
    private static final String KEY_IS_OPEN = "is_open";
    private static final String KEY_OPEN_STATE = "open_state";
    private static final String KEY_IS_DESTROYED = "is_destroyed";

    protected boolean mIsDestroyed = false;
    protected boolean mIsOpen;

    public InteractionObstacle(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.OBSTACLE;
    }


    @Override
    public void restoreFromPersistence(GameSession aGameSession) {
        Boolean isOpen = (Boolean) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_OPEN);
        if (isOpen != null && isOpen.booleanValue()) {
            mIsOpen = true;
            remove(CollisionComponent.class);
            String state = (String) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_OPEN_STATE);
            mCurrentState = getState(state == null ? mDef.defaultState : state);
        } else {
            mIsOpen = false;
        }
        Boolean isDestroyed = (Boolean) GameSession.getInstance().getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED);
        if (isDestroyed != null && isDestroyed.booleanValue()) {
            mIsDestroyed = true;
            mMap.getInteractions().removeValue(this, true);
            destroy();
        } else {
            mIsDestroyed = false;
        }

    }

    public GameSession saveInPersistence(GameSession aGameSession) {
        GameSession.getInstance().putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED, mIsDestroyed);
        aGameSession.putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_OPEN, mIsOpen);
        aGameSession.putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_OPEN_STATE, mCurrentState.name);
        return aGameSession;
    }

    @Override
    protected boolean doActionOnEvent(InteractionEventAction aAction) {
        boolean res = super.doActionOnEvent(aAction);
        if (!res && aAction != null && InteractionEventAction.ActionType.OPEN.name().equals(aAction.id)) {
            remove(CollisionComponent.class);
            mIsOpen = true;
            return true;
        } else if (!res && aAction != null && InteractionEventAction.ActionType.REMOVED.name().equals(aAction.id)) {
            mIsDestroyed=true;
            mMap.getInteractions().removeValue(this, true);
            destroy();
            return true;
        }
        return res;
    }


}
