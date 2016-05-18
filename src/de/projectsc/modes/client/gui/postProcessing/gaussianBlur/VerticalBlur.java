package de.projectsc.modes.client.gui.postProcessing.gaussianBlur;

import de.projectsc.modes.client.gui.postProcessing.PostProcessingEffect;
import de.projectsc.modes.client.gui.shaders.postprocessing.VerticalBlurShader;

/**
 * Gaussian blur effect.
 * 
 * @author Josch Bosch
 */
public class VerticalBlur extends PostProcessingEffect {

    public VerticalBlur(int targetFboWidth, int targetFboHeight) {
        super(targetFboWidth, targetFboHeight);
    }

    @Override
    protected void addShader(int targetFboWidth, int targetFboHeight) {
        shader = new VerticalBlurShader();
        shader.start();
        ((VerticalBlurShader) shader).loadTargetHeight(targetFboHeight);
        shader.stop();
    }
}
