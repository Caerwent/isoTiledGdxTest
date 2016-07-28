package com.vte.libgdx.ortho.test.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vte.libgdx.ortho.test.entity.components.PathComponent;

/**
 * Created by vincent on 26/07/2016.
 */

public class PathRenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private ComponentMapper<PathComponent> pm = ComponentMapper.getFor(PathComponent.class);

    ShapeRenderer mRenderer;

    public PathRenderSystem(ShapeRenderer renderer) {
        mRenderer = renderer;
    }

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PathComponent.class).get());
    }

    public void update(float deltaTime) {
        if (entities.size() <= 0)
            return;
        boolean shouldEndBatch = false;
        if (!mRenderer.isDrawing()) {
            mRenderer.begin();
            shouldEndBatch = true;
        }

        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PathComponent path = pm.get(entity);
            path.mPath.render(mRenderer);

        }

        if (shouldEndBatch) {
            mRenderer.end();
        }
    }


}