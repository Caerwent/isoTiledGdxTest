package com.vte.libgdx.ortho.test.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.vte.libgdx.ortho.test.entity.components.SpriteComponent;
import com.vte.libgdx.ortho.test.entity.components.StateComponent;
import com.vte.libgdx.ortho.test.entity.components.TextureComponent;


public class SpriteAnimationSystem extends IteratingSystem {

    public SpriteAnimationSystem() {
        super(Family.all(SpriteComponent.class,
                StateComponent.class,
                TextureComponent.class).get());
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        SpriteComponent animation = entity.getComponent(SpriteComponent.class);
        StateComponent state = entity.getComponent(StateComponent.class);
        TextureComponent texture = entity.getComponent(TextureComponent.class);

      /*  Animation newAnimation = animation.data.getAnimation(StateComponent.getName(state.mId));

        if (animation.currentAnimation != newAnimation) {
            animation.time = 0.0f;
            animation.currentAnimation = newAnimation;
        } else {
            animation.time += deltaTime;
        }

        texture.region = animation.currentAnimation.getKeyFrame(animation.time);*/
    }
}
