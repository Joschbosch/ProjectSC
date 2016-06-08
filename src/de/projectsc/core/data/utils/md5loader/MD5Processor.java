/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.md5loader;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.animation.Animation;
import de.projectsc.core.data.animation.AnimationController;
import de.projectsc.core.data.animation.Joint;
import de.projectsc.core.data.animation.Keyframe;
import de.projectsc.core.data.animation.Skeleton;
import de.projectsc.core.data.animation.Track;
import de.projectsc.core.data.utils.md5loader.anim.MD5AnimModel;
import de.projectsc.core.data.utils.md5loader.anim.MD5BaseFrame;
import de.projectsc.core.data.utils.md5loader.anim.MD5Frame;
import de.projectsc.core.data.utils.md5loader.anim.MD5Hierarchy;
import de.projectsc.core.data.utils.md5loader.mesh.MD5Joints;
import de.projectsc.core.data.utils.md5loader.mesh.MD5Joints.MD5JointData;
import de.projectsc.core.data.utils.md5loader.mesh.MD5Mesh;
import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.AnimatedModel;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.textures.ModelTexture;
import de.projectsc.modes.client.gui.utils.Loader;

public class MD5Processor {

    public static List<TexturedModel> process(MD5Model md5Model, MD5AnimModel animModel) {
        List<TexturedModel> newModels = new LinkedList<>();

        List<Matrix4f> invJointMatrices = calcInJointMatrices(md5Model);

        for (MD5Mesh md5Mesh : md5Model.getMeshes()) {
            RawModel mesh = null;
            mesh = generateMesh(md5Model, md5Mesh);
            ModelTexture texture =
                new ModelTexture(Loader.loadTexture(MD5Loader.class.getResourceAsStream(md5Mesh.getTexture()), "png"));

            TexturedModel model = null;
            if (animModel != null) {
                Animation animation = processAnimation(md5Model, animModel, invJointMatrices);
                model = new AnimatedModel(mesh, texture);
                AnimationController animationController = new AnimationController();
                animationController.setAnimation(animation);
                ((AnimatedModel) model).setAnimationController(animationController);
            } else {
                texture.setTransparent(true);
                model = new TexturedModel(mesh, texture);
            }
            newModels.add(model);
        }
        return newModels;
    }

    private static Animation processAnimation(MD5Model md5Model, MD5AnimModel animModel, List<Matrix4f> invJointMatrices) {
        Animation animation = null;
        if (animModel != null) {
            animation = new Animation();
            Skeleton s = new Skeleton();
            animation.setSkeleton(s);
            animation.setFrameCount(animModel.getFrames().size());
            animation.setDuration((float) (animModel.getFrames().size() / 24.0));
            s.setBindShapeMatrix(new Matrix4f());
            s.setName("New MD5Skeleton");
            List<MD5JointData> joints = md5Model.getJointInfo().getJoints();
            Map<Integer, Joint> newJoints = new HashMap<>();
            for (int i = 0; i < joints.size(); i++) {
                Joint joint = new Joint();
                joint.setId(i);
                joint.setName(joints.get(i).getName());
                newJoints.put(i, joint);
            }
            for (int i = 0; i < joints.size(); i++) {
                Joint current = newJoints.get(i);
                int parentIndex = joints.get(i).getParentIndex();
                if (parentIndex > -1) {
                    current.setParent(newJoints.get(parentIndex));
                    current.getParent().addChild(current);
                } else {
                    s.addRootJoint(current);
                    current.setParentMatrix(Maths.getYUpMatrix());
                }
                s.addJoint(current);
            }
            processAnimationFrames(md5Model, animModel, invJointMatrices, animation, newJoints);
        }
        return animation;
    }

    public static void processAnimationFrames(MD5Model md5Model, MD5AnimModel animModel,
        List<Matrix4f> invJointMatrices, Animation animation, Map<Integer, Joint> newJoints) {

        MD5BaseFrame baseFrame = animModel.getBaseFrame();
        List<MD5Hierarchy.MD5HierarchyData> hierarchyList = animModel.getHierarchy().getHierarchyDataList();

        List<MD5Joints.MD5JointData> joints = md5Model.getJointInfo().getJoints();
        int numJoints = joints.size();
        for (int i = 0; i < numJoints; i++) {
            Track track = new Track();
            track.setJointId(i);
            track.setJointName(newJoints.get(i).getName());
            newJoints.get(i).setInverseBindMatrix(invJointMatrices.get(i));

            for (int j = 0; j < animModel.getFrames().size(); j++) {
                MD5Frame frame = animModel.getFrames().get(j);
                Float[] frameData = frame.getFrameData();
                Keyframe keyframe = new Keyframe();
                keyframe.setTime(1f / 24.0f * j);
                setJointInfoForKeyframe(baseFrame, hierarchyList, joints, i, frameData, keyframe);
                track.addKeyframe(keyframe);
            }
            animation.addTrack(track);
        }

    }

