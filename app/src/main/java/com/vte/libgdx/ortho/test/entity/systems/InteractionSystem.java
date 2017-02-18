package com.vte.libgdx.ortho.test.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.vte.libgdx.ortho.test.entity.components.InteractionComponent;

/**
 * Created by vincent on 20/07/2016.
 */

public class InteractionSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private ComponentMapper<InteractionComponent> pm = ComponentMapper.getFor(InteractionComponent.class);

    public InteractionSystem() {
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(InteractionComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            InteractionComponent it = pm.get(entity);
            it.interaction.update(deltaTime);

        }
    }
}
