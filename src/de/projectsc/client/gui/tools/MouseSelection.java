/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.tools;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.components.impl.BoundingComponent;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.utils.BoundingBox;

public class MouseSelection {

    public void calculateSelection(List<Entity> entities, Vector3f worldRay, Vector3f cameraPosition) {
        List<Entity> collisions = new LinkedList<>();
        for (Entity e : entities) {
            if (e.hasComponent(BoundingComponent.class)) {
                BoundingComponent b = e.getComponent(BoundingComponent.class);
                BoundingBox box = b.getBox();
                Vector3f center = box.getCenterWithPosition();
                float scalar = calculateScalar(cameraPosition, worldRay, center);
                float distance = calculateDistance(cameraPosition, worldRay, center, scalar);

            }
        }

    }

    private float calculateDistance(Vector3f a, Vector3f m, Vector3f p, float r) {
        Vector3f scaledM = (Vector3f) m.scale(r);
        Vector3f f = Vector3f.add(a, scaledM, null);
        Vector3f fp = Vector3f.sub(p, f, null);
        return fp.length();
    }

    private float calculateScalar(Vector3f a, Vector3f m, Vector3f p) {
        float result = (Vector3f.dot(m, p) - Vector3f.dot(m, a)) / Vector3f.dot(m, m);
        return result;
    }
}
