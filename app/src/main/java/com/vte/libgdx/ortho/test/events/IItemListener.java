package com.vte.libgdx.ortho.test.events;

import com.vte.libgdx.ortho.test.items.Item;

/**
 * Created by vincent on 13/01/2017.
 */

public interface IItemListener {
    public void onItemFound(Item aItem);
    public void onItemLost(Item aItem);
}
