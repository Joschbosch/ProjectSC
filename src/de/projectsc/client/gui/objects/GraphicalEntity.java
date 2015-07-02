/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui.objects;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.core.entities.WorldEntity;

/**
 * The instance of a model.
 * 
 * @author Josch Bosch
 */
public class GraphicalEntity {

    private TexturedModel model;

    private int textureIndex = 0;

    private WorldEntity entity;

    public GraphicalEntity(WorldEntity entity, TexturedModel model) {
        this.model = model;
        this.setEntity(entity);
    }

    public GraphicalEntity(WorldEntity entity, TexturedModel model, int textureIndex) {
        this.model = model;
        this.textureIndex = textureIndex;
        this.setEntity(entity);
    }

    /**
     * Returns the X offset for a texture map.
     * 
     * @return position
     */
    public float getTextureOffsetX() {
        int column = textureIndex % model.getTexture().getNumberOfRows();
        return ((float) column / (float) model.getTexture().getNumberOfRows());
    }

    /**
     * Returns the Y offset for a texture map.
     * 
     * @return position
     */
    public float getTextureOffsetY() {
        int row = textureIndex / model.getTexture().getNumberOfRows();
        return ((float) row / (float) model.getTexture().getNumberOfRows());
    }

    public TexturedModel getModel() {
        return model;
    }

    public WorldEntity getEntity() {
        return entity;
    }

    public void setEntity(WorldEntity entity) {
        this.entity = entity;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return entity.getPosition();
    }

    public float getRotX() {
        return entity.getRotX();
    }

    public float getRotY() {
        return entity.getRotY();
    }

    public float getRotZ() {
        return entity.getRotZ();
    }

    public float getScale() {
        return entity.getScale();
    }

}
