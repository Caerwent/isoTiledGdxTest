package com.vte.libgdx.ortho.test.characters;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.box2d.PolygonShape;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VelocityComponent;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;
import com.vte.libgdx.ortho.test.map.IMapRendable;

/**
 * Created by vincent on 05/01/2017.
 */

public class Character extends Entity implements ICollisionHandler, IMapRendable {

    public String mId;
    public String mType;
    public String mSpriteSheet;
    public String mDialogId;

    static final int ANIM_IDX_REAR = 0;
    static final int ANIM_IDX_RIGHT = 1;
    static final int ANIM_IDX_FACE = 2;
    static final int ANIM_IDX_LEFT = 3;

    protected Animation[] walkAnimation; // animation instance
    protected Texture walkSheet; // sprite sheet
    protected TextureRegion currentFrame; // current animation frame

    protected int mCurrentAnimationIdx = 0;
    protected float stateTime; // elapsed time
    protected static float ANIMATION_TIME_PERIOD = 0.08f;// this specifies the time between two consecutive frames of animation
    protected boolean mIsRended = false;

    protected Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();
    protected PolygonShape mPolygonShape = new PolygonShape();
    protected float[] mVertices = new float[8];

    public Character(CharacterDef aDef) {
        this(aDef.id, aDef.type, aDef.spritesheet, aDef.dialogId);
    }

    public Character(String aId, String aType, String aSpiteSheet, String aDialogId) {
        super();

        mId = aId;
        mType = aType;
        mSpriteSheet = aSpiteSheet;
        mDialogId = aDialogId;
    }

    public String getType() {
        return mType;
    }

    public String getDialogId() {
        return mDialogId;
    }

    public void setDialogId(String aDialogId)
    {
        mDialogId = aDialogId;
    }

    public String getId() {
        return mId;
    }

    public void initialize() {
        EntityEngine.getInstance().addEntity(this);

        this.add(new VelocityComponent());
        this.add(new TransformComponent());
        walkSheet = new
                Texture(Gdx.files.internal("data/" + mSpriteSheet));
// initi
//split the sprite sheet into different textures
        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / 3, walkSheet.getHeight() / 4);
// convert 2D array to 1D

        walkAnimation = new Animation[4];
        for (int i = 0; i < tmp.length; i++) {
            Array<TextureRegion> walkFrames = new Array();
            for (int j = 0; j < tmp[0].length; j++) {
                walkFrames.add(tmp[i][j]);
            }
            walkAnimation[i] = new Animation(ANIMATION_TIME_PERIOD, walkFrames);

            // set the animation to loop
            walkAnimation[i].setPlayMode(Animation.PlayMode.LOOP);
        }
// create a new animation sequence with the walk frames and time periodof specified seconds
//
//
// get initial frame
        currentFrame = walkAnimation[ANIM_IDX_FACE].getKeyFrame(stateTime, true);

        TransformComponent transform = this.getComponent(TransformComponent.class);
        transform.scale = MyGame.SCALE_FACTOR;
        transform.setOriginOffset(-walkSheet.getWidth() / 3 * transform.scale / 2, -walkSheet.getHeight() / 4 * transform.scale / 2);
        this.add(new VisualComponent(currentFrame, this));

        setPosition(0, 0);

