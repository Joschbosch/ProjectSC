/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.javagl.jgltf.impl.Accessor;
import de.javagl.jgltf.impl.Animation;
import de.javagl.jgltf.impl.AnimationChannel;
import de.javagl.jgltf.impl.GlTF;
import de.javagl.jgltf.impl.Mesh;
import de.javagl.jgltf.impl.MeshPrimitive;
import de.javagl.jgltf.impl.Node;
import de.javagl.jgltf.impl.Skin;
import de.javagl.jgltf.model.GltfData;
import de.javagl.jgltf.model.GltfDataLoader;
import de.projectsc.modes.client.gui.models.AnimatedModel;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.textures.ModelTexture;
import de.projectsc.modes.client.gui.utils.GUIConstants;
import de.projectsc.modes.client.gui.utils.Loader;

public class GLTFLoader {

    private static GltfData data;

    private static Map<String, GLTFNode> nodes;

    private static Map<String, GLTFJoint> joints;

    public static synchronized List<TexturedModel> loadGLTF(String filename) {
        Consumer consumer = null;
        nodes = new HashMap<>();
        List<TexturedModel> list = new LinkedList<>();

        ModelTexture white =
            new ModelTexture(Loader.loadTexture(
                GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale.png"), "PNG"));
        white.setNormalMap(Loader.loadTexture(
            GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale_n.png"), "PNG"));

        try {
            data = GltfDataLoader.load(GLTFLoader.class.getResource("/models/animated/" + filename).toURI(), consumer);
            GlTF gltf = data.getGltf();
            RawModel model = loadModel(data, gltf);
            createNodes();
            createJoints();
            createSkins();
            GLTFAnimation animation = loadAnimations();
            if (!gltf.getSkins().isEmpty()) {
                for (Skin s : gltf.getSkins().values()) {
                    AnimatedModel animated = new AnimatedModel(model, readJointMatrices(s, data), null, white);
                    animated.setGltf(gltf);
                    list.add(animated);
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return list;

    }

    private static void createNodes() {
        Map<String, Node> origNodes = data.getGltf().getNodes();
        for (String key : origNodes.keySet()) {
            if (!nodes.containsKey(key)) {
                createNode(key, origNodes.get(key), origNodes);
            }
        }
        GLTFNode root = null;
        for (GLTFNode node : nodes.values()) {
            if (node.getParent() == null) {
                if (root == null) {
                    root = node;
                } else {
                    System.err.println("TWO ROOT NODES!");
                }
            }
        }
        for (GLTFNode node : nodes.values()) {
            if (node.getParent() != null) {
                node.setRoot(root);
            }
        }
    }

    private static GLTFNode createNode(String key, Node node, Map<String, Node> origNodes) {
        GLTFNode glNode = new GLTFNode();
        glNode.setCamera(node.getCamera());
        glNode.setChildren(node.getChildren());
        glNode.setExtensions(node.getExtensions());
        glNode.setExtras(node.getExtras());
        glNode.setJointName(node.getJointName());
        glNode.setMatrix(node.getMatrix());
        glNode.setMeshes(node.getMeshes());
        glNode.setName(node.getName());
        glNode.setRotation(node.getRotation());
        glNode.setScale(node.getScale());
        glNode.setSkeletons(node.getSkeletons());
        glNode.setSkin(node.getSkin());
        glNode.setTranslation(node.getTranslation());
        nodes.put(key, glNode);
        for (String child : node.getChildren()) {
            if (nodes.containsKey(child)) {
                GLTFNode childNode = nodes.get(child);
                childNode.setParent(glNode);
                glNode.addChild(childNode);
            } else {
                GLTFNode childNode = createNode(child, origNodes.get(child), origNodes);
                glNode.addChild(childNode);
                childNode.setParent(glNode);
            }
        }
        return glNode;
    }

    private static void createSkins() {

    }

    private static Map<String, GLTFJoint> createJoints() {
        Map<String, Node> nodes = data.getGltf().getNodes();
        Map<String, GLTFJoint> result = new HashMap<>();
        for (Node n : nodes.values()) {
            if (n.getJointName() != null) {
                GLTFJoint joint = new GLTFJoint();
                joint.setName(n.getJointName());
                joint.setPosition(new Vector3f(n.getTranslation()[0], n.getTranslation()[1], n.getTranslation()[2]));
                joint.setScale(new Vector3f(n.getScale()[0], n.getScale()[1], n.getScale()[2]));
                joint.setRotation(new Quaternion(n.getRotation()[0], n.getRotation()[1], n.getRotation()[2], n.getRotation()[3]));
                result.put(n.getJointName(), joint);
            }
        }
        return result;
    }

    private static RawModel loadModel(GltfData result, GlTF gltf) {
        RawModel model = null;
        for (Mesh mesh : gltf.getMeshes().values()) {
            for (MeshPrimitive p : mesh.getPrimitives()) {
                String indicesAccessor = p.getIndices();
                String positionsAccessor = p.getAttributes().get("POSITION");
                String normalsAccessor = p.getAttributes().get("NORMAL");
                String texCoordAccessor = p.getAttributes().get("TEXCOORD_0");
                String jointsAccessor = p.getAttributes().get("JOINT");
                String weigthsAccessor = p.getAttributes().get("WEIGHT");
                if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null && texCoordAccessor != null) {
                    model = Loader.loadToVAO(getFloatBuffer(positionsAccessor, result), getFloatBuffer(texCoordAccessor, result),
                        getFloatBuffer(normalsAccessor, result), getFloatBuffer(positionsAccessor, result),
                        getIntBuffer(indicesAccessor, result));

                } else if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null && texCoordAccessor != null
                    && weigthsAccessor != null && jointsAccessor != null) {
                    model = Loader.loadToVAO(getFloatBuffer(positionsAccessor, result), getFloatBuffer(texCoordAccessor, result),
                        getFloatBuffer(normalsAccessor, result), getIntBuffer(indicesAccessor, result),
                        getIntBuffer(jointsAccessor, result), getFloatBuffer(weigthsAccessor, result));
                }
            }
        }
        return model;
    }

    private static GLTFAnimation loadAnimations() {
        GLTFAnimation result = null;
        GlTF gltf = data.getGltf();
        if (gltf.getAnimations() != null && !gltf.getAnimations().isEmpty()) {
            result = new GLTFAnimation();
            for (Animation ani : gltf.getAnimations().values()) {
                GLTFTrack track = new GLTFTrack();
                track.setJointName(ani.getChannels().get(0).getTarget().getId());
                track.setJointId(joints.get(track.getJointName()).getId());
                track.setJoint(joints.get(track.getJointName()));

                String timeAccessorID = ani.getParameters().get("TIME");
                Accessor timeAccessor = data.getGltf().getAccessors().get(timeAccessorID);
                FloatBuffer timeBuffer = data.getExtractedAccessorByteBuffer(timeAccessorID).asFloatBuffer();
                for (int i = 0; i < timeAccessor.getCount(); i++) {
                    GLTFKeyFrame keyframe = new GLTFKeyFrame();
                    keyframe.setTime(timeBuffer.get(i));
                    for (AnimationChannel c : ani.getChannels()) {
                        if (c.getTarget().getPath().equals("rotation")) {
                            FloatBuffer buffer = data.getExtractedAccessorByteBuffer(ani.getParameters().get("rotation")).asFloatBuffer();
                            keyframe.setTranslation(new Vector3f(buffer.get(i), buffer.get(i + 1), buffer.get(i + 2)));
                        }
                        if (c.getTarget().getPath().equals("scale")) {
                            FloatBuffer buffer = data.getExtractedAccessorByteBuffer(ani.getParameters().get("scale")).asFloatBuffer();
                            keyframe.setScaling(new Vector3f(buffer.get(i), buffer.get(i + 1), buffer.get(i + 2)));
                        }
                        if (c.getTarget().getPath().equals("rotation")) {
                            FloatBuffer buffer = data.getExtractedAccessorByteBuffer(ani.getParameters().get("rotation")).asFloatBuffer();
                            keyframe.setOrientation(new Quaternion(buffer.get(i), buffer.get(i + 1), buffer.get(i + 2), buffer.get(i + 3)));
                        }
                    }
                    track.addKeyframe(keyframe);
                }
                result.addTrack(track);
            }
        }
        return result;
    }

    private static List<Matrix4f> readJointMatrices(Skin s, GltfData data) {
        List<Matrix4f> result = new LinkedList<>();
        FloatBuffer buffer = getFloatBuffer(s.getInverseBindMatrices(), data);
        while (buffer.hasRemaining()) {
            Matrix4f m = new Matrix4f();
            m.m00 = buffer.get();
            m.m01 = buffer.get();
            m.m02 = buffer.get();
            m.m03 = buffer.get();
            m.m10 = buffer.get();
            m.m11 = buffer.get();
            m.m12 = buffer.get();
            m.m13 = buffer.get();
            m.m20 = buffer.get();
            m.m21 = buffer.get();
            m.m22 = buffer.get();
            m.m23 = buffer.get();
            m.m30 = buffer.get();
            m.m31 = buffer.get();
            m.m32 = buffer.get();
            m.m33 = buffer.get();
            result.add(m);
        }
        return result;
    }

    private static FloatBuffer getFloatBuffer(String accessor, GltfData rawData) {
        ByteBuffer data = rawData.getExtractedAccessorByteBuffer(accessor);
        return data.asFloatBuffer();
    }

    private static int[] getIntBuffer(String accessor, GltfData rawData) {
        ByteBuffer data = rawData.getExtractedAccessorByteBuffer(accessor);
        List<Integer> temp = new ArrayList<>();
        while (data.hasRemaining()) {
            temp.add(Short.toUnsignedInt(data.getShort()));
        }
        int[] result = new int[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            result[i] = temp.get(i);
        }
        return result;
    }
}
