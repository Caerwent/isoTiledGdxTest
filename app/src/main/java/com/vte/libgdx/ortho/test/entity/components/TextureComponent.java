package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class TextureComponent implements Component {
    public TextureRegion region;

    public TextureComponent() {
        region = null;
    }

    public TextureComponent(TextureComponent other) {
        region = other.region;
    }

    public void reset() {
        region = null;
    }
}
