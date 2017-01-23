package com.vte.libgdx.ortho.test.characters;

import com.badlogic.gdx.math.Vector2;
import com.vte.libgdx.ortho.test.box2d.Path;
import com.vte.libgdx.ortho.test.entity.components.BobComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.map.MapInteractionItem;


/**
 * Created by vincent on 01/07/2016.
 */

public class CharacterHero extends Character {

    Path path;

    public void SetPath(Path p) {
        path = p;
        path.Reset();
        VisualComponent visual = this.getComponent(VisualComponent.class);
        setPosition(path.GetCurrentPoint().x, path.GetCurrentPoint().y);
        setVelocity(path.GetVelocity());
        stateTime = 0;
    }


    public void update(float dt) {
        super.update(dt);
        if (path != null) {
            if (path.hasNextPoint()) {
                TransformComponent transform = this.getComponent(TransformComponent.class);

                Vector2 pos2D = new Vector2(transform.position.x, transform.position.y);
                path.UpdatePath(pos2D, dt);
                setVelocity(path.GetVelocity());
                stateTime += dt;

            } else {
                path.destroy();
                path = null;
                setVelocity(0, 0);
                stateTime = 0;
            }

        } else {
            setVelocity(0, 0);
            stateTime = 0;
        }

    }

    public CharacterHero() {
        super("bob", "player", "characters/universal_walk.png", null);
        this.add(new BobComponent(this));


    }


    @Override
    public boolean onCollisionStart(CollisionComponent aEntity) {
        if(aEntity.mType== CollisionComponent.Type.ITEM)
        {
            EventDispatcher.getInstance().onItemFound((((MapInteractionItem) aEntity.mData).getItem()));
            return false;
        }
        boolean ret = super.onCollisionStart(aEntity);

        if (ret && aEntity.mType == CollisionComponent.Type.OBSTACLE && path != null) {
            path.destroy();
            path = null;
            setVelocity(0, 0);

        }
        return ret;
    }


}
