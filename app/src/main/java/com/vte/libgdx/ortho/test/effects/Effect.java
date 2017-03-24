package com.vte.libgdx.ortho.test.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.AssetsUtility;
import com.vte.libgdx.ortho.test.MyGame;

import java.util.ArrayList;

/**
 * Created by vincent on 25/02/2017.
 */

public class Effect {
    public enum Type {
        FREEZE,
        BURN,
        PORTAL,
        WAVE
    }

    public Type id;
    public String atlasFile;
    public String particuleFile;
    public float particuleScale;
    public float distance;
    public float duration;
    public String targetState;
    public float targetDuration; // 0 => effect duration or stop collision, -1 => target state duration, >0 targetDuration value
    public String description;
    public String sound;
    public int fps;
    public ArrayList<String> frames;
    public String icon;

    private TextureAtlas mAtlas;
    private Animation mAnimation;
    private TextureRegion mFixRegion;
    private boolean mIsParticule;
    private float mLastEffectTime=0;

 /*   ParticleEffectPool mEffectPool;
    Array<PooledEffect> effects = new Array();*/
 ParticleEffect mEffect = new ParticleEffect();
    boolean isEfectInit = false;

    public Effect() {
    }

    public void init()
    {
        init(true);
    }
    protected void init(boolean decodeString) {
        if(decodeString) {
            description = AssetsUtility.getString(description);
        }

        mAtlas = new TextureAtlas("data/effects/" + atlasFile);

        if (particuleFile != null && !particuleFile.isEmpty()) {
            mEffect = new ParticleEffect();
            mEffect.load(Gdx.files.internal("data/effects/" + particuleFile), Gdx.files.internal("data/effects"));
            mIsParticule = true;
            /* ParticleEffect particuleEffect = new ParticleEffect();
            particuleEffect.setEmittersCleanUpBlendFunction(false);
            particuleEffect.load(Gdx.files.internal("data/effects/" + particuleFile), Gdx.files.internal("data/effects"));

            mEffectPool = new ParticleEffectPool(particuleEffect, 1, 2);

            // Create effect:
            PooledEffect effect = mEffectPool.obtain();
            effect.setPosition(x, y);
            effects.add(effect);
            mIsParticule = true;
            effect.start();*/

        }

        if (frames == null || frames.size() <= 0)
            return;

        if (fps == 0 || frames.size() == 1) {
            mFixRegion = mAtlas.findRegion(frames.get(0));
        } else {
            Array<TextureRegion> regions = new Array<TextureRegion>();
            for (String key : frames) {
                TextureRegion reg = mAtlas.findRegion(key);
                if (reg != null) {
                    regions.add(reg);
                }
            }

            mAnimation = new Animation((1F / (float) fps), regions);
            mAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }
    }

    public TextureRegion getTextureRegion(float aTime) {
        if (mFixRegion != null) {
            return mFixRegion;
        } else if (mAnimation != null) {
            return mAnimation.getKeyFrame(aTime, true);
        } else
            return null;
    }

    public TextureRegion getIcon() {
        return mAtlas.findRegion(icon);
    }

    public Animation getAnimation() {
        return mAnimation;
    }

    public void setPlayMode(Animation.PlayMode playMode) {
        mAnimation.setPlayMode(playMode);
    }

    public Animation.PlayMode getPlayMode() {
        return mAnimation.getPlayMode();
    }

    public void renderEffect(Batch aBatch, int srcWidth, int srcHeight, float x, float y, float scale, float deltaT) {
        TextureRegion effectRegion = getTextureRegion(deltaT);

        if (effectRegion != null) {
            float width = effectRegion.getRegionWidth();
            float height = effectRegion.getRegionHeight();
            float offsetX = scale * (srcWidth / 2 - effectRegion.getRegionWidth() / 2);
            float offsetY = scale * (srcHeight / 2 - effectRegion.getRegionHeight() / 2);

            float effectScale = distance;
            if (effectScale <= 0) {
                effectScale = 1;
            }
            aBatch.draw(effectRegion,
                    x, y,
                    offsetX, offsetY,
                    width, height,
                    scale * effectScale, scale * effectScale,
                    0);
        }
        if (mIsParticule) {
            float offsetX =scale * (srcWidth / 2 );
            float offsetY = scale * (srcHeight / 2);

            if(!isEfectInit)
           {
               isEfectInit=true;
               mEffect.start();
               mEffect.scaleEffect(MyGame.SCALE_FACTOR*particuleScale);
           }
            mEffect.setPosition(x+offsetX, y+offsetY);

            mEffect.draw(aBatch, deltaT-mLastEffectTime);
            mLastEffectTime = deltaT;

//We need to reset the batch to the original blend state as we have setEmittersCleanUpBlendFunction as false in additiveEffect
            aBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public Effect clone() {
        Effect clone = new Effect();
        clone.id = id;
        clone.atlasFile = atlasFile;
        clone.distance = distance;
        clone.duration = duration;
        clone.targetState = targetState;
        clone.targetDuration = targetDuration;
        clone.description = description;
        clone.sound = sound;
        clone.fps = fps;
        clone.frames = frames;
        clone.icon = icon;
        clone.particuleFile = particuleFile;
        clone.particuleScale = particuleScale;

        clone.init(false);

        return clone;

    }


}
