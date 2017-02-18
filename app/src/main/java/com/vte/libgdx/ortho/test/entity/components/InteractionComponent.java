package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.vte.libgdx.ortho.test.interactions.IInteraction;

/**
 * Created by vincent on 20/07/2016.
 */

public class InteractionComponent implements Component {
    public IInteraction interaction;

    public InteractionComponent(IInteraction aInteraction) {
        interaction = aInteraction;

    }
}