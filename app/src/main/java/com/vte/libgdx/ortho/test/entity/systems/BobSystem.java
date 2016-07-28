package com.vte.libgdx.ortho.test.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.vte.libgdx.ortho.test.entity.components.BobComponent;

/**
 * Created by vincent on 20/07/2016.
 */

public class BobSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private ComponentMapper<BobComponent> pm = ComponentMapper.getFor(BobComponent.class);

    public BobSystem() {
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(BobComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            BobComponent bob = pm.get(entity);
            bob.bob.update(deltaTime);

        }
    }
}
