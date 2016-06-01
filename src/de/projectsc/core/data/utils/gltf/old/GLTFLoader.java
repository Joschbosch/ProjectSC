/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf.old;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import de.javagl.jgltf.impl.Scene;
import de.javagl.jgltf.impl.Skin;
import de.javagl.jgltf.model.GltfData;
import de.javagl.jgltf.model.GltfDataLoader;
import de.projectsc.core.data.animation.AnimatedFrame;
import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.AnimatedModel;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.textures.ModelTexture;
import de.projectsc.modes.client.gui.utils.GUIConstants;
import de.projectsc.modes.client.gui.utils.Loader;

public class GLTFLoader {

    private static GltfData data;

    private static Map<String, GLTFNode> nodes;

    private static Map<String, Joint> joints;

    private static Map<String, Joint> rootJoints = new HashMap<>();

    private static Map<String, GLTFSkeleton> skeletons = new HashMap<>();

    private static Matrix4f[] invBindPoseMatrices = new Matrix4f[200];

    private static GLTFAnimation animation;

    public static void main(String[] args) {
        loadGLTF("dragon.gltf");
    }

    public static synchronized List<TexturedModel> loadGLTF(String filename) {
        Consumer consumer = null;
        nodes = new HashMap<>();
        joints = new HashMap<>();
        animation = null;
        List<TexturedModel> list = new LinkedList<>();

        try {
            data = GltfDataLoader.load(GLTFLoader.class.getResource("/models/animated/" + filename).toURI(), consumer);
            GlTF gltf = data.getGltf();
            RawModel model = loadModel(data, gltf);
            createNodes();
            createSkins();
            loadAnimations();
            createScene();
            List<AnimatedFrame> frames = new LinkedList<>();
            for (int i = 0; i < animation.getFrameCount(); i++) {
                frames.add(processAnimationFrame(i));
            }
            List<Matrix4f> inverse = new ArrayList<>();
            for (int i = 0; i < invBindPoseMatrices.length; i++) {
                if (invBindPoseMatrices[i] != null) {
                    inverse.add(invBindPoseMatrices[i]);
                }
            }
            ModelTexture white =
                new ModelTexture(Loader.loadTexture(
                    GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale.png"), "PNG"));
            white.setNormalMap(Loader.loadTexture(
                GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale_n.png"), "PNG"));
            AnimatedModel animated = new AnimatedModel(model, inverse, frames, white);
            // TexturedModel animated = new TexturedModel(model, white);

            list.add(animated);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return list;

    }

    private static void createScene() {
        Scene scene = data.getGltf().getScenes().get(data.getGltf().getScene());
        List<Node> localNodes = new LinkedList<>();
        for (String nodeName : scene.getNodes()){
            if (nodes.get(nodeName)!= null){
                if (!(nodes.get(nodeName) instanceof Joint)){
                    localNodes.add(nodes.get(nodeName));
                }
                
            }
        }
    }

    private static AnimatedFrame processAnimationFrame(int i) {
        AnimatedFrame result = new AnimatedFrame();
        for (Joint joint : rootJoints.values()) {
            Matrix4f parentMatrix = getParentMatrix(joint.getParent());
            processJoint(i, joint, result, parentMatrix);
        }
        return result;
    }

    private static Matrix4f getParentMatrix(GLTFNode node) {
        System.out.println("GETTING PARENT: " + node.getName());
        Matrix4f parent = null;
        if (node.getParent() != null) {
            parent = getParentMatrix(node.getParent());
        }

        Matrix4f own = new Matrix4f();
        if (node.getMatrix() != null) {
            own = loadMatrixFromArray(node.getMatrix());
        } else if (node.getTranslation() != null) {
            Vector3f translation = new Vector3f(node.getTranslation()[0], node.getTranslation()[1], node.getTranslation()[2]);
            Quaternion rotation =
                new Quaternion(node.getRotation()[0], node.getRotation()[1], node.getRotation()[2], node.getRotation()[3]);
            Vector3f scale = new Vector3f(node.getScale()[0], node.getScale()[1], node.getScale()[2]);
            own = Maths.createTransformationMatrix(rotation, translation, scale);
        }
        if (parent != null) {
            return Matrix4f.mul(parent, own, null);
        } else {
            return own;
        }
    }

    private static void processJoint(int i, Joint joint, AnimatedFrame result, Matrix4f pathToRoot) {
        GLTFTrack track = animation.getTrackFromJoint(joint);
        GLTFKeyFrame frame = track.getKeyframes().get(i);

        Matrix4f localJointMatrix =
            Maths.createTransformationMatrix(joint.getRotationQuaternion(), joint.getPositionVector(), joint.getScaleVector());

        Matrix4f animationMatrix = Maths.createTransformationMatrix(frame.getOrientation(), frame.getTranslation(), frame.getScaling());
        Matrix4f invlocalJointMatrix = Matrix4f.invert(localJointMatrix, null);
        Matrix4f bindShapeMatrix = skeletons.values().iterator().next().getBindShapeMatrix();
        Matrix4f invBindShapeMatrix = Matrix4f.invert(bindShapeMatrix, null);
        // v += {[(v * BSM) * IBMi * JMi] * JW}

        // n: The number of joints that influence vertex v
        // BSM: Bind-shape matrix
        // IBMi: Inverse bind-pose matrix of joint i
        // JMi: Transformation matrix of joint i
        // JW: Weight of the influence of joint i on vertex v
        // Matrix4f.mul(localJointMatrix, animationMatrix, localJointMatrix);

        // Matrix4f.mul(localJointMatrix, animationMatrix, );

        Matrix4f jointsMatrix = new Matrix4f();
        Matrix4f.mul(pathToRoot, localJointMatrix, jointsMatrix);
        Matrix4f.mul(bindShapeMatrix, jointsMatrix, jointsMatrix);
        Matrix4f.mul(animationMatrix, jointsMatrix, jointsMatrix);
        result.setMatrix(joint.getId(), jointsMatrix, invBindPoseMatrices[joint.getId()]);

        if (!joint.getChildren().isEmpty()) {
            for (String child : joint.getChildren()) {
                Joint childJoint = joints.get(child);
                processJoint(i, childJoint, result, localJointMatrix);
            }
        }
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
        GLTFNode glNode = null;
        if (node.getJointName() != null) {
            glNode = new Joint();
            glNode.setName(node.getJointName());
            joints.put(node.getJointName(), (Joint) glNode);
        } else if (node.getCamera() != null) {
            return null;
        } else if (node.getMeshes() != null && !node.getMeshes().isEmpty()) {
            // ADD ALL MESHES
            glNode = new GLTFNode();
        } else if (node.getSkin() != null) {
            // get skins
            glNode = new GLTFNode();
        } else {
            glNode = new GLTFNode();
        }

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

        if (glNode.getMatrix() != null) {
            glNode.applyMatrix(loadMatrixFromArray(glNode.getMatrix()));
        } else if (glNode.getTranslation() != null) {
            glNode.setPosition(new Vector3f(node.getTranslation()[0], node.getTranslation()[1], node.getTranslation()[2]));
            glNode.setScale(new Vector3f(node.getScale()[0], node.getScale()[1], node.getScale()[2]));
            glNode.setRotation(new Quaternion(node.getRotation()[0], node.getRotation()[1], node.getRotation()[2], node.getRotation()[3]));
            glNode.applyMatrix(
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
            if (childNode instanceof Joint && !(glNode instanceof Joint)) {
                rootJoints.put(child, (Joint) childNode);
            }
        }
        return glNode;
    }

    private static void createSkins() {
        for (Entry<String, Skin> skin : data.getGltf().getSkins().entrySet()) {
            GLTFSkeleton skeleton = new GLTFSkeleton();
            skeleton.setName(skin.getKey());
            List<String> jointNames = skin.getValue().getJointNames();
            for (int i = 0; i < jointNames.size(); i++) {
                Joint joint = joints.get(jointNames.get(i));
                joint.setId(i);
                skeleton.addJoint(joint);
            }
            FloatBuffer fb = data.getExtractedAccessorByteBuffer(skin.getValue().getInverseBindMatrices()).asFloatBuffer();
            for (Joint joint : skeleton.getJoints()) {
                joint.setInverseBindMatrix(loadMatrixFromBuffer(joint.getId(), fb));
                invBindPoseMatrices[joint.getId()] = joint.getInverseBindMatrix();
            }
            Matrix4f bindShape = loadMatrixFromArray(skin.getValue().getBindShapeMatrix());
            skeleton.setBindShapeMatrix(bindShape);
            skeletons.put(skin.getKey(), skeleton);
        }
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
                FloatBuffer texCoords = ByteBuffer.allocateDirect(4 * 4).asFloatBuffer();
                texCoords.put(new float[] { 0, 0, 1, 1 });
                texCoords.flip();
                if (texCoordAccessor != null) {
                    getFloatBuffer(texCoordAccessor, result);
                }

                if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null && weigthsAccessor == null) {
                    model = Loader.loadToVAO(getFloatBuffer(positionsAccessor, result), texCoords,
                        getFloatBuffer(normalsAccessor, result), getFloatBuffer(positionsAccessor, result),
                        getIntBuffer(indicesAccessor, result));

                } else if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null
                    && weigthsAccessor != null && jointsAccessor != null) {
                    model = Loader.loadToVAO(getFloatBuffer(positionsAccessor, result), texCoords,
                        getFloatBuffer(normalsAccessor, result), getIntBuffer(indicesAccessor, result),
                        getIntJointBuffer(jointsAccessor, result), getFloatBuffer(weigthsAccessor, result));
                }
            }
        }
        return model;
    }

    private static void loadAnimations() {
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

        animation = result;
        if (!animation.getTracks().isEmpty()) {
            GLTFTrack track = animation.getTracks().values().iterator().next();
            animation.setDuration(track.getKeyframes().get(track.getKeyframes().size() - 1).getTime());
            animation.setFrameCount(track.getKeyframes().size());
        }
    }


   
}
