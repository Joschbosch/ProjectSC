/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.animation;

import java.util.Map;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.utils.Maths;

public class AnimationController {

    private String id;

    private Animation animation;

    private Map<String, Animation> animations;

    private float time = 0;

    public void update() {

        this.time += 15f / 1000f;
        if (time > animation.getDuration()) {
            time = time % animation.getDuration();
        }

        Map<String, Track> tracks = animation.getTracks();
        Keyframe startframe = null;
        Keyframe endframe = null;
        for (Track t : tracks.values()) {
            Joint j = animation.getSkeleton().getJoint(t.getJointId());
            if (j == null) {
                return;
            }

            for (int i = 0; i < t.getKeyframes().size() - 1; i++) {
                startframe = t.getKeyframes().get(i);
                endframe = t.getKeyframes().get(i);
                if (endframe.getTime() >= time) {
                    break;
                }
            }

            float percent = (time - startframe.getTime()) / (endframe.getTime() - startframe.getTime());
            Vector3f pos = Maths.lerp(startframe.getTranslation(), endframe.getTranslation(), percent);
            Quaternion rot = Maths.slerp(startframe.getOrientation(), endframe.getOrientation(), percent);
            Vector3f scale = Maths.lerp(startframe.getScaling(), endframe.getScaling(), percent);
            j.setLocalMatrix(Maths.createTransformationMatrix(rot, pos, scale));
        }

        // Update all world matrices

        // create joint matrices : joint.worldmatrix * inverseBindMatrix * bindShapeMatrix
        // set joint matrices in controller??

        // call update!

        // render -> load from controller
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Map<String, Animation> getAnimations() {
        return animations;
    }

    public void setAnimations(Map<String, Animation> animations) {
        this.animations = animations;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

}
