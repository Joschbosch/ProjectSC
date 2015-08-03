/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.components.impl;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.textures.ModelTexture;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.ModelData;
import de.projectsc.client.gui.tools.NewOBJFileLoader;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.entities.Entity;

public class ModelAndTextureComponent extends Component {

    private static final Log LOGGER = LogFactory.getLog(ModelAndTextureComponent.class);

    private RawModel model;

    private ModelTexture modelTexture;

    private int textureIndex = 0;

    public ModelAndTextureComponent() {
        super("Model and Texture Component");
        textureIndex = 0;
        type = ComponentType.GRAPHICS;
    }

    public ModelAndTextureComponent(int textureIndex) {
        super("Model and Texture Component");
        this.textureIndex = textureIndex;
        type = ComponentType.GRAPHICS;

    }

    @Override
    public void update(Entity owner) {

    }

    /**
     * Returns the X offset for a texture map.
     * 
     * @return position
     */
    public float getTextureOffsetX() {
        int column = textureIndex % modelTexture.getNumberOfRows();
        return (column / (float) modelTexture.getNumberOfRows());
    }

    /**
     * Returns the Y offset for a texture map.
     * 
     * @return position
     */
    public float getTextureOffsetY() {
        int row = textureIndex / modelTexture.getNumberOfRows();
        return (row / (float) modelTexture.getNumberOfRows());
    }

    public void loadModel(Loader loader, Entity owner) {
        ModelData data = NewOBJFileLoader.loadOBJ("M" + owner.getEntityTypeId() + "/model.obj");
        model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        int texture = 0 - 1;
        try {
            File textureFile = new File(this.getClass().getResource("model/M" + owner.getEntityTypeId() + "/texture.obj").toURI());
            loader.loadTexture(textureFile);
        } catch (URISyntaxException e) {
            LOGGER.error("Could not load texture for " + owner.getEntityTypeId());
        }
        modelTexture = new ModelTexture(texture);
        // load texture settings for this model
    }

    public void setIsTransparent(boolean value) {
        modelTexture.setTransparent(value);
    }

    public void setFakeLighting(boolean value) {
        modelTexture.setFakeLighting(value);
    }

    public void setIsTransparent(float value) {
        modelTexture.setReflectivity(value);
    }

    public void setShineDamper(float value) {
        modelTexture.setShineDamper(value);
    }

    public void setReflectivity(float value) {
        modelTexture.setReflectivity(value);
    }

    public void setNumberOfRows(int value) {
        modelTexture.setNumberOfRows(value);
    }

    public TexturedModel getTexturedModel() {
        return new TexturedModel(model, modelTexture);
    }

    public RawModel getModel() {
        return model;
    }

    public ModelTexture getModelTexture() {
        return modelTexture;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }
}
