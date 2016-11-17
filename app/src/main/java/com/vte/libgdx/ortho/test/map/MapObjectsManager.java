package com.vte.libgdx.ortho.test.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vte.libgdx.ortho.test.MyGame;
import com.vte.libgdx.ortho.test.box2d.MapBodyManager;
import com.vte.libgdx.ortho.test.entity.EntityEngine;
import com.vte.libgdx.ortho.test.entity.components.CollisionComponent;

/**
 * Created by gwalarn on 13/11/16.
 */

public class MapObjectsManager {
    static private MapObjectsManager s_instance;

    public static MapObjectsManager getInstance() {
        return s_instance;
    }

    public static synchronized MapObjectsManager createInstance(Map map) {
        s_instance = new MapObjectsManager(map);
        return s_instance;
    }

    private Array<MapControl> mControls = new Array<MapControl>();



    public MapObjectsManager(Map map) {
        mControls = buildControls(map, "controls");


    }

    public Array<MapControl> getControls() {
        return mControls;
    }


    public Array<MapControl> buildControls(Map map, String layerName) {
        MapObjects objects = map.getLayers().get(layerName).getObjects();

        Array<MapControl> controls = new Array<MapControl>();

        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                TextureMapObject textureObject = (TextureMapObject) object;
                if(textureObject.getName().compareTo(MapControl.Type.START.name())==0)
                {
                    float x = (textureObject.getX()+textureObject.getTextureRegion().getRegionWidth()/2)*MyGame.SCALE_FACTOR;
                    float y = (textureObject.getY()+textureObject.getTextureRegion().getRegionHeight()/2)*MyGame.SCALE_FACTOR;
                    MapControl control = new MapControl(x, y, MapControl.Type.START);
                    controls.add(control);
                }

            }

        }
        return controls;
    }
}
