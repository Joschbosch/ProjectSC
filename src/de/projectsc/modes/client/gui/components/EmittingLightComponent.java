/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.components;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.data.objects.Light;
import de.projectsc.core.interfaces.Component;

/**
 * Entity component to allow entities having lights.
 * 
 * @author Josch Bosch
 */
public class EmittingLightComponent extends DefaultComponent {

    /**
     * Name.
     */
    public static final String NAME = "Emitting light component";

    private final List<Light> lights = new LinkedList<>();

    public EmittingLightComponent() {
        setComponentName(NAME);
        setType(ComponentType.GRAPHICS);
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
    public void addLight(String entity, Vector3f position, Light light) {
        light.setPosition(Vector3f.add(new Vector3f(position), new Vector3f(light.getPosition()), null));
        lights.add(light);
    }

    /**
     * @param entity owner
     * @param position of the light
     * @param color of the light
     * @param name of the light
     */
    public void createAndAddLight(String entity, Vector3f position, Vector3f color, String name) {
        createAndAddLight(entity, new Vector3f(position), new Vector3f(0, 0, 0), color, new Vector3f(1.0f, 0, 0), name);
    }

    /**
     * @param entity owner
     * @param position of the light
     * @param color of the light
     * @param offset position.
     * @param name of the light
     */
    public void createAndAddLight(String entity, Vector3f position, Vector3f offset, Vector3f color, String name) {
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
    public void createAndAddLight(String entity, Vector3f position, Vector3f offset, Vector3f color, Vector3f attenuation, String name) {
        Light l = new Light(entity, Vector3f.add(new Vector3f(position), offset, null), color, attenuation, name);
        lights.add(l);
    }

    /**
     * @param l light to remove
     */
    public void removeLight(Light l) {
        lights.remove(l);
    }

    private Vector3f readVector(Map<String, List<Double>> map, String name) {
        List<Double> list = map.get(name);
        double v1 = list.get(0);
        double v2 = list.get(1);
        double v3 = list.get(2);
        return new Vector3f((float) v1, (float) v2, (float) v3);
    }

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }

    @Override
    public Component cloneComponent() {
        EmittingLightComponent elc = new EmittingLightComponent();
        for (Light l : lights) {
            System.out.println("Todo");
            l.getAttenuation();
        }
        return elc;
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(Map<String, Object> serialized, String loadingLocation) {
        for (String lightName : serialized.keySet()) {
            Vector3f position = readVector((Map<String, List<Double>>) serialized.get(lightName), "position");
            Vector3f color = readVector((Map<String, List<Double>>) serialized.get(lightName), "color");
            Vector3f attenuation = readVector((Map<String, List<Double>>) serialized.get(lightName), "attenuation");
            Light l = new Light(owner.getID(), position, color, attenuation, lightName);
            lights.add(l);
        }
    }

}
