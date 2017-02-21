package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.vte.libgdx.ortho.test.interactions.InteractionHero;

/**
 * Created by vincent on 20/07/2016.
 */

public class BobComponent implements Component {
    public InteractionHero bob;

    public BobComponent(InteractionHero aBob) {
        bob = aBob;

    }
}