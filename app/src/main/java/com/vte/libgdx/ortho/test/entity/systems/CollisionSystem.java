package com.vte.libgdx.ortho.test.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Intersector;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionSystem extends IteratingSystem {

    public CollisionSystem() {
        super(Family.all(CollisionComponent.class).get());
    }


    @Override
    public void processEntity(Entity entity, float deltaTime) {
        ImmutableArray<Entity> entities = getEntities();

        CollisionComponent collisionComponent = entity.getComponent(CollisionComponent.class);
        CollisionComponent otherCollisionComponent;

        for (Entity otherEntity : entities) {
            if (otherEntity == entity)
                continue;

            otherCollisionComponent = otherEntity.getComponent(CollisionComponent.class);
            if (Intersector.overlapConvexPolygons(collisionComponent.mBound, otherCollisionComponent.mBound, null)) {
                if (!collisionComponent.mHandler.getCollisions().contains(otherCollisionComponent, false)) {
                    collisionComponent.mHandler.onCollisionStart(otherCollisionComponent);
                }
            } else {
                if (collisionComponent.mHandler.getCollisions().contains(otherCollisionComponent, false)) {
                    collisionComponent.mHandler.onCollisionStop(otherCollisionComponent);
                }
            }

        }

    }
}
