package com.vte.libgdx.ortho.test.gui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.effects.Effect;
import com.vte.libgdx.ortho.test.effects.EffectFactory;
import com.vte.libgdx.ortho.test.screens.GameScreen;

import static com.vte.libgdx.ortho.test.MyGame.ScreenType.MainGame;
import static com.vte.libgdx.ortho.test.Settings.TARGET_WIDTH;

/**
 * Created by gwalarn on 16/11/16.
 */

public class TestActor extends Group  {

    private static final int DIALOG_W = TARGET_WIDTH/4*3;
    private static final int DIALOG_H = 100;

    protected final Table mainTable = new Table();
    private InventoryTable mInventorySlotTable = new InventoryTable();
    protected final DialogTable mDialogTable = new DialogTable(DIALOG_W, DIALOG_H);
    protected Image mInventoryButton ;
    protected Image mSpellButton ;
    /**
     * the duration of the screen transition for the screenOut method
     */
    public float dur;


    public TestActor() {

        mainTable.setSize(128, 64);


        this.addActor(mainTable);
        mainTable.setBackground(UIStage.getInstance().getSkin().getDrawable("window1"));
        mainTable.setColor(UIStage.getInstance().getSkin().getColor("dark-blue"));
        mainTable.setSkin(UIStage.getInstance().getSkin());

        mInventoryButton = new Image();
        mInventoryButton.setDrawable(new TextureRegionDrawable(UIStage.getInstance().getTextureAtlas().findRegion("bag")));
        mainTable.add(mInventoryButton);

        mSpellButton= new Image();
        mSpellButton.setDrawable(new TextureRegionDrawable(UIStage.getInstance().getTextureAtlas().findRegion("spell")));
        mainTable.add(mSpellButton);

        mainTable.row();

        mInventorySlotTable.setPosition(50, 25);
        addActor(mInventorySlotTable);
        mInventorySlotTable.setVisible(false);
      //  mInventorySlotTable.setTouchable(Touchable.enabled);

      /*  secondTable.addCaptureListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });*/

        mDialogTable.setPosition((TARGET_WIDTH-DIALOG_W)/2, 0);
        mDialogTable.setVisible(false);
        addActor(mDialogTable);
        mInventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mInventorySlotTable.isVisible()) {
                    mInventorySlotTable.setVisible(false);
                } else {
                    mInventorySlotTable.setVisible(true);
                }
            }
        });
        mInventoryButton.setTouchable(Touchable.enabled);

        mSpellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((GameScreen) MyGame.getInstance().getScreenType(MainGame)   ).getMap().getPlayer().getHero().launchEffect(EffectFactory.getInstance().getEffect(Effect.Type.FREEZE));
            }
        });
        mInventoryButton.setTouchable(Touchable.enabled);

    }



}
