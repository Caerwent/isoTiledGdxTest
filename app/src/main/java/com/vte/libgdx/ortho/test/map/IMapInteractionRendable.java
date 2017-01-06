package com.vte.libgdx.ortho.test.map;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by vincent on 05/01/2017.
 */

public interface IMapInteractionRendable {
    public boolean isRendable();

    public boolean isRended();

    public void setRended(boolean aRended);

    public void render(Batch batch);
}
