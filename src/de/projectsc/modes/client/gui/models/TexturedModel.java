/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.models;

import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.modes.client.gui.textures.ModelTexture;

/**
 * A model with a texture.
 * 
 * @author Josch Bosch
 */
public class TexturedModel {

    private final RawModel rawModel;

    private final ModelTexture texture;

    private Matrix4f modelMatrix = new Matrix4f();

    public TexturedModel(RawModel rawModel, ModelTexture texture) {
        super();
        this.rawModel = rawModel;
        this.texture = texture;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getTexture() {
        return texture;
    }

    public void setModelMatrix(Matrix4f matrix) {
        this.modelMatrix = matrix;
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

}