    private static void setJointInfoForKeyframe(MD5BaseFrame baseFrame, List<MD5Hierarchy.MD5HierarchyData> hierarchyList,
        List<MD5Joints.MD5JointData> joints, int i, Float[] frameData, Keyframe keyframe) {
        MD5BaseFrame.MD5BaseFrameData baseFrameData = baseFrame.getFrameDataList().get(i);
        Vector3f position = new Vector3f(baseFrameData.getPosition());
        Quaternion orientation = new Quaternion(baseFrameData.getOrientation());

        int flags = hierarchyList.get(i).getFlags();
        int startIndex = hierarchyList.get(i).getStartIndex();
        if ((flags & 1) > 0) {
            position.x = frameData[startIndex++];
        }
        if ((flags & 2) > 0) {
            position.y = frameData[startIndex++];
        }
        if ((flags & 4) > 0) {
            position.z = frameData[startIndex++];
        }
        if ((flags & 8) > 0) {
            orientation.x = frameData[startIndex++];
        }
        if ((flags & 16) > 0) {
            orientation.y = frameData[startIndex++];
        }
        if ((flags & 32) > 0) {
            orientation.z = frameData[startIndex++];
        }
        // Update Quaternion's w component
        orientation.w = MD5Utils.calculateWValue(new Vector3f(orientation.x, orientation.y, orientation.z));
        keyframe.setOrientation(orientation);
        keyframe.setTranslation(position);
        keyframe.setScaling(new Vector3f(1, 1, 1));

    }

    private static List<Matrix4f> calcInJointMatrices(MD5Model md5Model) {
        List<Matrix4f> result = new ArrayList<>();

        List<MD5Joints.MD5JointData> joints = md5Model.getJointInfo().getJoints();
        for (MD5Joints.MD5JointData joint : joints) {
            Matrix4f translateMat = new Matrix4f().translate(joint.getPosition());
            Matrix4f rotationMat = transform(joint.getOrientation());
            Matrix4f mat = Matrix4f.mul(translateMat, rotationMat, null);
            mat.invert();
            result.add(mat);
        }
        return result;
    }

    private static RawModel generateMesh(MD5Model md5Model, MD5Mesh md5Mesh) {
        List<AnimVertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        List<MD5Mesh.MD5Vertex> md5Vertices = md5Mesh.getVertices();
        List<MD5Mesh.MD5Weight> weights = md5Mesh.getWeights();
        List<MD5Joints.MD5JointData> joints = md5Model.getJointInfo().getJoints();

        for (MD5Mesh.MD5Vertex md5Vertex : md5Vertices) {
            AnimVertex vertex = new AnimVertex();
            vertices.add(vertex);

            vertex.position = new Vector3f();
            vertex.textCoords = md5Vertex.getTextCoords();

            int startWeight = md5Vertex.getStartWeight();
            int numWeights = md5Vertex.getWeightCount();

            vertex.jointIndices = new int[numWeights];
            Arrays.fill(vertex.jointIndices, -1);
            vertex.weights = new float[numWeights];
            Arrays.fill(vertex.weights, -1);
            for (int i = startWeight; i < startWeight + numWeights; i++) {
                MD5Mesh.MD5Weight weight = weights.get(i);
                MD5Joints.MD5JointData joint = joints.get(weight.getJointIndex());
                Vector3f rotatedPos = transform(weight.getPosition(), joint.getOrientation()); // position.rotate(quat).
                Vector3f acumPos = Vector3f.add(joint.getPosition(), rotatedPos, null);
                acumPos.scale(weight.getBias());
                vertex.position = Vector3f.add(vertex.position, acumPos, null);
                vertex.jointIndices[i - startWeight] = weight.getJointIndex();
                vertex.weights[i - startWeight] = weight.getBias();
            }
        }

        for (MD5Mesh.MD5Triangle tri : md5Mesh.getTriangles()) {
            indices.add(tri.getVertex0());
            indices.add(tri.getVertex1());
            indices.add(tri.getVertex2());

            // Normals
            AnimVertex v0 = vertices.get(tri.getVertex0());
            AnimVertex v1 = vertices.get(tri.getVertex1());
            AnimVertex v2 = vertices.get(tri.getVertex2());
            Vector3f pos0 = v0.position;
            Vector3f pos1 = v1.position;
            Vector3f pos2 = v2.position;

            Vector3f normal = Vector3f.cross(Vector3f.sub(pos2, pos0, null), Vector3f.sub(pos1, pos0, null), null);
            normal.normalise();

            v0.normal = (Vector3f) Vector3f.add(v0.normal, normal, null).normalise();
            v1.normal = (Vector3f) Vector3f.add(v1.normal, normal, null).normalise();
            v2.normal = (Vector3f) Vector3f.add(v2.normal, normal, null).normalise();
        }
        return createMesh(vertices, indices);
    }

