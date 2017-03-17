package com.vte.libgdx.ortho.test.interactions;

import com.badlogic.gdx.graphics.Camera;
import com.vte.libgdx.ortho.test.map.IMapRendable;

/**
 * Created by vincent on 08/02/2017.
 */

public interface IInteraction extends IMapRendable {
    public enum Type {
        ITEM,
        HERO,
        CHESS,
        NPC,
        PORTAL,
        MONSTER,
        ACTIVATOR,
        OBSTACLE,
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

    public void destroy();

    public void update(float dt);
}
