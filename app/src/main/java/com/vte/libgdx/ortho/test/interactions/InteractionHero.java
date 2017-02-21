package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.vte.libgdx.ortho.test.box2d.Path;
import com.vte.libgdx.ortho.test.entity.components.BobComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.map.GameMap;
import com.vte.libgdx.ortho.test.map.ItemInteraction;

/**
 * Created by vincent on 14/02/2017.
 */

public class InteractionHero extends Interaction{

    protected float stateTime; // elapsed time

    public InteractionHero(InteractionDef aDef, float x, float y, InteractionMapping aMapping, MapProperties aProperties, GameMap aMap) {
        super(aDef, x, y, aMapping, aProperties, aMap);
        mType = Type.HERO;
        CollisionComponent collisionComponent = this.getComponent(CollisionComponent.class);
        collisionComponent.mType=CollisionComponent.CHARACTER;
        this.add(new BobComponent(this));

    }
    Path mPath;

    public void setPath(Path p) {
        mPath = p;
        if(mPath==null)
        {
            setVelocity(0,0);
        }
        else
        {
            mPath.Reset();
            VisualComponent visual = this.getComponent(VisualComponent.class);
            setPosition(mPath.GetCurrentPoint().x, mPath.GetCurrentPoint().y);
            setVelocity(mPath.GetVelocity());
        }

        stateTime = 0;
    }


    public void update(float dt) {
        super.update(dt);
        if (mPath != null) {
            if (mPath.hasNextPoint()) {
                TransformComponent transform = this.getComponent(TransformComponent.class);

                Vector2 pos2D = new Vector2(transform.position.x, transform.position.y);
                mPath.UpdatePath(pos2D, dt);
                setVelocity(mPath.GetVelocity());
                stateTime += dt;

            } else {
                mPath.destroy();
                mPath = null;
                setVelocity(0, 0);
                stateTime = 0;
            }

        } else {
            setVelocity(0, 0);
            stateTime = 0;
        }

    }

    @Override
    public boolean hasCollisionInteraction(CollisionComponent aEntity) {
        if((aEntity.mType & CollisionComponent.ITEM) !=0)
        {
            EventDispatcher.getInstance().onItemFound((((ItemInteraction) aEntity.mData).getItem()));
            return false;
        }
        return aEntity.mShape.getBounds().getY() > getShape().getBounds().getY();
    }

    @Override
    public void onStartCollisionInteraction(CollisionComponent aEntity) {
        if (((aEntity.mType & CollisionComponent.OBSTACLE) !=0) && mPath != null) {
            mPath.destroy();
            mPath = null;
            setVelocity(0, 0);

        }
    }


}
