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

    protected boolean mIsOpen;

    public InteractionObstacle(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.OBSTACLE;
    }



    @Override
    public void restoreFromPersistence(GameSession aGameSession) {
       Boolean isDestroyed = (Boolean) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_OPEN);
        if (isDestroyed != null && isDestroyed.booleanValue()) {
            mIsOpen = true;
            remove(CollisionComponent.class);
            String state = (String) aGameSession.getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_OPEN_STATE);
            mCurrentState = getState(state==null ? mDef.defaultState : state);
        } else {
            mIsOpen = false;
        }

    }

    public GameSession saveInPersistence(GameSession aGameSession) {
        aGameSession.putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_OPEN, mIsOpen);
        aGameSession.putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_OPEN_STATE, mCurrentState.name);
        return aGameSession;
    }

    @Override
    protected void doActionOnEvent(InteractionEventAction aAction) {
        super.doActionOnEvent(aAction);
        if (aAction != null && InteractionEventAction.ActionType.OPEN.name().equals(aAction.id)) {
            remove(CollisionComponent.class);
            mIsOpen = true;
        }
    }


}