        this.add(new CollisionComponent(CollisionComponent.CHARACTER, getShape(), mId, this, this));


    }

    public void setPosition(float x, float y) {
        TransformComponent transformComponent = this.getComponent(TransformComponent.class);
        transformComponent.position.x = x;
        transformComponent.position.y = y;

    }

    public void setPosition(Vector2 pos) {
        TransformComponent transformComponent = this.getComponent(TransformComponent.class);
        transformComponent.position.x = pos.x;
        transformComponent.position.y = pos.y;

    }

    public Vector2 getPosition() {
        TransformComponent transformComponent = this.getComponent(TransformComponent.class);
        return new Vector2(transformComponent.position.x, transformComponent.position.y);
    }

    public void setVelocity(float vx, float vy) {
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);
        velocity.x = vx;
        velocity.y = vy;
    }

    public void setVelocity(Vector2 v) {
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);
        velocity.x = v.x;
        velocity.y = v.y;
    }

    public boolean isRendable() {
        return true;
    }

    public boolean isRended() {
        return mIsRended;
    }

    public void setRended(boolean aRended) {
        mIsRended = aRended;
    }

    @Override
    public void render(Batch batch) {
        TransformComponent transform = this.getComponent(TransformComponent.class);
        VisualComponent visual = this.getComponent(VisualComponent.class);
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);

        if (velocity.y == 0 && velocity.x == 0) {
            mCurrentAnimationIdx = ANIM_IDX_FACE;
            stateTime = 0;
        } else {
            if (velocity.y == 0) {
                mCurrentAnimationIdx = velocity.x < 0 ? ANIM_IDX_LEFT : ANIM_IDX_RIGHT;
            } else {
                double angle = Math.atan2(velocity.y, velocity.x);
                double PI4 = Math.PI / 4;
                if (angle < 0) {
                    angle += Math.PI * 2;
                }

                if (angle > 7 * PI4 || angle <= PI4) {
                    mCurrentAnimationIdx = ANIM_IDX_RIGHT;
                } else if (angle > PI4 && angle <= 3 * PI4) {
                    mCurrentAnimationIdx = ANIM_IDX_REAR;
                } else if (angle > 3 * PI4 && angle <= 5 * PI4) {
                    mCurrentAnimationIdx = ANIM_IDX_LEFT;
                } else if (angle > 5 * PI4 && angle <= 7 * PI4) {
                    mCurrentAnimationIdx = ANIM_IDX_FACE;
                }
            }
        }
        currentFrame = walkAnimation[mCurrentAnimationIdx].getKeyFrame(stateTime, true);
        visual.region = currentFrame;
        float width = visual.region.getRegionWidth();
        float height = visual.region.getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        //Allow for Offset
        float originX = 0;//transform.originOffset.x;
        float originY = 0;//transform.originOffset.y;

        batch.draw(visual.region,
                transform.position.x + transform.originOffset.x, transform.position.y + transform.originOffset.y,
                originX, originY,
                width, height,
                transform.scale, transform.scale,
                transform.angle);
    }


    public PolygonShape getShape() {
        TransformComponent tfm = this.getComponent(TransformComponent.class);
        mPolygonShape.setX(tfm.position.x + tfm.originOffset.x);
        mPolygonShape.setY(tfm.position.y + tfm.originOffset.y);
        float width = walkSheet.getWidth() / 3 * tfm.scale;
        float height = walkSheet.getHeight() / 4 * tfm.scale;


        mVertices[0] = tfm.originOffset.x;
        mVertices[1] = tfm.originOffset.y;
        mVertices[2] = width + tfm.originOffset.x;
        mVertices[3] = tfm.originOffset.y;
        mVertices[4] = width + tfm.originOffset.x;
        mVertices[5] = height + tfm.originOffset.y;
        mVertices[6] = tfm.originOffset.x;
        mVertices[7] = height + tfm.originOffset.y;
        mPolygonShape.getShape().setVertices(mVertices);
        mPolygonShape.getShape().setPosition(tfm.position.x, tfm.position.y);

        return mPolygonShape;
    }


    public void update(float dt) {
        CollisionComponent collision = this.getComponent(CollisionComponent.class);
        collision.mShape = getShape();
    }

    @Override
    public boolean onCollisionStart(CollisionComponent aEntity) {

        if (!mCollisions.contains(aEntity, false)) {

            if (aEntity.mShape.getBounds().getY() > mPolygonShape.getBounds().getY())
                return false;

            Gdx.app.debug("DEBUG", "collision start");
            mCollisions.add(aEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCollisionStop(CollisionComponent aEntity) {
        if (mCollisions.contains(aEntity, false)) {
            mCollisions.removeValue(aEntity, false);
            Gdx.app.debug("DEBUG", "collision stop");
            return true;
        }
        return false;
    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
    }
}
