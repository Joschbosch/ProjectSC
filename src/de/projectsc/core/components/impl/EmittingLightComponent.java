/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.components.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.Light;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.entities.Entity;

public class EmittingLightComponent extends Component {

    List<Light> lights = new LinkedList<>();

    Map<Light, Vector3f> offsets = new HashMap<>();

    public EmittingLightComponent() {
        super("Emitting light component");
        type = ComponentType.GRAPHICS;
    }

    @Override
    public void update(Entity owner) {
        for (Light l : lights) {
            l.setPosition(Vector3f.add(owner.getPosition(), offsets.get(l), null));
        }
    }

    public Light getLight(String name) {
        for (Light l : lights) {
            if (l.getName().equals(name)) {
                return l;
            }
        }
        return null;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void addLight(Entity e, Light light) {
        offsets.put(light, light.getPosition());
        light.setPosition(Vector3f.add(e.getPosition(), light.getPosition(), null));
        lights.add(light);
    }

    public void createAndAddLight(Entity e, Vector3f color, String name) {
        createAndAddLight(e, new Vector3f(0, 0, 0), color, new Vector3f(1.0f, 0, 0), name);
    }

    public void createAndAddLight(Entity e, Vector3f offset, Vector3f color, String name) {
        createAndAddLight(e, offset, color, new Vector3f(1.0f, 0, 0), name);
    }

    public void createAndAddLight(Entity e, Vector3f offset, Vector3f color, Vector3f attenuation, String name) {
        Light l = new Light(Vector3f.add(e.getPosition(), offset, null), color, attenuation, name);
        lights.add(l);
        offsets.put(l, offset);
    }
}
