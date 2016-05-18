/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.gui.postProcessing;

import de.projectsc.modes.client.gui.shaders.postprocessing.ContrastShader;

/**
 * PostProcessing effect to change the contrast.
 * 
 * @author Josch Bosch
 */
public class ConstrastChanger extends PostProcessingEffect {

    public ConstrastChanger(int targetFboWidth, int targetFboHeight) {
        super(targetFboWidth, targetFboHeight);

    }

    @Override
    protected void addShader(int targetFboWidth, int targetFboHeight) {
        shader = new ContrastShader();
    }

}
