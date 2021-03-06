/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.javagl.jgltf.model.GltfData;

public class GLTFUtils {

    public static int[] getIndicesIntArray(String accessor, GltfData rawData) {
        if (accessor != null) {
            ByteBuffer tmpdata = rawData.getExtractedAccessorByteBuffer(accessor);
            List<Integer> temp = new ArrayList<>();
            while (tmpdata.hasRemaining()) {
                temp.add(Short.toUnsignedInt(tmpdata.getShort()));
            }
            int[] result = new int[temp.size()];
            for (int i = 0; i < temp.size(); i++) {
                result[i] = temp.get(i);
            }
            return result;
        }
        return null;
    }

    public static FloatBuffer getFloatBuffer(String accessor, GltfData rawData) {
        if (accessor != null) {
            ByteBuffer tmpdata = rawData.getExtractedAccessorByteBuffer(accessor);
            return tmpdata.asFloatBuffer();
        }
        return null;
    }

    public static int[] getJointIntArray(String accessor, GltfData rawData) {
        if (accessor != null) {
            FloatBuffer tmpdata = rawData.getExtractedAccessorByteBuffer(accessor).asFloatBuffer();
            int[] result = new int[tmpdata.capacity()];
            for (int i = 0; i < tmpdata.capacity(); i++) {
                result[i] = (int) tmpdata.get(i);
            }
            return result;
        }
        return null;
    }

    public static Vector3f loadVectorFromArray(float[] source) {
        Vector3f result = new Vector3f();
        result.x = source[0];
        result.y = source[1];
        result.z = source[2];
        return result;
    }

    public static Quaternion loadQuaternionFromArray(float[] source) {
        Quaternion result = new Quaternion();
        result.x = source[0];
        result.y = source[1];
        result.z = source[2];
        result.w = source[3];
        return result;
    }

    public static Matrix4f loadMatrixFromArray(float[] sourceMatrix) {
        Matrix4f matrix = new Matrix4f();
        matrix.m00 = sourceMatrix[0];
        matrix.m01 = sourceMatrix[1];
        matrix.m02 = sourceMatrix[2];
        matrix.m03 = sourceMatrix[3];
        matrix.m10 = sourceMatrix[4];
        matrix.m11 = sourceMatrix[5];
        matrix.m12 = sourceMatrix[6];
        matrix.m13 = sourceMatrix[7];
        matrix.m20 = sourceMatrix[8];
        matrix.m21 = sourceMatrix[9];
        matrix.m22 = sourceMatrix[10];
        matrix.m23 = sourceMatrix[11];
        matrix.m30 = sourceMatrix[12];
        matrix.m31 = sourceMatrix[13];
        matrix.m32 = sourceMatrix[14];
        matrix.m33 = sourceMatrix[15];
        return matrix;
    }

    public static Matrix4f loadMatrixFromBuffer(int position, FloatBuffer buffer) {
        Matrix4f result = new Matrix4f();
        result.m00 = buffer.get(16 * position + 0);
        result.m01 = buffer.get(16 * position + 1);
        result.m02 = buffer.get(16 * position + 2);
        result.m03 = buffer.get(16 * position + 3);
        result.m10 = buffer.get(16 * position + 4);
        result.m11 = buffer.get(16 * position + 5);
        result.m12 = buffer.get(16 * position + 6);
        result.m13 = buffer.get(16 * position + 7);
        result.m20 = buffer.get(16 * position + 8);
        result.m21 = buffer.get(16 * position + 9);
        result.m22 = buffer.get(16 * position + 10);
        result.m23 = buffer.get(16 * position + 11);
        result.m30 = buffer.get(16 * position + 12);
        result.m31 = buffer.get(16 * position + 13);
        result.m32 = buffer.get(16 * position + 14);
        result.m33 = buffer.get(16 * position + 15);
        return result;

    }

    public static Vector3f loadVectorFromBuffer(FloatBuffer buffer, int i) {
        Vector3f result = new Vector3f();
        result.x = buffer.get(3 * i);
        result.y = buffer.get(3 * i + 1);
        result.z = buffer.get(3 * i + 2);
        return result;

    }

    public static Quaternion loadQuaternionFromBuffer(FloatBuffer buffer, int i) {
        Quaternion result = new Quaternion();
        result.x = buffer.get(4 * i);
        result.y = buffer.get(4 * i + 1);
        result.z = buffer.get(4 * i + 2);
        result.w = buffer.get(4 * i + 3);
        return result;

    }
}
