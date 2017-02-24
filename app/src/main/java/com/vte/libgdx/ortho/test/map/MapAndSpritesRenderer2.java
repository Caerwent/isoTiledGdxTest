package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.box2d.PolygonShape;
import com.vte.libgdx.ortho.test.box2d.RectangleShape;
import com.vte.libgdx.ortho.test.box2d.Shape;
import com.vte.libgdx.ortho.test.box2d.ShapeUtils;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.components.VisualComponent;

import java.util.Arrays;
import java.util.Comparator;

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

    TiledMapTileLayer mDefaultlayer;
    //initiate shapeRenderer. Can remove later
    ShapeRenderer shapeRenderer = new ShapeRenderer();

    ShapeRenderer collisionRenderer = new ShapeRenderer();
    GameMap mMap;
    private ComponentMapper<VisualComponent> vm = ComponentMapper.getFor(VisualComponent.class);

    private ImmutableArray<Entity> entities;


    public MapAndSpritesRenderer2(GameMap map) {
        super(map.getTiledMap());
        mMap = map;
        init();
    }

    public MapAndSpritesRenderer2(GameMap map, Batch batch) {
        super(map.getTiledMap(), batch);
        mMap = map;
        init();
    }

    public MapAndSpritesRenderer2(GameMap map, float unitScale) {
        super(map.getTiledMap(), unitScale);
        mMap = map;
        init();
    }

    public MapAndSpritesRenderer2(GameMap map, float unitScale, Batch batch) {
        super(map.getTiledMap(), unitScale, batch);
        mMap = map;
        init();
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
        collisionRenderer.setColor(Color.BLUE);
        // shapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void render() {

        entities = EntityEngine.getInstance().getEntitiesFor(Family.all(VisualComponent.class).get());
        for (int i = 0; i < entities.size(); ++i) {
            Entity e = entities.get(i);

            vm.get(e).rendable.setRended(false);
        }
        Entity[] sortedEntities = entities.toArray(Entity.class);
        Arrays.sort(sortedEntities, new Comparator<Entity>() {
            @Override
            public int compare(Entity lhs, Entity rhs) {
                IMapRendable lhsT = vm.get(lhs).rendable;
                IMapRendable rhsT = vm.get(rhs).rendable;
                if (lhsT.getShape().getY() == rhsT.getShape().getY()) {
                    return 0;
                } else {
                    return lhsT.getShape().getBounds().getY() < rhsT.getShape().getBounds().getY() ? 1 : -1;
                }
            }
        });


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
    //     Gdx.app.debug("DEBUG", "************************************************************");
        Array<Shape> zindexList = mMap.getBodiesZindex();

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
                                RectangleShape tileShape = new RectangleShape();
                                tileShape.setShape(new Rectangle(x1, y1, layerTileWidth, layerTileHeight));
                            //     Gdx.app.debug("DEBUG", "------------------------- tile "+ShapeUtils.logShape(tileShape));

                                for (int i = 0; i < zindexList.size; i++) {
                                    Shape currentZIndex = zindexList.get(i);
                                //     Gdx.app.debug("DEBUG", "check zindex "+ShapeUtils.logShape(currentZIndex));


                                    if (ShapeUtils.overlaps(currentZIndex, tileShape)) {
                                     //   Gdx.app.debug("DEBUG", "zindex overlaps tile");

                                        for (int idx = 0; idx< sortedEntities.length; idx++) {
                                            Entity entity = sortedEntities[idx];
                                            IMapRendable rendable = vm.get(entity).rendable;
                                            if (rendable.isRendable() && !rendable.isRended()) {
                                            //     Gdx.app.debug("DEBUG", "check entity "+entity.getClass().getSimpleName()+" "+ShapeUtils.logShape(rendable.getShape()));
                                                if (ShapeUtils.overlaps(rendable.getShape(), currentZIndex)) {
                                                //     Gdx.app.debug("DEBUG", "entity overlaps tile");
                                                    if (currentZIndex.getBounds().getY() < rendable.getShape().getBounds().getY()) {
                                                    //     Gdx.app.debug("DEBUG", "render entity "+ entity.getClass().getSimpleName());
                                                        // need to draw all overlaping entities with < Y
                                                        drawPreviousOverlapingEntity(rendable, idx, sortedEntities);
                                                        rendable.render(getBatch());
                                                        rendable.setRended(true);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                                batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);

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

        renderRemainingObjects(sortedEntities);

        endRender();

       // renderShapes(sortedEntities);
       // renderCollisionShapes();

    }

    private void drawPreviousOverlapingEntity(IMapRendable rendable, int idx, Entity[] sortedEntities)
    {
        if(idx<=0)
            return;

            Entity entity = sortedEntities[idx];
            IMapRendable prevRendable = vm.get(sortedEntities[idx-1]).rendable;
            if (prevRendable.isRendable() && !prevRendable.isRended()) {
                //     Gdx.app.debug("DEBUG", "check entity "+entity.getClass().getSimpleName()+" "+ShapeUtils.logShape(rendable.getShape()));
                if (ShapeUtils.overlaps(rendable.getShape(), prevRendable.getShape())) {
                    //     Gdx.app.debug("DEBUG", "entity overlaps tile");
                    if (rendable.getShape().getBounds().getY() < prevRendable.getShape().getBounds().getY()) {
                        drawPreviousOverlapingEntity(prevRendable, idx-1, sortedEntities);
                        prevRendable.render(getBatch());
                        prevRendable.setRended(true);
                    }
                }
            }

    }
    private void renderRemainingObjects(Entity[] sortedEntities) {

        for (Entity entity : sortedEntities) {
            IMapRendable rendable = vm.get(entity).rendable;
            if (rendable.isRendable() && !rendable.isRended()) {
                //    Gdx.app.debug("DEBUG", "render "+rendable.getClass().getSimpleName()+" y="+rendable.getShape().getBounds().getY());


                rendable.render(batch);
                rendable.setRended(true);
            }
        }

    }

    @Override
    public void setView(OrthographicCamera camera) {
        super.setView(camera);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    private void renderShapes(Entity[] sortedEntities) {
        Array<Shape> bodies = mMap.getBodiesZindex();
        shapeRenderer.setProjectionMatrix(getBatch().getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Shape body : bodies) {
            if (body.getType() == Shape.Type.POLYGON) {
                shapeRenderer.polygon(((PolygonShape) body).getShape().getTransformedVertices());
            }
        }

        for (Entity entity : sortedEntities) {
            IMapRendable rendable = vm.get(entity).rendable;
            if (rendable.isRendable() && rendable.isRended()) {
                if (rendable.getShape() instanceof PolygonShape) {
                    shapeRenderer.polygon(((PolygonShape) rendable.getShape()).getShape().getTransformedVertices());
                } else if (rendable.getShape() instanceof RectangleShape) {
                    Rectangle rect = ((RectangleShape) rendable.getShape()).getShape();
                    shapeRenderer.rect(rect.getX(), rect.getY(), 0, 0, rect.getWidth(), rect.getHeight(), 1, 1, 0);
                }
            }
        }
        shapeRenderer.end();
    }

    private void renderCollisionShapes() {
        Array<Shape> bodies = mMap.getBodiesCollision();
        collisionRenderer.setProjectionMatrix(getBatch().getProjectionMatrix());
        collisionRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Shape body : bodies) {
            if (body.getType() == Shape.Type.POLYGON) {
                collisionRenderer.polygon(((PolygonShape) body).getShape().getTransformedVertices());
            }
        }


        collisionRenderer.end();
    }

}
