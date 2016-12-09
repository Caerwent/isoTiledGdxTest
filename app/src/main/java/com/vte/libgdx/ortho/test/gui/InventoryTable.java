package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.vte.libgdx.ortho.test.Settings;
import com.vte.libgdx.ortho.test.items.Item;
import com.vte.libgdx.ortho.test.player.Player;

/**
 * Created by vincent on 09/12/2016.
 */

public class InventoryTable extends Table implements Player.IPlayerListener{
    private final int mSlotWidth = 66;
    private final int mSlotHeight = 66;
    private int mLengthSlotRow = 2;
    private InventorySlot mSelectedItem;

    public InventoryTable () {
        super(null);
        init();
    }

    /** Creates a table with a skin, which enables the {@link #add(CharSequence)} and {@link #add(CharSequence, String)} methods to
     * be used. */
    public InventoryTable (Skin skin) {
       super(skin);
        init();
    }

    private void init()
    {
        setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
        setColor(UIStage.getInstance().getSkin().getColor("lt-blue"));
        setSkin(UIStage.getInstance().getSkin());
        align(Align.topLeft);
        setName("Inventory_Slot_Table");
        setSize(mLengthSlotRow*mSlotWidth+5, Settings.TARGET_HEIGHT - 50);
        Player.getInstance().registerListener(this);

    }

    public void update()
    {
        int nbItemInRow=0;
        clear();
        for(Item item : Player.getInstance().getInventory())
        {
            InventorySlot inventorySlot = new InventorySlot();
            inventorySlot.add(item);
            add(inventorySlot).size(mSlotWidth, mSlotHeight);

            inventorySlot.addListener(new ClickListener() {
                @Override
                public void clicked (InputEvent event, float x, float y)
                {
                    if(mSelectedItem!=null)
                    {
                        mSelectedItem.setSelected(false);
                    }
                    if(event.getRelatedActor() instanceof  InventorySlot)
                    {
                        mSelectedItem = (InventorySlot) event.getRelatedActor();
                        mSelectedItem.setSelected(true);
                    }
                }
            });

            nbItemInRow++;
            if(nbItemInRow % mLengthSlotRow == 0){
                row();
            }

        }
    }

    @Override
    public void onInventoryChanged() {
        update();
    }
}
