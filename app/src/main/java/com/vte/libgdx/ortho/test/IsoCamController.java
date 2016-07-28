package com.vte.libgdx.ortho.test;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by vincent on 28/06/2016.
 */

public class IsoCamController extends InputAdapter {
    final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
    final Vector3 intersection = new Vector3();
    final Vector3 curr = new Vector3();
    final Vector3 last = new Vector3(-1, -1, -1);
    final Vector3 delta = new Vector3();
    final Camera camera;

    public IsoCamController(Camera camera) {
        this.camera = camera;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
       /* Ray pickRay = camera.getPickRay(x, y);
        Intersector.intersectRayPlane(pickRay, xzPlane, curr);
        if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
            pickRay = camera.getPickRay(last.x, last.y);
            Intersector.intersectRayPlane(pickRay, xzPlane, delta);
            delta.sub(curr);
            camera.position.add(delta.x, delta.y, 0);
        }
        last.set(x, y, 0);*/
        camera.unproject(curr.set(x, y, 0));

        if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
            camera.unproject(delta.set(last.x, last.y, 0));
            delta.sub(curr);
            camera.position.add(delta.x, delta.y, 0);

        }
        last.set(x, y, 0);
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        last.set(-1, -1, -1);
        return false;
    }
}
