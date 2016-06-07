/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.models;

import de.projectsc.core.data.animation.AnimationController;
import de.projectsc.modes.client.gui.textures.ModelTexture;

/**
 * An animated model.
 * 
 * @author Josch Bosch
 */
public class AnimatedModel extends TexturedModel {

    public static final int MAX_WEIGHTS = 4;

    private AnimationController animationController;

    public AnimatedModel(RawModel rawModel, ModelTexture texture) {
        super(rawModel, texture);
    }

    public AnimatedModel(RawModel model, AnimationController controller, ModelTexture texture) {
        super(model, texture);
        this.setAnimationController(controller);
    }

    public AnimationController getAnimationController() {
        return animationController;
    }

    public void setAnimationController(AnimationController animationController) {
        this.animationController = animationController;
    }
}
