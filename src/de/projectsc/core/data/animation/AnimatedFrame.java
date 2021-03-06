package de.projectsc.core.data.animation;

import java.util.Arrays;

import org.lwjgl.util.vector.Matrix4f;

public class AnimatedFrame {

    public static final int MAX_JOINTS = 150;

    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

    private final Matrix4f[] localJointMatrices;

    private final Matrix4f[] jointMatrices;

    public AnimatedFrame() {
        localJointMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(localJointMatrices, IDENTITY_MATRIX);

        jointMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(jointMatrices, IDENTITY_MATRIX);
    }

    public Matrix4f[] getLocalJointMatrices() {
        return localJointMatrices;
    }

    public Matrix4f[] getJointMatrices() {
        return jointMatrices;
    }

    public void setMatrix(int pos, Matrix4f localJointMatrix, Matrix4f invJointMatrix) {
        localJointMatrices[pos] = localJointMatrix;
        Matrix4f mat = new Matrix4f(localJointMatrix);
        mat = Matrix4f.mul(mat, invJointMatrix, null);
        jointMatrices[pos] = mat;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(localJointMatrices) + "\nJOINTMATRICES: \n" + Arrays.toString(jointMatrices);
    }
}
