/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Matrix4f;

import de.javagl.jgltf.impl.Accessor;
import de.javagl.jgltf.impl.AnimationChannel;
import de.javagl.jgltf.impl.GlTF;
import de.javagl.jgltf.impl.Mesh;
import de.javagl.jgltf.impl.MeshPrimitive;
import de.javagl.jgltf.impl.Node;
import de.javagl.jgltf.impl.Skin;
import de.javagl.jgltf.model.GltfData;
import de.javagl.jgltf.model.GltfDataLoader;
import de.projectsc.core.data.animation.Animation;
import de.projectsc.core.data.animation.AnimationController;
import de.projectsc.core.data.animation.Joint;
import de.projectsc.core.data.animation.Keyframe;
import de.projectsc.core.data.animation.Skeleton;
import de.projectsc.core.data.animation.Track;
import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.AnimatedModel;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.textures.ModelTexture;
import de.projectsc.modes.client.gui.utils.GUIConstants;
import de.projectsc.modes.client.gui.utils.Loader;

public class GLTFLoader {

    private static final Log LOGGER = LogFactory.getLog(GLTFLoader.class);

    private GltfData data;

    private GlTF gltf;

    private Map<String, Joint> joints = new HashMap<>();

    private Map<String, Skeleton> skeletons = new HashMap<>();

    private Map<String, GLTFNode> nodes = new HashMap<>();

    private List<GLTFNode> roots = new LinkedList<>();

    private List<Joint> jointRoots = new LinkedList<>();

    private Animation animation;

    public List<TexturedModel> loadGLTF(String filename) {
        Consumer consumer = null;
        List<TexturedModel> list = new LinkedList<>();

        try {
            data = GltfDataLoader.load(GLTFLoader.class.getResource("/models/animated/" + filename).toURI(), consumer);
            gltf = data.getGltf();
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Could not load gltf file: " + filename, e);
        }
        createNodes();
        createJoints();
        createSkins();
        loadAnimation();

        List<RawModel> modelList = loadModel();
        ModelTexture texture = loadTexture();

        System.out.println("Nodes: " + nodes.size());
        System.out.println("Root Nodes: " + roots.size());

        System.out.println("Joints: " + joints.size());
        for (Joint j : joints.values()) {
            System.out.println("Joint " + j.getName() + ": Id= " + j.getId() + " LocalMatrix \n" + j.getLocalMatrix() + " WorldMatrix\n "
                + j.getWorldMatrix() + "  Children: " + j.getChildren());
        }
        System.out.println("Root Joints: " + jointRoots.size());

        System.out.println("Skeletons: " + skeletons.size());
        if (skeletons.size() > 0) {
            Skeleton first = skeletons.values().iterator().next();
            System.out.println("First Skeleton Matrix: \n" + first.getBindShapeMatrix());
            System.out.println("First Skeleton Joints Size: " + first.getJoints().size());
        }
        if (animation != null) {
            System.out.println("Animation frame count: " + animation.getFrameCount());
            System.out.println("Animation duration: " + animation.getDuration());
            System.out.println("Animation tracks size: " + animation.getTracks().size());
        }
        for (RawModel model : modelList) {
            if (animation != null) {
                AnimationController controller = new AnimationController();
                animation.setSkeleton(skeletons.values().iterator().next()); // improve
                controller.setAnimation(animation);
                controller.setId("gltf");
                list.add(new AnimatedModel(model, controller, texture));
            } else {
                list.add(new TexturedModel(model, texture));
            }
        }
        return list;
    }

