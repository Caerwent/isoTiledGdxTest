package com.vte.libgdx.ortho.test;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.box2d.MapBodyManager;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.g2d.Batch.C1;
import static com.badlogic.gdx.graphics.g2d.Batch.C2;
import static com.badlogic.gdx.graphics.g2d.Batch.C3;
import static com.badlogic.gdx.graphics.g2d.Batch.C4;
import static com.badlogic.gdx.graphics.g2d.Batch.U1;
import static com.badlogic.gdx.graphics.g2d.Batch.U2;
import static com.badlogic.gdx.graphics.g2d.Batch.U3;
import static com.badlogic.gdx.graphics.g2d.Batch.U4;
import static com.badlogic.gdx.graphics.g2d.Batch.V1;
import static com.badlogic.gdx.graphics.g2d.Batch.V2;
import static com.badlogic.gdx.graphics.g2d.Batch.V3;
import static com.badlogic.gdx.graphics.g2d.Batch.V4;
import static com.badlogic.gdx.graphics.g2d.Batch.X1;
import static com.badlogic.gdx.graphics.g2d.Batch.X2;
import static com.badlogic.gdx.graphics.g2d.Batch.X3;
import static com.badlogic.gdx.graphics.g2d.Batch.X4;
import static com.badlogic.gdx.graphics.g2d.Batch.Y1;
import static com.badlogic.gdx.graphics.g2d.Batch.Y2;
import static com.badlogic.gdx.graphics.g2d.Batch.Y3;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;

/**
 * Created by vincent on 06/07/2016.
 */

public class MapAndSpritesRenderer2 extends OrthogonalTiledMapRenderer {
    private List<Bob> sprites = new ArrayList<Bob>();

    TiledMapTileLayer mDefaultlayer;
    //initiate shapeRenderer. Can remove later
    ShapeRenderer shapeRenderer = new ShapeRenderer();


    public MapAndSpritesRenderer2(TiledMap map) {
        super(map);
        init();
    }

    public MapAndSpritesRenderer2(TiledMap map, Batch batch) {
        super(map, batch);
        init();
    }

    public MapAndSpritesRenderer2(TiledMap map, float unitScale) {
        super(map, unitScale);
        init();
    }

    public MapAndSpritesRenderer2(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
        init();
    }

    public void addSprite(Bob sprite) {
        sprites.add(sprite);
    }

