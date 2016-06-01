/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.models;

import java.util.List;

import de.projectsc.core.data.animation.AnimatedFrame;
import de.projectsc.core.data.animation.AnimationController;
import de.projectsc.modes.client.gui.textures.ModelTexture;

/**
 * An animated model.
 * 
 * @author Josch Bosch
 */
public class AnimatedModel extends TexturedModel {

    public static final int MAX_WEIGHTS = 4;

    private List<AnimatedFrame> animatedFrames;

    private AnimationController animationController;

    public AnimatedModel(RawModel rawModel, List<AnimatedFrame> animatedFrames, ModelTexture texture) {
        super(rawModel, texture);
        this.setAnimatedFrames(animatedFrames);
    }

    public AnimatedModel(RawModel model, AnimationController controller, ModelTexture texture) {
        super(model, texture);
        this.setAnimationController(controller);
    }

    public List<AnimatedFrame> getAnimatedFrames() {
        return animatedFrames;
    }

    public void setAnimatedFrames(List<AnimatedFrame> animatedFrames) {
        this.animatedFrames = animatedFrames;
    }

    public AnimationController getAnimationController() {
        return animationController;
    }

    public void setAnimationController(AnimationController animationController) {
        this.animationController = animationController;
    }
}
