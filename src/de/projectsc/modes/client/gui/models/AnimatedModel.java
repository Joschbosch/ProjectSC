/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.models;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.core.data.AnimatedFrame;
import de.projectsc.modes.client.gui.textures.ModelTexture;

/**
 * An animated model.
 * 
 * @author Josch Bosch
 */
public class AnimatedModel extends TexturedModel {

    public static final int MAX_WEIGHTS = 4;

    private List<Matrix4f> invJointMatrices;

    private List<AnimatedFrame> animatedFrames;

    public AnimatedModel(RawModel rawModel, List<Matrix4f> invJointMatrices, List<AnimatedFrame> animatedFrames, ModelTexture texture) {
        super(rawModel, texture);
        this.setInvJointMatrices(invJointMatrices);
        this.setAnimatedFrames(animatedFrames);
    }

    public List<Matrix4f> getInvJointMatrices() {
        return invJointMatrices;
    }

    public void setInvJointMatrices(List<Matrix4f> invJointMatrices) {
        this.invJointMatrices = invJointMatrices;
    }

    public List<AnimatedFrame> getAnimatedFrames() {
        return animatedFrames;
    }

    public void setAnimatedFrames(List<AnimatedFrame> animatedFrames) {
        this.animatedFrames = animatedFrames;
    }

}
