/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.models;

import de.projectsc.gui.textures.ModelTexture;

/**
 * A model with a texture.
 * 
 * @author Josch Bosch
 */
public class TexturedModel {

    private final RawModel rawModel;

    private final ModelTexture texture;

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

}
