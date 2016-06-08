/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Matrix4f;

import de.javagl.jgltf.impl.Accessor;
import de.javagl.jgltf.impl.AnimationChannel;
import de.javagl.jgltf.impl.GlTF;
import de.javagl.jgltf.impl.Material;
import de.javagl.jgltf.impl.Mesh;
import de.javagl.jgltf.impl.MeshPrimitive;
import de.javagl.jgltf.impl.Node;
import de.javagl.jgltf.impl.Skin;
import de.javagl.jgltf.impl.Texture;
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

public class GLTFModel {

    private static final Log LOGGER = LogFactory.getLog(GLTFModel.class);

    private String name;

    private GltfData data;

    private GlTF gltf;

    private Map<String, Joint> joints = new HashMap<>();

    private Map<String, Skeleton> skeletons = new HashMap<>();

    private Map<String, GLTFNode> nodes = new HashMap<>();

    private List<GLTFNode> roots = new LinkedList<>();

    private List<Joint> jointRoots = new LinkedList<>();

    private Animation animation;

    private List<TexturedModel> loadedModels = new LinkedList<>();

    public GLTFModel loadGLTF(String filename) {
        this.setName(filename.substring(0, filename.lastIndexOf(".")));
        try {
            data = GltfDataLoader.load(GLTFModel.class.getResource("/models/animated/" + filename).toURI(), null);
            gltf = data.getGltf();
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Could not load gltf file: " + filename, e);
            return this;
        }
        createNodes();
        createJoints();
        createSkins();
        loadAnimation();

        List<RawModel> modelList = loadModelNodes();
        LOGGER.debug(String.format("Loaded new GLTF File %s with properties: ", filename));
        LOGGER.debug("Nodes: " + nodes.size());
        LOGGER.debug("Root Nodes: " + roots.size());

        LOGGER.debug("Joints: " + joints.size());
        for (Joint j : joints.values()) {
            LOGGER.debug("Joint " + j.getName() + ": Id= " + j.getId() + " LocalMatrix \n" + j.getLocalMatrix() + " WorldMatrix\n "
                + j.getWorldMatrix() + "  Children: " + j.getChildren());
        }
        LOGGER.debug("Root Joints: " + jointRoots.size());

        LOGGER.debug("Skeletons: " + skeletons.size());
        if (skeletons.size() > 0) {
            Skeleton first = skeletons.values().iterator().next();
            LOGGER.debug("First Skeleton Matrix: \n" + first.getBindShapeMatrix());
            LOGGER.debug("First Skeleton Joints Size: " + first.getJoints().size());
        }
        if (animation != null) {
            LOGGER.debug("Animation frame count: " + animation.getFrameCount());
            LOGGER.debug("Animation duration: " + animation.getDuration());
            LOGGER.debug("Animation tracks size: " + animation.getTracks().size());
        }
        for (RawModel model : modelList) {
            ModelTexture texture = null;
            if (textures.get(model) != null) {
                texture = new ModelTexture(textures.get(model));
            } else {
                int textureId =
                    Loader.loadTextureFromOtherResourceLocation("/models/animated/textures/"
                        + filename.substring(0, filename.lastIndexOf(".")) + ".png");
                if (textureId != -1) {
                    texture = new ModelTexture(textureId);
                    int normalId =
                        Loader.loadTexture("/models/animated/textures/" + filename.substring(0, filename.lastIndexOf(".")) + "_n.png");
                    if (normalId != -1) {
                        texture.setNormalMap(normalId);
                    }
                } else {
                    texture = new ModelTexture(Loader.loadTexture(GUIConstants.BASIC_TEXTURE_WHITE));
                }
            }
            if (animation != null) {
                AnimationController controller = new AnimationController();
                animation.setSkeleton(skeletons.values().iterator().next()); // improve
                controller.setAnimation(animation);
                controller.setId("gltf");
                loadedModels.add(new AnimatedModel(model, controller, texture));
            } else {
                TexturedModel newModel = new TexturedModel(model, texture);
                newModel.setModelMatrix(Maths.getYUpMatrix());
                loadedModels.add(newModel);
            }
        }
        return this;
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
                            keyframe.setTranslation(GLTFUtils.loadVectorFromBuffer(buffer, i));
                        }
                        if (c.getTarget().getPath().equals("scale")) {
                            FloatBuffer buffer = data.getExtractedAccessorByteBuffer(ani.getParameters().get("scale")).asFloatBuffer();
                            keyframe.setScaling(GLTFUtils.loadVectorFromBuffer(buffer, i));
                        }
                        if (c.getTarget().getPath().equals("rotation")) {
                            FloatBuffer buffer = data.getExtractedAccessorByteBuffer(ani.getParameters().get("rotation")).asFloatBuffer();
                            keyframe.setOrientation(GLTFUtils.loadQuaternionFromBuffer(buffer, i));
                        }
                    }
                    track.addKeyframe(keyframe);
                }
                result.addTrack(track);
            }
        }
        if (result != null) {
            animation = result;
            if (!animation.getTracks().isEmpty()) {
                Track track = animation.getTracks().values().iterator().next();
                animation.setDuration(track.getKeyframes().get(track.getKeyframes().size() - 1).getTime());
                animation.setFrameCount(track.getKeyframes().size());
            }
        }
    }

    private List<RawModel> loadModelNodes() {
        List<RawModel> result = new LinkedList<>();
        for (GLTFNode root : roots) {
            checkForMesh(root, result);
        }
        return result;
    }

    private void checkForMesh(GLTFNode node, List<RawModel> result) {
        List<String> meshes = node.getNode().getMeshes();
        if (meshes != null && !meshes.isEmpty()) {
            loadModel(node, result);
        }
        if (node.getChildNodes().size() > 0) {
            for (GLTFNode child : node.getChildNodes().values()) {
                checkForMesh(child, result);
            }
        }
    }

    private void loadModel(GLTFNode node, List<RawModel> result) {
        for (String meshName : node.getNode().getMeshes()) {
            Mesh mesh = gltf.getMeshes().get(meshName);
            for (MeshPrimitive p : mesh.getPrimitives()) {
                RawModel model = null;
                String indicesAccessor = p.getIndices();
                String positionsAccessor = p.getAttributes().get("POSITION");
                String normalsAccessor = p.getAttributes().get("NORMAL");
                String texCoordAccessor = p.getAttributes().get("TEXCOORD_0");
                String jointsAccessor = p.getAttributes().get("JOINT");
                String weigthsAccessor = p.getAttributes().get("WEIGHT");

                model =
                    Loader.loadToVAO(GLTFUtils.getFloatBuffer(positionsAccessor, data),
                        GLTFUtils.getFloatBuffer(texCoordAccessor, data),
                        GLTFUtils.getFloatBuffer(normalsAccessor, data), null, GLTFUtils.getIndicesIntArray(indicesAccessor, data),
                        GLTFUtils.getJointIntArray(jointsAccessor, data), GLTFUtils.getFloatBuffer(weigthsAccessor, data));
                if (model.hasTexture()) {
                    Material materialForMesh = gltf.getMaterials().get(p.getMaterial());
                    if (materialForMesh != null) {
                        Object diffuseTexture = materialForMesh.getValues().get("diffuse");
                        if (diffuseTexture instanceof String) {
                            Texture texture = gltf.getTextures().get(diffuseTexture);
                            String fileType = "PNG";
                            if (((String) diffuseTexture).toLowerCase().contains("jpg")
                                || ((String) diffuseTexture).toLowerCase().contains("jpeg")) {
                                fileType = "jpg";
                            }
                            if (texture != null) {
                                BufferedImage bufferedTexture = data.getImageAsBufferedImage(texture.getSource());
                                int textureID = Loader.loadTexture(bufferedTexture, name + mesh.getName() + texture.getName(), fileType);
                                textures.put(model, textureID);
                            }
                        }
                    }
                }
                result.add(model);
            }
        }
    }

    private Map<RawModel, Integer> textures = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TexturedModel> getLoadedModels() {
        return loadedModels;
    }

    public void setLoadedModels(List<TexturedModel> loadedModels) {
        this.loadedModels = loadedModels;
    }

}
