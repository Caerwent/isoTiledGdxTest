package com.vte.libgdx.ortho.test.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;

/**
 * Created by vincent on 07/07/2016.
 */

public class VisualRenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<VisualComponent> vm = ComponentMapper.getFor(VisualComponent.class);

    public VisualRenderSystem(OrthographicCamera camera) {
        batch = new SpriteBatch();

        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(TransformComponent.class, VisualComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {

    }

    @Override
    public void update(float deltaTime) {
        TransformComponent transform;
        VisualComponent visual;

        camera.update();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        for (int i = 0; i < entities.size(); ++i) {
            Entity e = entities.get(i);

            transform = tm.get(e);
            visual = vm.get(e);

            float width = visual.region.getRegionWidth();
            float height = visual.region.getRegionHeight();
            float halfWidth = width/2f;
            float halfHeight = height/2f;
            //Allow for Offset
            float originX = halfWidth + transform.originOffset.x;
            float originY = halfHeight + transform.originOffset.y;

            batch.draw(visual.region,
                    transform.position.x - halfWidth, transform.position.y - halfHeight,
                    originX, originY,
                    width, height,
                    transform.scale, transform.scale,
                    transform.angle);
        }

        batch.end();
    }
}