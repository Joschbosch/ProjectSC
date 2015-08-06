/*
 * Copyright (C) 2015
 */

package de.projectsc.core.components.impl;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.ModelData;
import de.projectsc.client.gui.tools.NewOBJFileLoader;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.utils.BoundingBox;

public class BoundingComponent extends Component {

    /**
     * Name.
     */
    public static final String NAME = "Bounding component";

    private BoundingBox box;

    private Vector3f position;

    private Vector3f offset;

    private float scale;

    private File boxFile;

    public BoundingComponent() {
        super(NAME);
        setType(ComponentType.PHYSICS);
        offset = new Vector3f(0f, 0f, 0f);
        scale = 1.0f;
        box = null;
        position = null;
    }

    public void loadBoundingBox(Entity owner, Loader loader, File boxObjectFile) {
        this.setBoxFile(boxObjectFile);
        System.out.println("load " + boxObjectFile);
        ModelData data = NewOBJFileLoader.loadOBJ(boxObjectFile);
        RawModel model = loader.loadToVAO(data.getVertices(), data.getIndices());
        Vector3f[] minMax = findMinAndMax(data.getVertices());
        box = new BoundingBox(minMax[0], minMax[1]);
        box.setModel(model);
        position = Vector3f.add(owner.getPosition(), offset, null);
        box.setPosition(position);
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
            position = Vector3f.add(owner.getPosition(), offset, null);
            box.setPosition(position);
            scale = 1.0f;
            box.setScale(scale);
        }
    }

    @Override
    public String serialize() throws JsonGenerationException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public void deserialize(JsonNode input) throws JsonProcessingException, IOException {

    }

    public Vector3f getPosition() {
        return position;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setPosition(Vector3f position) {
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;

    }

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