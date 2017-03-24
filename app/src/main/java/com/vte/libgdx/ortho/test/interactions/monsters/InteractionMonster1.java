package com.vte.libgdx.ortho.test.interactions.monsters;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.vte.libgdx.ortho.test.box2d.PathMap;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VelocityComponent;
import com.vte.libgdx.ortho.test.interactions.Interaction;
import com.vte.libgdx.ortho.test.interactions.InteractionDef;
import com.vte.libgdx.ortho.test.interactions.InteractionEventAction;
import com.vte.libgdx.ortho.test.interactions.InteractionMapping;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.persistence.GameSession;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionMonster1 extends Interaction {
    private static final String KEY_IS_DESTROYED = "is_destroyed";

    protected PathMap mPath;
    protected boolean mIsDestroyed = false;

    public InteractionMonster1(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.MONSTER;

        mPath = aMap.getPaths().get(getId());

    }
    public void restoreFromPersistence() {
        Boolean isDestroyed = (Boolean) GameSession.getInstance().getSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED);
        if (isDestroyed != null && isDestroyed.booleanValue()) {
            mIsDestroyed = true;
            mMap.getInteractions().removeValue(this, true);
            destroy();
        } else {
            mIsDestroyed = false;
        }

    }

    public void saveInPersistence() {
        GameSession.getInstance().putSessionDataForMapAndEntity(mMap.getMapName(), mId, KEY_IS_DESTROYED, mIsDestroyed);
    }
    @Override
    public void update(float dt) {
        super.update(dt);
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);
        if (velocity != null) {
            if (mPath != null && mPath.hasNextPoint()) {
                TransformComponent transform = this.getComponent(TransformComponent.class);
                Vector2 pos2D = new Vector2(transform.position.x, transform.position.y);
                setVelocity(mPath.getVelocityForPosAndTime(pos2D, dt));
            } else {
                setMovable(false);
            }

        }
    }

    @Override
    protected void doActionOnEvent(InteractionEventAction aAction) {
        super.doActionOnEvent(aAction);
        if (aAction != null && InteractionEventAction.ActionType.WAKEUP.name().equals(aAction.id)) {
            if (mPath != null) {
                setMovable(true);
            }
        }
        else if(aAction != null && InteractionEventAction.ActionType.REMOVED.name().equals(aAction.id)) {
            mIsDestroyed=true;
            mMap.getInteractions().removeValue(this, true);
            destroy();
        }
    }


}