    private void init() {
        mDefaultlayer = null;
        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                mDefaultlayer = (TiledMapTileLayer) layer;
                break;
            }
        }
        shapeRenderer.setColor(Color.RED);
        // shapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void render() {
        for (Bob entity : sprites) {
            entity.rended = false;
        }
        beginRender();

        final Color batchColor = batch.getColor();
        final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * mDefaultlayer.getOpacity());

        final int layerWidth = mDefaultlayer.getWidth();
        final int layerHeight = mDefaultlayer.getHeight();

        final float layerTileWidth = mDefaultlayer.getTileWidth() * unitScale;
        final float layerTileHeight = mDefaultlayer.getTileHeight() * unitScale;

        final int col1 = Math.max(0, (int) (viewBounds.x / layerTileWidth));
        final int col2 = Math.min(layerWidth, (int) ((viewBounds.x + viewBounds.width + layerTileWidth) / layerTileWidth));

        final int row1 = Math.max(0, (int) (viewBounds.y / layerTileHeight));
        final int row2 = Math.min(layerHeight, (int) ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight));

        float y = row2 * layerTileHeight;
        float xStart = col1 * layerTileWidth;
        final float[] vertices = this.vertices;
        super.renderTileLayer(mDefaultlayer);
        for (int row = row2; row >= row1; row--) {
            float x = xStart;
            for (int col = col1; col < col2; col++) {
                for (MapLayer layer : map.getLayers()) {
                    if (layer.isVisible() && layer != mDefaultlayer) {
                        if (layer instanceof TiledMapTileLayer) {


                            final TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) layer).getCell(col, row);
                            if (cell == null) {
                                //x += layerTileWidth;
                                continue;
                            }
                            final TiledMapTile tile = cell.getTile();

                            if (tile != null) {
                                final boolean flipX = cell.getFlipHorizontally();
                                final boolean flipY = cell.getFlipVertically();
                                final int rotations = cell.getRotation();

                                TextureRegion region = tile.getTextureRegion();

                                float x1 = x + tile.getOffsetX() * unitScale;
                                float y1 = y + tile.getOffsetY() * unitScale;
                                float x2 = x1 + region.getRegionWidth() * unitScale;
                                float y2 = y1 + region.getRegionHeight() * unitScale;

                                float u1 = region.getU();
                                float v1 = region.getV2();
                                float u2 = region.getU2();
                                float v2 = region.getV();

                                vertices[X1] = x1;
                                vertices[Y1] = y1;
                                vertices[C1] = color;
                                vertices[U1] = u1;
                                vertices[V1] = v1;

                                vertices[X2] = x1;
                                vertices[Y2] = y2;
                                vertices[C2] = color;
                                vertices[U2] = u1;
                                vertices[V2] = v2;

                                vertices[X3] = x2;
                                vertices[Y3] = y2;
                                vertices[C3] = color;
                                vertices[U3] = u2;
                                vertices[V3] = v2;

                                vertices[X4] = x2;
                                vertices[Y4] = y1;
                                vertices[C4] = color;
                                vertices[U4] = u2;
                                vertices[V4] = v1;

                                if (flipX) {
                                    float temp = vertices[U1];
                                    vertices[U1] = vertices[U3];
                                    vertices[U3] = temp;
                                    temp = vertices[U2];
                                    vertices[U2] = vertices[U4];
                                    vertices[U4] = temp;
                                }
                                if (flipY) {
                                    float temp = vertices[V1];
                                    vertices[V1] = vertices[V3];
                                    vertices[V3] = temp;
                                    temp = vertices[V2];
                                    vertices[V2] = vertices[V4];
                                    vertices[V4] = temp;
                                }
                                if (rotations != 0) {
                                    switch (rotations) {
                                        case TiledMapTileLayer.Cell.ROTATE_90: {
                                            float tempV = vertices[V1];
                                            vertices[V1] = vertices[V2];
                                            vertices[V2] = vertices[V3];
                                            vertices[V3] = vertices[V4];
                                            vertices[V4] = tempV;

                                            float tempU = vertices[U1];
                                            vertices[U1] = vertices[U2];
                                            vertices[U2] = vertices[U3];
                                            vertices[U3] = vertices[U4];
                                            vertices[U4] = tempU;
                                            break;
                                        }
                                        case TiledMapTileLayer.Cell.ROTATE_180: {
                                            float tempU = vertices[U1];
                                            vertices[U1] = vertices[U3];
                                            vertices[U3] = tempU;
                                            tempU = vertices[U2];
                                            vertices[U2] = vertices[U4];
                                            vertices[U4] = tempU;
                                            float tempV = vertices[V1];
                                            vertices[V1] = vertices[V3];
                                            vertices[V3] = tempV;
                                            tempV = vertices[V2];
                                            vertices[V2] = vertices[V4];
                                            vertices[V4] = tempV;
                                            break;
                                        }
                                        case TiledMapTileLayer.Cell.ROTATE_270: {
                                            float tempV = vertices[V1];
                                            vertices[V1] = vertices[V4];
                                            vertices[V4] = vertices[V3];
                                            vertices[V3] = vertices[V2];
                                            vertices[V2] = tempV;

                                            float tempU = vertices[U1];
                                            vertices[U1] = vertices[U4];
                                            vertices[U4] = vertices[U3];
                                            vertices[U3] = vertices[U2];
                                            vertices[U2] = tempU;
                                            break;
                                        }
                                    }
                                }
                                boolean tilePainted = false;
                                for (Bob entity : sprites) {

                                    for (CollisionComponent collision : entity.getCollisions()) {
                                        if(collision.mType!=CollisionComponent.Type.ZINDEX)
                                            continue;
                                        Polygon polygon = new Polygon(new float[]{0, 0, layerTileWidth, 0, layerTileWidth, layerTileHeight, 0, layerTileHeight});
                                        polygon.setPosition(x1, y1);
                                        if (Intersector.overlapConvexPolygons(collision.mBound, polygon) && !entity.rended) {

                                            if (collision.mBound.getBoundingRectangle().y < entity.getPolygonBound().getBoundingRectangle().y) {
                                                entity.render(getBatch());
                                                entity.rended = true;

                                                batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
                                                tilePainted = true;

                                                break;
                                            }


                                        }

                                    }


                                }
                                if (!tilePainted) {
                                    batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
                                }

                            }
                        } else if (layer instanceof TiledMapImageLayer) {
                            renderImageLayer((TiledMapImageLayer) layer);
                        } else {
                            renderObjects(layer);
                        }
                    }
                }
                x += layerTileWidth;
            }
            y -= layerTileHeight;
        }

        renderRemainingObjects();

        endRender();

     //   renderShapes();

    }

    private void renderRemainingObjects() {
        for (Bob entity : sprites) {
            if (!entity.rended) {
                entity.render(getBatch());
            }
        }
    }

    @Override
    public void setView(OrthographicCamera camera) {
        super.setView(camera);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    private void renderShapes() {
        Array<Polygon> bodies = MapBodyManager.getInstance().getBodiesCollision();
        shapeRenderer.setProjectionMatrix(getBatch().getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Polygon body : bodies) {
            shapeRenderer.polygon(body.getTransformedVertices());
        }
        for (Bob entity : sprites) {
            shapeRenderer.polygon(entity.getPolygonBound().getTransformedVertices());
        }
        shapeRenderer.end();
    }
}
