package com.vte.libgdx.ortho.test.interactions.monsters;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.vte.libgdx.ortho.test.box2d.PathMap;
import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VelocityComponent;
import com.vte.libgdx.ortho.test.interactions.Interaction;
import com.vte.libgdx.ortho.test.interactions.InteractionDef;
import com.vte.libgdx.ortho.test.interactions.InteractionEventAction;
import com.vte.libgdx.ortho.test.interactions.InteractionMapping;
import com.vte.libgdx.ortho.test.map.GameMap;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionMonster1 extends Interaction {
    protected PathMap mPath;

    public InteractionMonster1(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.MONSTER;

        mPath = aMap.getPaths().get(getId());

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
        if (aAction != null && "WAKEUP".equals(aAction.id)) {
            if (mPath != null) {
                setMovable(true);
            }
        }
    }

    @Override
    protected void stopEffectAction() {
        boolean isToBeRemoved = false;
        if (mEffectAction.id== Effect.Type.BURN && mEffectAction.targetDuration < 0) {
            isToBeRemoved = true;
        }
        super.stopEffectAction();
        if (isToBeRemoved) {
            mMap.getInteractions().removeValue(this, true);
            destroy();
        }
    }

}
