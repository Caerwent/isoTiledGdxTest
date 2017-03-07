package com.vte.libgdx.ortho.test.effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by vincent on 25/02/2017.
 */

public class Effect {
    public enum Type {
        FREEZE,
        BURN
    }
    public Type id;
    public String atlasFile;
    public float distance;
    public float duration;
    public String targetState;
    public float targetDuration;
    public String description;
    public int fps;
    public ArrayList<String> frames;
    public String icon;

    private TextureAtlas mAtlas;
    private Animation mAnimation;
    private TextureRegion mFixRegion;

    public Effect() {
    }
    public void init()
    {
        mAtlas = new TextureAtlas("data/effects/" + atlasFile);

        if(frames==null || frames.size()<=0)
            return;

        if(fps==0 || frames.size()==1) {
            mFixRegion=mAtlas.findRegion(frames.get(0));
        }
        else {
            Array<TextureRegion> regions = new Array<TextureRegion>();
            for (String key : frames) {
                TextureRegion reg = mAtlas.findRegion(key);
                if (reg != null) {
                    regions.add(reg);
                }
            }

            mAnimation = new Animation((1F / (float)fps), regions);
            mAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }
    }

    public TextureRegion getTextureRegion(float aTime)
    {
        if(mFixRegion!=null)
        {
            return mFixRegion;
        }
        return mAnimation.getKeyFrame(aTime, true);
    }

    public TextureRegion getIcon()
    {
        return mAtlas.findRegion(icon);
    }


}