    private ModelTexture loadTexture() {
        ModelTexture result =
            new ModelTexture(Loader.loadTexture(
                GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + GUIConstants.BASIC_TEXTURE_WHITE), "PNG"));
        return result;
    }

    private void createJoints() {
        for (GLTFNode node : roots) {
            createJointNode(node, null);
        }
    }

    private Joint createJointNode(GLTFNode node, Joint parent) {
        Joint thisNodeIsJoint = null;
        if (node.getParent() == null) {
            node.worldMatrix = node.localMatrix;
        } else {
            node.worldMatrix = Matrix4f.mul(node.getParent().worldMatrix, node.localMatrix, null);
        }

        if (node.getNode().getJointName() != null) {
            thisNodeIsJoint = new Joint();
            thisNodeIsJoint.setName(node.getNode().getJointName());
            thisNodeIsJoint.setLocalMatrix(node.localMatrix);
            thisNodeIsJoint.setWorldMatrix(node.worldMatrix);
            joints.put(thisNodeIsJoint.getName(), thisNodeIsJoint);
            if (parent != null) {
                thisNodeIsJoint.setParent(parent);
            } else {
                jointRoots.add(thisNodeIsJoint);
                thisNodeIsJoint.setParentMatrix(node.getParent().worldMatrix);
            }
        }
        if (node.getChildNodes() != null) {
            for (GLTFNode child : node.getChildNodes().values()) {
                Joint childJoint = createJointNode(child, thisNodeIsJoint);
                if (thisNodeIsJoint != null) {
                    thisNodeIsJoint.addChild(childJoint);
                }
            }
        }
        return thisNodeIsJoint;
    }

    private void createNodes() {
        Map<String, Node> origNodes = gltf.getNodes();
        for (String key : origNodes.keySet()) {
            if (!nodes.containsKey(key)) {
                createNode(key, origNodes.get(key), origNodes);
            }
        }
        for (GLTFNode node : nodes.values()) {
            if (node.getParent() == null) {
                roots.add(node);
            }
        }
    }

    private GLTFNode createNode(String key, Node node, Map<String, Node> origNodes) {
        GLTFNode glNode = new GLTFNode(node);
        if (node.getMatrix() != null) {
            glNode.setLocalMatrix(GLTFUtils.loadMatrixFromArray(node.getMatrix()));
            System.out.println("Load from node : " + node.getName() + "  " + glNode.getLocalMatrix());
        } else if (node.getTranslation() != null) {
            glNode.setPosition(GLTFUtils.loadVectorFromArray(node.getTranslation()));
            glNode.setScale(GLTFUtils.loadVectorFromArray(node.getScale()));
            glNode.setRotation(GLTFUtils.loadQuaternionFromArray(node.getRotation()));
            glNode.setLocalMatrix(
                Maths.createTransformationMatrix(glNode.getRotationQuaternion(), glNode.getPositionVector(), glNode.getScaleVector()));
        }
        nodes.put(key, glNode);
        for (String child : node.getChildren()) {
            GLTFNode childNode = null;
            if (nodes.containsKey(child)) {
                childNode = nodes.get(child);
                childNode.setParent(glNode);
                glNode.addChild(childNode);
            } else {
                childNode = createNode(child, origNodes.get(child), origNodes);
                glNode.addChild(childNode);
                childNode.setParent(glNode);
            }
        }
        return glNode;
    }

    private void createSkins() {
        for (Entry<String, Skin> skin : gltf.getSkins().entrySet()) {
            Skeleton skeleton = new Skeleton();
            skeleton.setName(skin.getKey());
            List<String> jointNames = skin.getValue().getJointNames();
            for (int i = 0; i < jointNames.size(); i++) {
                Joint joint = joints.get(jointNames.get(i));
                joint.setId(i);
                skeleton.addJoint(joint);
                if (jointRoots.contains(joint)) {
                    skeleton.addRootJoint(joint);
                }
            }
            FloatBuffer fb = data.getExtractedAccessorByteBuffer(skin.getValue().getInverseBindMatrices()).asFloatBuffer();
            for (Joint joint : skeleton.getJoints()) {
                joint.setInverseBindMatrix(GLTFUtils.loadMatrixFromBuffer(joint.getId(), fb));
            }
            Matrix4f bindShape = GLTFUtils.loadMatrixFromArray(skin.getValue().getBindShapeMatrix());
            skeleton.setBindShapeMatrix(bindShape);
            skeletons.put(skin.getKey(), skeleton);
        }
    }

    private void loadAnimation() {
        Animation result = null;
        if (gltf.getAnimations() != null && !gltf.getAnimations().isEmpty()) {
            result = new Animation();
            for (de.javagl.jgltf.impl.Animation ani : gltf.getAnimations().values()) {
                Track track = new Track();
                track.setJointName(ani.getChannels().get(0).getTarget().getId());
                track.setJointId(joints.get(track.getJointName()).getId());
                track.setJoint(joints.get(track.getJointName()));
                String timeAccessorID = ani.getParameters().get("TIME");
                Accessor timeAccessor = gltf.getAccessors().get(timeAccessorID);
                FloatBuffer timeBuffer = data.getExtractedAccessorByteBuffer(timeAccessorID).asFloatBuffer();
                for (int i = 0; i < timeAccessor.getCount(); i++) {
                    Keyframe keyframe = new Keyframe();
                    keyframe.setTime(timeBuffer.get(i));
                    for (AnimationChannel c : ani.getChannels()) {
                        if (c.getTarget().getPath().equals("translation")) {
                            FloatBuffer buffer =
                                data.getExtractedAccessorByteBuffer(ani.getParameters().get("translation")).asFloatBuffer();
                            keyframe.setTranslation(GLTFUtils.loadVectorFromBuffer(buffer, i * 3));
                        }
                        if (c.getTarget().getPath().equals("scale")) {
                            FloatBuffer buffer = data.getExtractedAccessorByteBuffer(ani.getParameters().get("scale")).asFloatBuffer();
                            keyframe.setScaling(GLTFUtils.loadVectorFromBuffer(buffer, i * 3));
                        }
                        if (c.getTarget().getPath().equals("rotation")) {
                            FloatBuffer buffer = data.getExtractedAccessorByteBuffer(ani.getParameters().get("rotation")).asFloatBuffer();
                            keyframe.setOrientation(GLTFUtils.loadQuaternionFromBuffer(buffer, i * 4));
                        }
                    }
                    track.addKeyframe(keyframe);
                }
                result.addTrack(track);
            }
        }

        animation = result;
        if (!animation.getTracks().isEmpty()) {
            Track track = animation.getTracks().values().iterator().next();
            animation.setDuration(track.getKeyframes().get(track.getKeyframes().size() - 1).getTime());
            animation.setFrameCount(track.getKeyframes().size());
        }
    }

    private List<RawModel> loadModel() {
        List<RawModel> result = new LinkedList<>();
        for (Mesh mesh : gltf.getMeshes().values()) {
            RawModel model = null;
            for (MeshPrimitive p : mesh.getPrimitives()) {
                String indicesAccessor = p.getIndices();
                String positionsAccessor = p.getAttributes().get("POSITION");
                String normalsAccessor = p.getAttributes().get("NORMAL");
                String texCoordAccessor = p.getAttributes().get("TEXCOORD_0");
                String jointsAccessor = p.getAttributes().get("JOINT");
                String weigthsAccessor = p.getAttributes().get("WEIGHT");
                FloatBuffer texCoords = null;
                if (texCoordAccessor != null) {
                    texCoords = GLTFUtils.getFloatBuffer(texCoordAccessor, data);
                } else {
                    texCoords = ByteBuffer.allocateDirect(4 * 4).asFloatBuffer();
                    texCoords.put(new float[] { 0, 0, 1, 1 });
                    texCoords.flip();
                }

                if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null && weigthsAccessor == null) {
                    model = Loader.loadToVAO(GLTFUtils.getFloatBuffer(positionsAccessor, data), texCoords,
                        GLTFUtils.getFloatBuffer(normalsAccessor, data), GLTFUtils.getFloatBuffer(positionsAccessor, data),
                        GLTFUtils.getIndicesIntArray(indicesAccessor, data));

                } else if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null
                    && weigthsAccessor != null && jointsAccessor != null) {
                    model = Loader.loadToVAO(GLTFUtils.getFloatBuffer(positionsAccessor, data), texCoords,
                        GLTFUtils.getFloatBuffer(normalsAccessor, data), GLTFUtils.getIndicesIntArray(indicesAccessor, data),
                        GLTFUtils.getJointIntArray(jointsAccessor, data), GLTFUtils.getFloatBuffer(weigthsAccessor, data));
                }
            }
            result.add(model);
        }
        return result;
    }
}
