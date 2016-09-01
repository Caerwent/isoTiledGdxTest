package com.vte.libgdx.ortho.test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.box2d.Path;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.ICollisionHandler;
import com.vte.libgdx.ortho.test.entity.components.BobComponent;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;
import com.vte.libgdx.ortho.test.entity.components.TransformComponent;
import com.vte.libgdx.ortho.test.entity.components.VelocityComponent;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;


/**
 * Created by vincent on 01/07/2016.
 */

public class Bob extends Entity implements ICollisionHandler {
    public static final float BOB_RESIZE_FACTOR = 1F;
    Animation[] walkAnimation; // animation instance
    Texture walkSheet; // sprite sheet
    TextureRegion currentFrame; // current animation frame
    static final int ANIM_IDX_REAR = 0;
    static final int ANIM_IDX_RIGHT = 1;
    static final int ANIM_IDX_FACE = 2;
    static final int ANIM_IDX_LEFT = 3;
    private int mCurrentAnimationIdx= 0;
    float stateTime; // elapsed time
    private static float ANIMATION_TIME_PERIOD = 0.08f;// this specifies the time between two consecutive frames of animation
    static Texture bobSpriteSheet; // texture sprite sheet for the bob

    public boolean rended = false;

    private Array<CollisionComponent> mCollisions = new Array<CollisionComponent>();
    private Rectangle mBounds = new Rectangle();
    private Polygon mPolygonBound = new Polygon();
    private float[] mVertices = new float[8];

    Path path;

    public void SetPath(Path p) {
        path = p;
        path.Reset();
        VisualComponent visual = this.getComponent(VisualComponent.class);
        setPosition(path.GetCurrentPoint().x,path.GetCurrentPoint().y);
        setVelocity(path.GetVelocity());
        stateTime=0;
    }


    public void update(float dt) {

        if (path != null) {
            if (path.hasNextPoint()) {
                TransformComponent transform = this.getComponent(TransformComponent.class);

                Vector2 pos2D = new Vector2(transform.position.x, transform.position.y);
                path.UpdatePath(pos2D, dt);
                setVelocity(path.GetVelocity());
                stateTime+=dt;

            } else {
                path.destroy();
                path = null;
                setVelocity(0, 0);
                stateTime=0;
            }

        }
        else
        {
            setVelocity(0, 0);
            stateTime=0;
        }

        CollisionComponent collision = this.getComponent(CollisionComponent.class);
        collision.mBound = getPolygonBound();
    }

    public Bob() {
        super();
        EntityEngine.getInstance().addEntity(this);

        this.add(new VelocityComponent());
        this.add(new TransformComponent());
        this.add(new BobComponent(this));


    }

    public void render(Batch batch) {
        TransformComponent transform = this.getComponent(TransformComponent.class);
        VisualComponent visual = this.getComponent(VisualComponent.class);
        VelocityComponent velocity = this.getComponent(VelocityComponent.class);

        if(velocity.y==0 && velocity.x==0)
        {
            mCurrentAnimationIdx=ANIM_IDX_FACE;
            stateTime=0;
        }
        else {
            if (velocity.y == 0) {
                mCurrentAnimationIdx = velocity.x < 0 ? ANIM_IDX_LEFT : ANIM_IDX_RIGHT;
            } else
            {
                double angle = Math.atan2(velocity.y, velocity.x);
                double PI4 = Math.PI/4;
                if (angle < 0)
                {
                    angle += Math.PI * 2;
                }

                if(angle>7*PI4 || angle<=PI4)
                {
                    mCurrentAnimationIdx=ANIM_IDX_RIGHT;
                }else if(angle>PI4 && angle<=3*PI4)
                {
                    mCurrentAnimationIdx=ANIM_IDX_REAR;
                }
                else if(angle>3*PI4 && angle<=5*PI4)
                {
                    mCurrentAnimationIdx=ANIM_IDX_LEFT;
                }
                else if(angle>5*PI4 && angle<=7*PI4)
                {
                    mCurrentAnimationIdx=ANIM_IDX_FACE;
                }
            }
        }
        currentFrame = walkAnimation[mCurrentAnimationIdx].getKeyFrame(stateTime, true);
        visual.region=currentFrame;
        float width = visual.region.getRegionWidth();
        float height = visual.region.getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        //Allow for Offset
        float originX = 0;//transform.originOffset.x;
        float originY = 0;//transform.originOffset.y;

        batch.draw(visual.region,
                transform.position.x +transform.originOffset.x, transform.position.y +transform.originOffset.y,
                originX, originY,
                width, height,
                transform.scale, transform.scale,
                transform.angle);
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

    public Vector2 getPosition()
    {
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

    public Rectangle getBound() {
        TransformComponent tfm = this.getComponent(TransformComponent.class);
        mBounds.x = tfm.position.x + tfm.originOffset.x;
        mBounds.y = tfm.position.y + tfm.originOffset.y;
        mBounds.setSize(walkSheet.getWidth() / 3 * tfm.scale, walkSheet.getHeight() / 4 * tfm.scale);
        return mBounds;
    }

    public Polygon getPolygonBound() {
        TransformComponent tfm = this.getComponent(TransformComponent.class);
        mBounds.x = tfm.position.x + tfm.originOffset.x;
        mBounds.y = tfm.position.y + tfm.originOffset.y;
        mBounds.setSize(walkSheet.getWidth() / 3 * tfm.scale, walkSheet.getHeight() / 4 * tfm.scale);

        mVertices[0] = tfm.originOffset.x;
        mVertices[1] = tfm.originOffset.y;
        mVertices[2] = mBounds.getWidth()+tfm.originOffset.x;
        mVertices[3] = tfm.originOffset.y;
        mVertices[4] = mBounds.getWidth()+tfm.originOffset.x;
        mVertices[5] = mBounds.getHeight()+tfm.originOffset.y;
        mVertices[6] = tfm.originOffset.x;
        mVertices[7] = mBounds.getHeight()+tfm.originOffset.y;
        mPolygonBound.setVertices(mVertices);
        mPolygonBound.setPosition(tfm.position.x, tfm.position.y);

        return mPolygonBound;
    }

    public void initialize(float width, float height, Texture walkSheet) {
        // instantiate bob sprite

        this.walkSheet = walkSheet; // save the sprite sheet
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
        transform.scale = MyGame.SCALE_FACTOR * BOB_RESIZE_FACTOR;
transform.setOriginOffset(-walkSheet.getWidth() / 3 * transform.scale/2,- walkSheet.getHeight() / 4 * transform.scale/2);
        this.add(new VisualComponent(currentFrame));

        setPosition(0, 0);

        this.add(new CollisionComponent(CollisionComponent.Type.BOB, getPolygonBound(), this));


    }

    @Override
    public void onCollisionStart(CollisionComponent aEntity) {
        if (!mCollisions.contains(aEntity, false)) {

            if(aEntity.mBound.getBoundingRectangle().getY() > mBounds.getY())
                return;

            Gdx.app.debug("DEBUG", "collision start");
            mCollisions.add(aEntity);
            if(aEntity.mType==CollisionComponent.Type.OBSTACLE && path!=null) {
                path.destroy();
                path = null;
                setVelocity(0, 0);
            }
        }
    }

    @Override
    public void onCollisionStop(CollisionComponent aEntity) {
        if (mCollisions.contains(aEntity, false)) {
            mCollisions.removeValue(aEntity, false);
            Gdx.app.debug("DEBUG", "collision stop");
        }
    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
    }
}
