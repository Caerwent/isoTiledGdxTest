package com.vte.libgdx.ortho.test.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.vte.libgdx.ortho.test.box2d.ShapeUtils;
import com.vte.libgdx.ortho.test.entity.components.CollisionEffectComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionInteractionComponent;

/**
 * Created by vincent on 26/07/2016.
 */

public class CollisionInteractionSystem extends IteratingSystem {

    public CollisionInteractionSystem() {
        super(Family.one(CollisionInteractionComponent.class, CollisionEffectComponent.class).get());
    }


    @Override
    public void processEntity(Entity entity, float deltaTime) {
        ImmutableArray<Entity> entities = getEntities();

        CollisionEffectComponent effectComponent = entity.getComponent(CollisionEffectComponent.class);
        ;
        CollisionInteractionComponent collisionInteractionComponent = entity.getComponent(CollisionInteractionComponent.class);

        CollisionInteractionComponent otherCollisionInteractionComponent;


        for (Entity otherEntity : entities) {
            if (otherEntity == entity)
                continue;

            otherCollisionInteractionComponent = otherEntity.getComponent(CollisionInteractionComponent.class);

            if (otherCollisionInteractionComponent == null || otherCollisionInteractionComponent.mHandler == null)
                continue;

            if (effectComponent != null) {
                if (ShapeUtils.overlaps(effectComponent.mZoneEffect, otherCollisionInteractionComponent.mShape)) {
                    if (!otherCollisionInteractionComponent.mHandler.getCollisionEffect().contains(effectComponent, false)) {
                        otherCollisionInteractionComponent.mHandler.onCollisionEffectStart(effectComponent);
                    }
                } else {
                    if (otherCollisionInteractionComponent.mHandler.getCollisionEffect().contains(effectComponent, false)) {
                        otherCollisionInteractionComponent.mHandler.onCollisionEffectStop(effectComponent);
                    }
                }
            }
            if (collisionInteractionComponent != null) {
                if (ShapeUtils.overlaps(collisionInteractionComponent.mShape, otherCollisionInteractionComponent.mShape)) {
                    if (!otherCollisionInteractionComponent.mHandler.getCollisionInteraction().contains(collisionInteractionComponent, false)) {
                        otherCollisionInteractionComponent.mHandler.onCollisionInteractionStart(collisionInteractionComponent);
                    }
                } else {
                    if (otherCollisionInteractionComponent.mHandler.getCollisionInteraction().contains(collisionInteractionComponent, false)) {
                        otherCollisionInteractionComponent.mHandler.onCollisionInteractionStop(collisionInteractionComponent);
                    }
                }
            }

        }

    }
}
