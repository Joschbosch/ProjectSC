/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.components.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.Light;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.utils.Serialization;

/**
 * Entity component to allow entities having lights.
 * 
 * @author Josch Bosch
 */
public class EmittingLightComponent extends Component {

    /**
     * Name.
     */
    public static final String NAME = "Emitting light component";

    private final List<Light> lights = new LinkedList<>();

    private final Map<Light, Vector3f> offsets = new HashMap<>();

    public EmittingLightComponent() {
        super(NAME);
        type = ComponentType.GRAPHICS;
    }

    @Override
    public void update(Entity owner) {
        for (Light l : lights) {
            l.setPosition(Vector3f.add(owner.getPosition(), offsets.get(l), null));
        }
    }

    /**
     * Return light with the given name.
     * 
     * @param name of the light
     * @return light instance
     */
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

    /**
     * Add light to entity. Note that the lights postion will be the offset of the entities
     * position.
     * 
     * @param e owner entity
     * @param light to add
     */
    public void addLight(Entity e, Light light) {
        offsets.put(light, light.getPosition());
        light.setPosition(Vector3f.add(e.getPosition(), light.getPosition(), null));
        lights.add(light);
    }

    /**
     * @param e owner
     * @param color of the light
     * @param name of the light
     */
    public void createAndAddLight(Entity e, Vector3f color, String name) {
        createAndAddLight(e, new Vector3f(0, 0, 0), color, new Vector3f(1.0f, 0, 0), name);
    }

    /**
     * @param e owner
     * @param color of the light
     * @param offset position.
     * @param name of the light
     */
    public void createAndAddLight(Entity e, Vector3f offset, Vector3f color, String name) {
        createAndAddLight(e, offset, color, new Vector3f(1.0f, 0, 0), name);
    }

    /**
     * @param e owner
     * @param color of the light
     * @param offset position.
     * @param attenuation for the light
     * @param name of the light
     */
    public void createAndAddLight(Entity e, Vector3f offset, Vector3f color, Vector3f attenuation, String name) {
        Light l = new Light(Vector3f.add(e.getPosition(), offset, null), color, attenuation, name);
        lights.add(l);
        offsets.put(l, offset);
    }

    /**
     * @param l light to remove
     */
    public void removeLight(Light l) {
        lights.remove(l);
        offsets.remove(l);
    }

    @Override
    public String serialize() throws JsonGenerationException, JsonMappingException, IOException {
        Map<String, Map<String, Float[]>> serializedLights = Serialization.createSerializableMap(lights);
        for (Light l : offsets.keySet()) {
            Map<String, Float[]> values = serializedLights.get(l.getName());
            values.put("offset", new Float[] { offsets.get(l).x, offsets.get(l).y, offsets.get(l).z });
        }
        return mapper.writeValueAsString(serializedLights);
    }

    @Override
    public void deserialize(JsonNode input) throws JsonProcessingException, IOException {
        @SuppressWarnings("unchecked") Map<String, Map<String, List<Double>>> deserializedLights =
            mapper.readValue(input.getTextValue(), new HashMap<String, Map<String, List<Double>>>().getClass());
        for (String lightName : deserializedLights.keySet()) {
            Vector3f position = readVector(deserializedLights.get(lightName), "position");
            Vector3f color = readVector(deserializedLights.get(lightName), "color");
            Vector3f attenuation = readVector(deserializedLights.get(lightName), "attenuation");
            Light l = new Light(position, color, attenuation, lightName);
            lights.add(l);
            offsets.put(l, readVector(deserializedLights.get(lightName), "offset"));
        }
    }

    private Vector3f readVector(Map<String, List<Double>> map, String name) {
        List<Double> list = map.get(name);
        double v1 = list.get(0);
        double v2 = list.get(1);
        double v3 = list.get(2);
        return new Vector3f((float) v1, (float) v2, (float) v3);
    }

}
