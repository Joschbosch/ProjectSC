/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.core.entities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.utils.BoundingBox;
import de.projectsc.core.utils.BoundingBoxLoader;
import de.projectsc.core.utils.PhysicalObject;

/**
 * An entity in the world.
 * 
 * @author Josch Bosch
 */
public abstract class WorldEntity implements Comparable<WorldEntity>, PhysicalObject {

    // private static final float TURN_SPEED = 160f;
    //
    // private static final float GRAVITY = -50;

    // private static final float JUMP_POWER = 30;

    private static final Log LOGGER = LogFactory.getLog(WorldEntity.class);

    private static int idCount = 0;

    protected EntityType type;

    protected Vector3f position;

    protected final Vector3f rotation;

    protected float scale;

    protected final int id;

    protected String model;

    protected String texture;

    protected BoundingBox boundingBox;

    public WorldEntity(EntityType type, String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.id = idCount++;
        this.type = type;
        this.setModel(model);
        this.setTexture(texture);
        boundingBox = BoundingBoxLoader.readBoundingBox(this);
        LOGGER.debug("Created new Entity : " + this);
    }

    public WorldEntity(int id, EntityType type, String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        this.id = id;
        if (idCount < id) {
            idCount = id + 1;
        }
        this.position = position;
        this.rotation = rotation;
        this.type = type;
        this.scale = scale;
        this.setModel(model);
        this.setTexture(texture);
        if (type != EntityType.DECORATION) {
            boundingBox = BoundingBoxLoader.readBoundingBox(this);
        }
        LOGGER.debug("Created new Entity : " + this);

    }

    public Vector3f getLocationBoundingBoxMinimum() {
        return Vector3f.add(boundingBox.getMin(), position, null);
    }

    public Vector3f getLocationBoundingBoxMaximum() {
        return Vector3f.add(boundingBox.getMax(), position, null);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotation.x;
    }

    public void setRotX(float rotX) {
        this.rotation.x = rotX;
    }

    public float getRotY() {
        return rotation.y;
    }

    public void setRotY(float rotY) {
        this.rotation.y = rotY;
    }

    public float getRotZ() {
        return rotation.z;
    }

    public void setRotZ(float rotZ) {
        this.rotation.z = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public Integer getID() {
        return id;
    }

    public EntityType getType() {
        return type;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public String toString() {
        String result = "";
        result += getModel() + ", ";
        result += getTexture() + ", ";
        result += getID() + ", ";
        result += getPosition() + ", ";
        result += getRotX() + ", ";
        result += getRotY() + ", ";
        result += getRotZ() + ", ";
        result += getScale() + ", ";
        result += getType() + "";
        return result;
    }

    @Override
    public int compareTo(WorldEntity arg0) {
        return getID().compareTo(arg0.getID());
    }
}
