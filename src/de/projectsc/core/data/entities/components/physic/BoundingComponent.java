/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data.entities.components.physic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.data.BoundingBox;
import de.projectsc.core.data.OBJFileLoader;
import de.projectsc.core.data.entities.Component;
import de.projectsc.core.data.entities.ComponentType;
import de.projectsc.core.data.entities.Entity;

/**
 * Bounding boxes for entites.
 * 
 * @author Josch Bosch
 */
public class BoundingComponent extends Component {

    /**
     * Name.
     */
    public static final String NAME = "Bounding component";

    private static final String SCALE = "scale";

    private static final String OFFSET_ROTATION = "offsetRotation";

    private static final String OFFSET = "offset";

    private BoundingBox box;

    private final Vector3f offset;

    private final Vector3f offsetRotation;

    private float scale;

    private File boxFile;

    public BoundingComponent(Entity owner) {
        super(NAME, owner);
        setType(ComponentType.PHYSICS);
        offset = new Vector3f(0f, 0f, 0f);
        offsetRotation = new Vector3f(0f, 0f, 0f);
        scale = 1.0f;
        box = null;
    }

    /**
     * Loads bounding box file and creates simplified bounding box.
     * 
     * @param owner of box
     * @param boxObjectFile to load
     */
    public void loadBoundingBox(Entity owner, File boxObjectFile) {
        this.setBoxFile(boxObjectFile);
        Vector3f[] minMax = findMinAndMax(OBJFileLoader.loadOBJ(boxObjectFile).getVertices());
        box = new BoundingBox(minMax[0], minMax[1]);
        box.setPosition(Vector3f.add(owner.getPosition(), offset, null));
        box.setRotation(Vector3f.add(owner.getRotation(), offsetRotation, null));
        box.setScale(scale);
        owner.setBoundingBox(box);
    }

    private Vector3f[] findMinAndMax(float[] vertices) {
        float[] min = new float[] { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };
        float[] max = new float[] { Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE };
        int i = 0;
        for (float f : vertices) {
            if (min[i] > f) {
                min[i] = f;
            }
            if (max[i] < f) {
                max[i] = f;
            }
            i = (i + 1) % min.length;
        }
        return new Vector3f[] { new Vector3f(min[0], min[1], min[2]), new Vector3f(max[0], max[1], max[2]) };
    }

    @Override
    public void update(Entity owner) {
        if (owner != null && box != null) {
            box.setPosition(Vector3f.add(owner.getPosition(), offset, null));
            box.setRotation(Vector3f.add(owner.getRotation(), offsetRotation, null));
            box.setScale(scale);
        }
    }

    @Override
    public String serialize() throws JsonGenerationException, JsonMappingException, IOException {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put(OFFSET, offset);
        serialized.put(OFFSET_ROTATION, offsetRotation);
        serialized.put(SCALE, scale);
        return mapper.writeValueAsString(serialized);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(JsonNode input, File schemaDir) throws JsonProcessingException, IOException {
        Map<String, Object> values = mapper.readValue(input.asText(), new HashMap<String, Object>().getClass());
        boxFile = new File(schemaDir, CoreConstants.BOX_FILENAME);
        loadBoundingBox(owner, boxFile);
        this.scale = (Float.parseFloat("" + values.get(SCALE)));
        Vector3f newOffset = new Vector3f(Float.parseFloat("" + ((Map<String, Double>) values.get(OFFSET)).get("x")),
            Float.parseFloat("" + ((Map<String, Double>) values.get(OFFSET)).get("y")),
            Float.parseFloat("" + ((Map<String, Double>) values.get(OFFSET)).get("z")));
        Vector3f newRotation = new Vector3f(Float.parseFloat("" + ((Map<String, Double>) values.get(OFFSET_ROTATION)).get("x")),
            Float.parseFloat("" + ((Map<String, Double>) values.get(OFFSET_ROTATION)).get("y")),
            Float.parseFloat("" + ((Map<String, Double>) values.get(OFFSET_ROTATION)).get("z")));
        this.setOffset(newOffset);
        this.setRotation(newRotation);
    }

    /**
     * Check if the current entity intersects with the picking ray.
     * 
     * @param org position of camera
     * @param ray to intersect
     * @return true if it intersects
     */
    public float intersects(Vector3f org, Vector3f ray) {
        Vector3f lb = Vector3f.add(box.getPosition(), box.getMin(), null);
        Vector3f rt = Vector3f.add(box.getPosition(), box.getMax(), null);
        // r.dir is unit direction vector of ray
        float dirfracx = 1.0f / ray.x;
        float dirfracy = 1.0f / ray.y;
        float dirfracz = 1.0f / ray.z;
        // lb is the corner of AABB with minimal coordinates - left bottom, rt is maximal corner
        // r.org is origin of ray
        float t1 = (lb.x - org.x) * dirfracx;
        float t2 = (rt.x - org.x) * dirfracx;
        float t3 = (lb.y - org.y) * dirfracy;
        float t4 = (rt.y - org.y) * dirfracy;
        float t5 = (lb.z - org.z) * dirfracz;
        float t6 = (rt.z - org.z) * dirfracz;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        float t;
        // if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behing us
        if (tmax < 0) {
            t = tmax;
            return -1;
        }

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax) {
            t = tmax;
            return -1;
        }

        t = tmin;
        return t;
    }

    @Override
    public void remove() {
        owner.setBoundingBox(null);
    }

    public Vector3f getPosition() {
        return box.getPosition();
    }

    public float getScale() {
        return scale;
    }

    /**
     * Sets new Scale.
     * 
     * @param scale to set.
     */
    public void setScale(float scale) {
        this.scale = scale;
        box.setScale(scale);
    }

    /**
     * 
     * @param position to set for the box
     */
    public void setPosition(Vector3f position) {
        this.box.setPosition(position);

    }

    /**
     * @param rotation to set for the box
     */
    public void setRotation(Vector3f rotation) {
        box.setRotation(rotation);

    }

    /**
     * Set offset of component rotation.
     * 
     * @param rotation to set.
     */
    public void setOffsetRotation(Vector3f rotation) {
        this.offsetRotation.x = rotation.x;
        this.offsetRotation.y = rotation.y;
        this.offsetRotation.z = rotation.z;
    }

    public Vector3f getOffsetRotation() {
        return offsetRotation;
    }

    /**
     * Set offset of component position.
     * 
     * @param position to set.
     */
    public void setOffset(Vector3f position) {
        this.offset.x = position.x;
        this.offset.y = position.y;
        this.offset.z = position.z;
    }

    public Vector3f getOffset() {
        return offset;
    }

    public BoundingBox getBox() {
        return box;
    }

    public File getBoxFile() {
        return boxFile;
    }

    public void setBoxFile(File boxFile) {
        this.boxFile = boxFile;
    }

    @Override
    public boolean isValidForSaving() {
        return box != null && boxFile != null && boxFile.exists();
    }
}
