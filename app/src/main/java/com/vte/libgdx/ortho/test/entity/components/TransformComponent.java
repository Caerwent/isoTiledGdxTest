package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class TransformComponent implements Component {
    public Vector3 position;
    public Vector2 originOffset = new Vector2(0f, 0f);
    public float scale;
    public float angle;

    public TransformComponent() {
        position = new Vector3();
        reset();
    }

    public TransformComponent(TransformComponent other) {
        position = new Vector3(other.position);
        scale = other.scale;
        angle = other.angle;
    }

    public void reset() {
        position.set(0.0f, 0.0f, 0.0f);
        scale = 1.0f;
        angle = 0.0f;
    }

    public TransformComponent setPosition(float x, float y){
        return setPosition(x, y, this.position.z);
    }
    public TransformComponent setPosition(float x, float y, float z){
        this.position.set(x, y, z);
        return this;
    }

    public TransformComponent setOriginOffset(float x, float y){
        this.originOffset.set(x, y);
        return this;
    }
}
