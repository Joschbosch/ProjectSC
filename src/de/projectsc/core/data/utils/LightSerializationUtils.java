/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.objects.Light;

/**
 * Utils for Serialization.
 * 
 * @author Josch Bosch
 */
public final class LightSerializationUtils {

    private static final String ATTENUATION = "attenuation";

    private static final String COLOR = "color";

    private static final String POSITION = "position";

    private LightSerializationUtils() {};

    /**
     * @param lightsToSerialize list
     * @return map ready to serialize
     */
    public static Map<String, Map<String, Float[]>> createSerializableMap(List<Light> lightsToSerialize) {

        Map<String, Map<String, Float[]>> lights = new HashMap<>();
        for (Light l : lightsToSerialize) {
            Map<String, Float[]> light = new HashMap<>();
            light.put(ATTENUATION, new Float[] { l.getAttenuation().x, l.getAttenuation().y, l.getAttenuation().z });
            light.put(COLOR, new Float[] { l.getColor().x, l.getColor().y, l.getColor().z });
            light.put(POSITION, new Float[] { l.getPosition().x, l.getPosition().y, l.getPosition().z });
            lights.put(l.getName(), light);
        }
        return lights;
    }

    /**
     * @param mapper to read json
     * @param lightsNode of tree
     * @return list of all lights
     * @throws IOException e
     * @throws JsonParseException e
     * @throws JsonMappingException e
     */
    public static List<Light> deserializeLights(ObjectMapper mapper, ObjectNode lightsNode)
        throws IOException, JsonParseException, JsonMappingException {
        Iterator<String> lightsIterator = lightsNode.getFieldNames();
        List<Light> staticLights = new ArrayList<Light>();
        while (lightsIterator.hasNext()) {
            String light = lightsIterator.next();
            Vector3f[] attributes = new Vector3f[3];
            int i = 0;
            for (String attributeName : new String[] { POSITION, COLOR, ATTENUATION }) {
                Float[] attribute = mapper.readValue(lightsNode.get(light).get(attributeName), new Float[3].getClass());
                attributes[i++] = new Vector3f(attribute[0], attribute[1], attribute[2]);
            }
            Light l = new Light(attributes[0], attributes[1], attributes[2], light);
            staticLights.add(l);
        }
        return staticLights;
    }

    /**
     * @param mapper to read json
     * @param node of vector
     * @return read vector
     * @throws JsonParseException e
     * @throws JsonMappingException e
     * @throws IOException e
     */
    public static Vector3f readVector(ObjectMapper mapper, JsonNode node) throws JsonParseException, JsonMappingException, IOException {
        Float[] values = mapper.readValue(node, new Float[3].getClass());
        Vector3f vector = new Vector3f(values[0], values[1], values[2]);
        return vector;

    }

}
