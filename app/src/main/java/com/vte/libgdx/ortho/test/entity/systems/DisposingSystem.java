package com.vte.libgdx.ortho.test.entity.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.components.StateComponent;


public class DisposingSystem extends IteratingSystem {

    Array<Entity> toRemove;
    int eraseID;


    public DisposingSystem() {
        super(Family.all(StateComponent.class).get());

        toRemove = new Array<Entity>();
        eraseID = StateComponent.getID("erase");
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Engine engine = EntityEngine.getInstance();

        for (Entity entity : toRemove) {
            engine.removeEntity(entity);
        }

        toRemove.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        StateComponent state = entity.getComponent(StateComponent.class);

        if (state.id == eraseID) {
            toRemove.add(entity);
        }
    }

}
