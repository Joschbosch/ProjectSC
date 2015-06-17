/*
 * Copyright (C) 2015
 */

package de.projectsc.gui.models;

import de.projectsc.gui.textures.ModelTexture;

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
