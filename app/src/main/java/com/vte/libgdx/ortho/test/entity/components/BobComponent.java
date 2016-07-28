package com.vte.libgdx.ortho.test.entity.components;

import com.badlogic.ashley.core.Component;
import com.vte.libgdx.ortho.test.Bob;

/**
 * Created by vincent on 20/07/2016.
 */

public class BobComponent implements Component {
    public Bob bob;

    public BobComponent(Bob aBob) {
        bob = aBob;

    }
}