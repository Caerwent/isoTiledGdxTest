package com.vte.libgdx.ortho.test;

import com.badlogic.ashley.core.Entity;
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
    Animation walkAnimation; // animation instance
    Texture walkSheet; // sprite sheet
    TextureRegion currentFrame; // current animation frame
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
        setPosition(path.GetCurrentPoint());
        setVelocity(path.GetVelocity());
    }


    public void update(float dt) {

        if (path != null) {
            if (path.hasNextPoint()) {
                TransformComponent transform = this.getComponent(TransformComponent.class);
                Vector2 pos2D = new Vector2(transform.position.x, transform.position.y);
                if (path.UpdatePath(pos2D, dt)) {
                    setVelocity(path.GetVelocity());


                }
            } else {
                path.destroy();
                path = null;
                setVelocity(0, 0);
            }

        }


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
        //    bobSprite.setPosition(position.x, position.y);
        //    bobSprite.setRegion(currentFrame); // set the bob sprite's texture to the current frame
        //    bobSprite.draw(batch);

        float width = visual.region.getRegionWidth();
        float height = visual.region.getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        //Allow for Offset
        float originX = /*halfWidth +*/ transform.originOffset.x;
        float originY = /*halfHeight +*/ transform.originOffset.y;

        batch.draw(visual.region,
                transform.position.x /*- halfWidth*/, transform.position.y /*- halfHeight*/,
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

        mVertices[0] = 0;
        mVertices[1] = 0;
        mVertices[2] = mBounds.getWidth();
        mVertices[3] = 0;
        mVertices[4] = mBounds.getWidth();
        mVertices[5] = mBounds.getHeight();
        mVertices[6] = 0;
        mVertices[7] = mBounds.getHeight();
        mPolygonBound.setVertices(mVertices);
        mPolygonBound.setPosition(mBounds.getX(), mBounds.getY());

        return mPolygonBound;
    }

    public void initialize(float width, float height, Texture walkSheet) {
        // instantiate bob sprite

        this.walkSheet = walkSheet; // save the sprite sheet
//split the sprite sheet into different textures
        TextureRegion[][] tmp = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / 3, walkSheet.getHeight() / 4);
// convert 2D array to 1D
        Array<TextureRegion> walkFrames = new Array();
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < tmp[0].length; j++) {
                walkFrames.add(tmp[i][j]);
            }
        }
// create a new animation sequence with the walk frames and time periodof specified seconds
        walkAnimation = new Animation(ANIMATION_TIME_PERIOD, walkFrames);
        // set the animation to loop
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
// get initial frame
        currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        TransformComponent transform = this.getComponent(TransformComponent.class);
        transform.scale = MyGame.SCALE_FACTOR * BOB_RESIZE_FACTOR;

        this.add(new VisualComponent(currentFrame));

        setPosition(0, 0);

        this.add(new CollisionComponent(getPolygonBound(), this));


    }

    @Override
    public void onCollisionStart(CollisionComponent aEntity) {
        if (!mCollisions.contains(aEntity, false)) {
            mCollisions.add(aEntity);
        }
    }

    @Override
    public void onCollisionStop(CollisionComponent aEntity) {
        if (mCollisions.contains(aEntity, false)) {
            mCollisions.removeValue(aEntity, false);
        }
    }

    @Override
    public Array<CollisionComponent> getCollisions() {
        return mCollisions;
    }
}
