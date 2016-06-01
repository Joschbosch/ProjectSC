/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.animation;

import java.util.Collection;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.utils.Maths;

public class AnimationController {

    private String id;

    private Animation animation;

    private Map<String, Animation> animations;

    private float time = 0;

    private Matrix4f[] jointMatrices;

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
                endframe = t.getKeyframes().get(i + 1);
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
        for (Joint root : animation.getSkeleton().getRootJoints()) {
            root.updateMatrix(true);
        }
        // create joint matrices : joint.worldmatrix * inverseBindMatrix * bindShapeMatrix
        Collection<Joint> joints = animation.getSkeleton().getJoints();
        this.jointMatrices = new Matrix4f[joints.size()];
        for (Joint joint : joints) {
            Matrix4f jointMatrix = new Matrix4f();
            // Matrix4f.mul(jointMatrix, animation.getSkeleton().getBindShapeMatrix(), jointMatrix);
            // Matrix4f.mul(animation.getSkeleton().getBindShapeMatrix(), jointMatrix, jointMatrix); // add later

            Matrix4f.mul(jointMatrix, joint.getInverseBindMatrix(), jointMatrix);
            Matrix4f.mul(jointMatrix, joint.getWorldMatrix(), jointMatrix);

            // Matrix4f.mul(joint.getWorldMatrix(), jointMatrix, jointMatrix);
            // Matrix4f.mul(joint.getInverseBindMatrix(), jointMatrix, jointMatrix);

            jointMatrices[joint.getId()] = jointMatrix;
        }
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

    public Matrix4f[] getJointMatrices() {
        return jointMatrices;
    }

}
