package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by vincent on 07/07/2016.
 */

public class VisualComponent implements Component {
    public TextureRegion region;

    public VisualComponent(TextureRegion region) {
        this.region = region;
    }
}