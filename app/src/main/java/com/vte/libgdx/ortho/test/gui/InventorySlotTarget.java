package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.items.ItemCombinaison;
import com.vte.libgdx.ortho.test.items.ItemFactory;

/**
 * Created by vincent on 24/02/2017.
 */

public class InventorySlotTarget extends Target {

    InventorySlot _targetSlot;

    public InventorySlotTarget(InventorySlot actor) {
        super(actor);
        _targetSlot = actor;
    }

    @Override
    public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
        return true;
    }

    @Override
    public void reset(Source source, Payload payload) {
    }

    @Override
    public void drop(Source source, Payload payload, float x, float y, int pointer) {
        InventorySlot sourceSlot = ((InventorySlotSource) source).getSourceSlot();

        if (sourceSlot ==_targetSlot) {
            return;
        }

        //First, does the target accept the source item type?
        Item sourceItem = sourceSlot.getItemOnTop();
        Item targetItem = _targetSlot.getItemOnTop();
        if(sourceItem!=null && targetItem!=null)
        {
            ItemCombinaison combinaison = ItemFactory.getInstance().getItemCombinaison(sourceItem.getItemTypeID(), targetItem.getItemTypeID());
            if(combinaison!=null && combinaison.resultItem!=null)
            {
                EventDispatcher.getInstance().onItemFound(ItemFactory.getInstance().getInventoryItem(combinaison.resultItem));
                EventDispatcher.getInstance().onItemLost(sourceItem);
                EventDispatcher.getInstance().onItemLost(targetItem);


            }

        }



    }
}
