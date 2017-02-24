package com.vte.libgdx.ortho.test.box2d;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.components.PathComponent;

/**
 * Created by vincent on 20/07/2016.
 */

public class PathHero extends PathMap {
      static public final float CHECK_RADIUS = 0.01f;
    Entity entity;

    public PathHero() {
      super();
        setLoop(false);
        setRevert(false);
        setVelocityCte(3);
        entity = new Entity();
        entity.add(new PathComponent(this));
        EntityEngine.getInstance().addEntity(entity);
    }


    public void destroy() {
        if (entity != null) {
            EntityEngine.getInstance().removeEntity(entity);
            entity = null;
        }
    }

    public void render(ShapeRenderer renderer) {
        for (int i = currentPointIndex; i < positions.size() - 1; i++) {
            Vector2 pointStart = positions.get(i);
            Vector2 pointEnd = positions.get(i + 1);
            renderer.line(pointStart.x, pointStart.y, 0, pointEnd.x, pointEnd.y, 0, Color.YELLOW, Color.YELLOW);
        }
    }
}
