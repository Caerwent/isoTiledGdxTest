package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.vte.libgdx.ortho.test.box2d.PathMap;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VelocityComponent;
import com.vte.libgdx.ortho.test.map.GameMap;

/**
 * Created by vincent on 17/04/2017.
 */

public class InteractionFollowPath extends Interaction  {
    protected PathMap mPath;

    public InteractionFollowPath(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);

        if(aMapping.properties!=null)
        {
            if(aMapping.properties.containsKey("pathId"))
            {
                mPath = aMap.getPaths().get((String) aMapping.properties.get("pathId"));
            }

        }

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
    protected boolean doActionOnEvent(InteractionEventAction aAction) {
        boolean res = super.doActionOnEvent(aAction);
        if (!res && aAction != null && InteractionEventAction.ActionType.WAKEUP.name().equals(aAction.id)) {
            if (mPath != null) {
                setMovable(true);
            }
            return true;
        }
        return res;
    }
}