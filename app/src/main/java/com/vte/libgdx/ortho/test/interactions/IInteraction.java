package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by vincent on 08/02/2017.
 */

public interface IInteraction {
    public enum Type {
        ITEM,
        CHESS,
        NPJ,
        PORTAL,
        MONSTER,
        PATH
    }


    public void setCamera(Camera aCamera);
    public String getId();
    public float getX();

    public float getY();

    public Type getType();

    public boolean isClickable();

    public boolean isMovable();

    public boolean isPersistent();

    public boolean isRendable();

    public boolean isRended();

    public void setRended(boolean aRended);

    public void render(Batch batch);

    public void destroy();

    public void update(float dt);
}
