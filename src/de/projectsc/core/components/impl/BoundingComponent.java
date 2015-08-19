/*
 * Copyright (C) 2015
 */

package de.projectsc.core.components.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Billboard;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.objects.ParticleEmitter;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.ModelData;
import de.projectsc.client.gui.tools.OBJFileLoader;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.utils.BoundingBox;

/**
 * Component for having bounding boxes on an entity.
 * 
 * @author Josch Bosch
 */
public class BoundingComponent extends Component {

    /**
     * Name.
     */
    public static final String NAME = "Bounding component";

    private BoundingBox box;

    private final Vector3f offset;

    private final Vector3f offsetRotation;

    private float scale;

    private File boxFile;

    public BoundingComponent() {
        super(NAME);
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
        ModelData data = OBJFileLoader.loadOBJ(boxObjectFile);
        RawModel model = Loader.loadToVAO(data.getVertices(), data.getIndices());
        Vector3f[] minMax = findMinAndMax(data.getVertices());
        box = new BoundingBox(minMax[0], minMax[1]);
        box.setModel(model);
        box.setPosition(Vector3f.add(owner.getPosition(), offset, null));
        box.setRotation(Vector3f.add(owner.getRotation(), offsetRotation, null));
        box.setScale(scale);
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
    public void render(Entity owner, Map<TexturedModel, List<Entity>> entities, Map<RawModel, List<BoundingBox>> boundingBoxes,
        List<Light> lights,
        List<Billboard> billboards, List<ParticleEmitter> particles, Camera camera, long elapsedTime) {
        if (getBox() != null) {
            RawModel model = getBox().getModel();
            List<BoundingBox> batch = boundingBoxes.get(model);
            if (batch != null) {
                batch.add(getBox());
            } else {
                List<BoundingBox> newBatch = new ArrayList<>();
                newBatch.add(getBox());
                boundingBoxes.put(model, newBatch);
            }
        }
    }

    @Override
    public String serialize() throws JsonGenerationException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public void deserialize(JsonNode input) throws JsonProcessingException, IOException {

    }

    /**
     * Check if the current entity intersects with the picking ray.
     * 
     * @param orig position of camera
     * @param ray to intersect
     * @return true if it intersects
     */
    public boolean intersects(Vector3f orig, Vector3f ray) {
        Vector3f bMin = Vector3f.add(box.getPosition(), box.getMin(), null);
        Vector3f bMax = Vector3f.add(box.getPosition(), box.getMax(), null);
        float tminX = bMin.x - orig.x / ray.x;
        float tminY = bMin.y - orig.y / ray.y;
        float tminZ = bMin.z - orig.z / ray.z;
        float tmaxX = bMax.x - orig.x / ray.x;
        float tmaxY = bMax.y - orig.y / ray.y;
        float tmaxZ = bMax.z - orig.z / ray.z;

        if (tminX > tmaxY) {
            float temp = tminX;
            tminX = tmaxX;
            tmaxX = temp;
        }
        if (tminY > tmaxY) {
            float temp = tminY;
            tminY = tmaxY;
            tmaxY = temp;
        }
        if (tminZ > tmaxY) {
            float temp = tminZ;
            tminZ = tmaxZ;
            tmaxZ = temp;
        }
        if ((tminX > tmaxY) || (tminY > tmaxX)) {
            return false;
        }
        if (tminY > tminX) {
            tminX = tminY;
        }
        if (tmaxY < tmaxX) {
            tmaxX = tmaxY;
        }

        if ((tminX > tmaxZ) || (tminZ > tmaxX)) {
            return false;
        }

        if (tminZ > tminX) {
            tminX = tminZ;
        }

        if (tmaxZ < tmaxX) {
            tmaxX = tmaxZ;
        }
        return true;
    }

    public Vector3f getPosition() {
        return box.getPosition();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
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
        this.box.setRotation(rotation);

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
