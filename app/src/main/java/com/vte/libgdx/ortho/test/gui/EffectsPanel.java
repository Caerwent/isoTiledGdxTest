package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vte.libgdx.ortho.test.Settings;
import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.effects.EffectFactory;
import com.vte.libgdx.ortho.test.events.EventDispatcher;
import com.vte.libgdx.ortho.test.persistence.Profile;

import java.util.ArrayList;

/**
 * Created by vincent on 05/03/2017.
 */

public class EffectsPanel extends Table {

    private Table mList = new Table();
    private ArrayList<Table> mSlots = new ArrayList<>();
    private Table mSelectedItem;

    private InventoryDetails mDetails;

    public EffectsPanel() {
        super();
        init();
    }


    private void init() {
        mList.setSkin(UIStage.getInstance().getSkin());
        mList.setName("Effects_Slot_Table");
        ;
        mDetails = new InventoryDetails(200, (Settings.TARGET_HEIGHT - 64) / 2);
        left().add(mList).expandY().fillY();
        add(mDetails).left().top();
        row();
        mDetails.setVisible(false);

    }

    public void update() {
        mList.clear();
        mSlots.clear();
        int idx = 0;
        for (Effect.Type effectType : Effect.Type.values()) {
            Effect effect = EffectFactory.getInstance().getEffect(effectType);
            Table slot = new Table();
            slot.setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
            mSlots.add(slot);
            Image img = new Image(effect.getIcon());
            slot.setColor(UIStage.getInstance().getSkin().getColor("lt-blue"));
            slot.top().left().add(img).size(64, 64);
            slot.row();
            slot.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    int idx = mSlots.indexOf(event.getListenerActor());
                    if (idx >= 0) {
                        Effect clickedEffect = EffectFactory.getInstance().getEffect(Effect.Type.values()[idx]);
                        Effect.Type selectedEffectType = Profile.getInstance().getSelectedEffect();
                        if (selectedEffectType != null) {
                            Effect selectedEffect = EffectFactory.getInstance().getEffect(selectedEffectType);
                            if (selectedEffect != clickedEffect) {
                                EventDispatcher.getInstance().onNewSelectedEffect(Effect.Type.values()[idx]);
                            }
                        }

                    }
                }
            });
            Effect.Type selectedEffectType = Profile.getInstance().getSelectedEffect();
            if (selectedEffectType != null && selectedEffectType == effectType) {
                setSelected(idx);

            }
            idx++;

            mList.top().left().add(slot);
        }

    }


    private void setSelected(int idx) {
        if (idx >= 0) {

            if (mSelectedItem != null) {
                mSelectedItem.setColor(UIStage.getInstance().getSkin().getColor("lt-blue"));
            }
            mSelectedItem = mSlots.get(idx);
            mSelectedItem.setColor(UIStage.getInstance().getSkin().getColor("dark-blue"));

            mDetails.setText(EffectFactory.getInstance().getEffect(Effect.Type.values()[idx]).description);
        }

    }
}
