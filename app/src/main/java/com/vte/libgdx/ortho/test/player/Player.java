package com.vte.libgdx.ortho.test.player;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.items.Item;

/**
 * Created by gwalarn on 05/12/16.
 */

public class Player {
    static private Player sPlayer = new Player();

    public static Player getInstance() {
        return sPlayer;
    }

    private Array<Item> mInventory;

    public Player()
    {
        mInventory = new Array<Item>();
    }

    public Array<Item> getInventory()
    {
        return mInventory;
    }
}
