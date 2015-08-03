/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.components;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.textures.ModelTexture;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.ModelData;
import de.projectsc.client.gui.tools.OBJFileLoader;
import de.projectsc.core.entities.Entity;

public class ModelAndTextureComponent extends Component {

    private RawModel model;

    private ModelTexture modelTexture;

    public ModelAndTextureComponent() {
        super("Model and Texture Component");
    }

    @Override
    public void update(Entity owner) {

    }

    public void loadModel(Loader loader, Entity owner) {
        ModelData data = OBJFileLoader.loadOBJ("" + owner.getEntityTypeId());
        model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        int texture = 0 - 1;
        modelTexture = new ModelTexture(texture);
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
}
