package de.projectsc.modes.client.gui.postProcessing.gaussianBlur;

import de.projectsc.modes.client.gui.postProcessing.PostProcessingEffect;
import de.projectsc.modes.client.gui.shaders.postprocessing.HorizontalBlurShader;

/**
 * Postprocessing effect for gaussian blur.
 * 
 * @author Josch Bosch
 */
public class HorizontalBlur extends PostProcessingEffect {

    public HorizontalBlur(int targetFboWidth, int targetFboHeight) {
        super(targetFboWidth, targetFboHeight);
    }

    @Override
    protected void addShader(int targetFboWidth, int targetFboHeight) {
        HorizontalBlurShader myShader = new HorizontalBlurShader();
        myShader.start();
        myShader.loadTargetWidth(targetFboWidth);
        myShader.stop();
        shader = myShader;
    }

}
