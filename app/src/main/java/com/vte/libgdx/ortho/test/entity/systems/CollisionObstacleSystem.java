package com.vte.libgdx.ortho.test.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.vte.libgdx.ortho.test.box2d.ShapeUtils;
import com.vte.libgdx.ortho.test.entity.components.CollisionObstacleComponent;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionObstacleSystem extends IteratingSystem {

    public CollisionObstacleSystem() {
        super(Family.all(CollisionObstacleComponent.class).get());
    }


    @Override
    public void processEntity(Entity entity, float deltaTime) {
        ImmutableArray<Entity> entities = getEntities();

        CollisionObstacleComponent collisionObstacleComponent = entity.getComponent(CollisionObstacleComponent.class);
        if(collisionObstacleComponent.mHandler==null)
            return;
        CollisionObstacleComponent otherCollisionObstacleComponent;

        for (Entity otherEntity : entities) {
            if (otherEntity == entity)
                continue;

            otherCollisionObstacleComponent = otherEntity.getComponent(CollisionObstacleComponent.class);
            if(otherCollisionObstacleComponent ==null)
                continue;
            if (ShapeUtils.overlaps(collisionObstacleComponent.mShape, otherCollisionObstacleComponent.mShape)) {
                if (!collisionObstacleComponent.mHandler.getCollisionObstacle().contains(otherCollisionObstacleComponent, false)) {
                    collisionObstacleComponent.mHandler.onCollisionObstacleStart(otherCollisionObstacleComponent);
                }
            } else {
                if (collisionObstacleComponent.mHandler.getCollisionObstacle().contains(otherCollisionObstacleComponent, false)) {
                    collisionObstacleComponent.mHandler.onCollisionObstacleStop(otherCollisionObstacleComponent);
                }
            }

        }

    }
}
