/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.gui.components;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.objects.Light;
import de.projectsc.core.data.utils.LightSerializationUtils;
import de.projectsc.core.entities.ComponentType;
import de.projectsc.core.modes.client.gui.data.Scene;
import de.projectsc.core.modes.client.gui.data.WireFrame;

/**
 * Entity component to allow entities having lights.
 * 
 * @author Josch Bosch
 */
public class EmittingLightComponent extends GraphicalComponent {

    /**
     * Name.
     */
    public static final String NAME = "Emitting light component";

    private final List<Light> lights = new LinkedList<>();

    private final Map<Light, Vector3f> offsets = new HashMap<>();

    public EmittingLightComponent() {
        setID(NAME);
        setType(ComponentType.GRAPHICS);
    }

    /**
     * update position of light relative to entity.
     * 
     * @param owner to set to
     * @param position of owner
     */
    public void updateLightPositionToEntity(long owner, Vector3f position) {
        for (Light l : lights) {
            Vector3f currentPosition = new Vector3f(position);
            currentPosition.x += offsets.get(l).getX();
            currentPosition.y += offsets.get(l).getY();
            currentPosition.z += offsets.get(l).getZ();
            l.setPosition(currentPosition);
        }
    }

    @Override
    public void render(long entity, Scene scene) {
        scene.getLights().addAll(getLights());
    }

    @Override
    public void addDebugMode(Scene scene) {
        for (Light l : lights) {
            WireFrame w = new WireFrame(WireFrame.SPHERE, l.getPosition(), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
            scene.getWireFrames().add(w);
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
     * Add light to entity. Note that the lights postion will be the offset of the entities position.
     * 
     * @param entity owner entity
     * @param position of the light
     * @param light to add
     */
    public void addLight(long entity, Vector3f position, Light light) {
        offsets.put(light, light.getPosition());
        light.setPosition(Vector3f.add(new Vector3f(position), new Vector3f(light.getPosition()), null));
        lights.add(light);
    }

    /**
     * @param entity owner
     * @param position of the light
     * @param color of the light
     * @param name of the light
     */
    public void createAndAddLight(long entity, Vector3f position, Vector3f color, String name) {
        createAndAddLight(entity, new Vector3f(position), new Vector3f(0, 0, 0), color, new Vector3f(1.0f, 0, 0), name);
    }

    /**
     * @param entity owner
     * @param position of the light
     * @param color of the light
     * @param offset position.
     * @param name of the light
     */
    public void createAndAddLight(long entity, Vector3f position, Vector3f offset, Vector3f color, String name) {
        createAndAddLight(entity, new Vector3f(position), offset, color, new Vector3f(1.0f, 0, 0), name);
    }

    /**
     * @param entity owner
     * @param position of the light
     * @param color of the light
     * @param offset position.
     * @param attenuation for the light
     * @param name of the light
     */
    public void createAndAddLight(long entity, Vector3f position, Vector3f offset, Vector3f color, Vector3f attenuation, String name) {
        Light l = new Light(Vector3f.add(new Vector3f(position), offset, null), color, attenuation, name);
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

    private Vector3f readVector(Map<String, List<Double>> map, String name) {
        List<Double> list = map.get(name);
        double v1 = list.get(0);
        double v2 = list.get(1);
        double v3 = list.get(2);
        return new Vector3f((float) v1, (float) v2, (float) v3);
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    @Override
    public void update(long ownerEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> serialize(File savingLocation) {
        Map<String, Object> serializedLights = LightSerializationUtils.createSerializableMap(lights);
        for (Light l : offsets.keySet()) {
            Map<String, Float[]> values = (Map<String, Float[]>) serializedLights.get(l.getName());
            if (values != null) {
                values.put("offset", new Float[] { offsets.get(l).x, offsets.get(l).y, offsets.get(l).z });
            }
        }
        return serializedLights;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {
        for (String lightName : serialized.keySet()) {
            Vector3f position = readVector((Map<String, List<Double>>) serialized.get(lightName), "position");
            Vector3f color = readVector((Map<String, List<Double>>) serialized.get(lightName), "color");
            Vector3f attenuation = readVector((Map<String, List<Double>>) serialized.get(lightName), "attenuation");
            Light l = new Light(position, color, attenuation, lightName);
            lights.add(l);
            offsets.put(l, readVector((Map<String, List<Double>>) serialized.get(lightName), "offset"));
        }
    }

}