    private static RawModel createMesh(List<AnimVertex> vertices, List<Integer> indices) {
        List<Float> positions = new ArrayList<>();
        List<Float> textCoords = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> jointIndices = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        for (AnimVertex vertex : vertices) {
            positions.add(vertex.position.x);
            positions.add(vertex.position.y);
            positions.add(vertex.position.z);

            textCoords.add(vertex.textCoords.x);
            textCoords.add(vertex.textCoords.y);

            normals.add(vertex.normal.x);
            normals.add(vertex.normal.y);
            normals.add(vertex.normal.z);

            int numWeights = vertex.weights.length;
            for (int i = 0; i < AnimatedModel.MAX_WEIGHTS; i++) {
                if (i < numWeights) {
                    jointIndices.add(vertex.jointIndices[i]);
                    weights.add(vertex.weights[i]);
                } else {
                    jointIndices.add(-1);
                    weights.add(-1.0f);
                }
            }
        }

        float[] positionsArr = listToArray(positions);
        float[] textCoordsArr = listToArray(textCoords);
        float[] normalsArr = listToArray(normals);
        float[] tangents = new float[positions.size()];
        Arrays.fill(tangents, 0f);
        int[] indicesArr = listIntToArray(indices);
        int[] jointIndicesArr = listIntToArray(jointIndices);
        float[] weightsArr = listToArray(weights);

        return Loader.loadToVAO(positionsArr, textCoordsArr, normalsArr, tangents, indicesArr, jointIndicesArr, weightsArr);
    }

    /**
     * Loads the given model into the graphics card.
     * 
     * @param positions to load.
     * @param textCoords to load.
     * @param normals to load.
     * @param indices to load.
     * @param jointIndices to load.
     * @param weights to load.
     * @return {@link RawModel} with neccessary information.
     */
    public static RawModel loadUp(float[] positions, float[] textCoords, float[] normals, int[] indices, int[] jointIndices,
        float[] weights) {
        int vertexCount = indices.length;

        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Position VBO
        int vboId = glGenBuffers();
        FloatBuffer posBuffer = BufferUtils.createFloatBuffer(positions.length);
        posBuffer.put(positions).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // Texture coordinates VBO
        vboId = glGenBuffers();
        FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
        textCoordsBuffer.put(textCoords).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        // Vertex normals VBO
        vboId = glGenBuffers();
        FloatBuffer vecNormalsBuffer = BufferUtils.createFloatBuffer(normals.length);
        vecNormalsBuffer.put(normals).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        // Weights
        vboId = glGenBuffers();
        FloatBuffer weightsBuffer = BufferUtils.createFloatBuffer(weights.length);
        weightsBuffer.put(weights).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, 0, 0);

        // Joint indices
        vboId = glGenBuffers();
        IntBuffer jointIndicesBuffer = BufferUtils.createIntBuffer(jointIndices.length);
        jointIndicesBuffer.put(jointIndices).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, jointIndicesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, 0, 0);

        // Index VBO
        vboId = glGenBuffers();
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        RawModel model = new RawModel(vaoId, vertexCount);
        return model;
    }

    public static int[] listIntToArray(List<Integer> list) {
        int[] result = list.stream().mapToInt((Integer v) -> v).toArray();
        return result;
    }

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    private static Vector3f transform(Vector3f vec, Quaternion q) {
        float num = q.x + q.x;
        float num2 = q.y + q.y;
        float num3 = q.z + q.z;
        float num4 = q.x * num;
        float num5 = q.y * num2;
        float num6 = q.z * num3;
        float num7 = q.x * num2;
        float num8 = q.x * num3;
        float num9 = q.y * num3;
        float num10 = q.w * num;
        float num11 = q.w * num2;
        float num12 = q.w * num3;
        return new Vector3f((1.0f - (num5 + num6)) * vec.x + (num7 - num12) * vec.y + (num8 + num11) * vec.z,
            (num7 + num12) * vec.x + (1.0f - (num4 + num6)) * vec.y + (num9 - num10) * vec.z,
            (num8 - num11) * vec.x + (num9 + num10) * vec.y + (1.0f - (num4 + num5)) * vec.z);
    }

    public static Matrix4f transform(Quaternion quat) {
        float dqx = quat.x + quat.x;
        float dqy = quat.y + quat.y;
        float dqz = quat.z + quat.z;
        float q00 = dqx * quat.x;
        float q11 = dqy * quat.y;
        float q22 = dqz * quat.z;
        float q01 = dqx * quat.y;
        float q02 = dqx * quat.z;
        float q03 = dqx * quat.w;
        float q12 = dqy * quat.z;
        float q13 = dqy * quat.w;
        float q23 = dqz * quat.w;
        Matrix4f rotated = new Matrix4f();
        rotated.m00 = 1.0f - q11 - q22;
        rotated.m01 = q01 + q23;
        rotated.m02 = q02 - q13;
        rotated.m03 = 0.0f;
        rotated.m10 = q01 - q23;
        rotated.m11 = 1.0f - q22 - q00;
        rotated.m12 = q12 + q03;
        rotated.m13 = 0.0f;
        rotated.m20 = q02 + q13;
        rotated.m21 = q12 - q03;
        rotated.m22 = 1.0f - q11 - q00;
        rotated.m23 = 0.0f;
        rotated.m30 = 0.0f;
        rotated.m31 = 0.0f;
        rotated.m32 = 0.0f;
        rotated.m33 = 1.0f;

        return rotated;
    }
}
