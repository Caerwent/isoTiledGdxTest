package com.vte.libgdx.ortho.test.gui.challenge;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.vte.libgdx.ortho.test.screens.GenericUI;

/**
 * Created by vincent on 04/04/2017.
 */

public class ChallengeUI extends Window {

    public static enum ChallengeType {
        TEST(ChallengeTest.class);

        protected Class mClass;

        ChallengeType(Class aClass)
        {
            mClass= aClass;
        }
    }

    public static ChallengeUI createInstance(ChallengeType aType) {

        try {
            return (ChallengeUI) aType.mClass.newInstance();
        }
         catch (InstantiationException e) {
             e.printStackTrace();
         } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;

    }

    public ChallengeUI() {
        super("", GenericUI.getInstance().getSkin(), "solidbackground");
    }


    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);
        //draw content

    }

    @Override
    public void act(float delta){
        // update content
        super.act(delta);
    }

    public void release()
    {

    }
}
