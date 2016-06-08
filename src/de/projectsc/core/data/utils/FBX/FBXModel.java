/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.FBX;


public class FBXModel {

    // private static final Log LOGGER = LogFactory.getLog(GLTFModel.class);
    //
    // private String name;
    //
    // private JUMIScene data;
    //
    // private Map<String, Joint> joints = new HashMap<>();
    //
    // private Map<String, Skeleton> skeletons = new HashMap<>();
    //
    // private Map<String, GLTFNode> nodes = new HashMap<>();
    //
    // private List<JUMIBone> roots = new LinkedList<>();
    //
    // private List<Joint> jointRoots = new LinkedList<>();
    //
    // private Animation animation;
    //
    // private List<TexturedModel> loadedModels = new LinkedList<>();
    //
    // public static void main(String[] args) {
    // new FBXModel().loadFBX("dragon.fbx");
    // }
    //
    // public FBXModel loadFBX(String filename) {
    // this.setName(filename.substring(0, filename.lastIndexOf(".")));
    // try {
    // data = FBXLoader.importModel(GLTFModel.class.getResourceAsStream("/models/animated/" + filename), filename);
    // } catch (IOException e) {
    // LOGGER.error("Could not load fbx file: " + filename, e);
    // return null;
    // }
    // createJoints();
    // createSkins();
    // // loadAnimation();
    // data.getAllMeshes()[0].getEdges();
    //
    // List<RawModel> modelList = loadModelNodes();
    // LOGGER.debug(String.format("Loaded new GLTF File %s with properties: ", filename));
    // LOGGER.debug("Nodes: " + nodes.size());
    // LOGGER.debug("Root Nodes: " + roots.size());
    //
    // LOGGER.debug("Joints: " + joints.size());
    // for (Joint j : joints.values()) {
    // LOGGER.debug("Joint " + j.getName() + ": Id= " + j.getId() + " LocalMatrix \n" + j.getLocalMatrix() + " WorldMatrix\n "
    // + j.getWorldMatrix() + "  Children: " + j.getChildren());
    // }
    // LOGGER.debug("Root Joints: " + jointRoots.size());
    //
    // LOGGER.debug("Skeletons: " + skeletons.size());
    // if (skeletons.size() > 0) {
    // Skeleton first = skeletons.values().iterator().next();
    // LOGGER.debug("First Skeleton Matrix: \n" + first.getBindShapeMatrix());
    // LOGGER.debug("First Skeleton Joints Size: " + first.getJoints().size());
    // }
    // if (animation != null) {
    // LOGGER.debug("Animation frame count: " + animation.getFrameCount());
    // LOGGER.debug("Animation duration: " + animation.getDuration());
    // LOGGER.debug("Animation tracks size: " + animation.getTracks().size());
    // }
    // for (RawModel model : modelList) {
    // ModelTexture texture = null;
    // if (textures.get(model) != null) {
    // texture = new ModelTexture(textures.get(model));
    // } else {
    // texture = new ModelTexture(Loader.loadTexture(GUIConstants.BASIC_TEXTURE_WHITE));
    // }
    // if (animation != null) {
    // AnimationController controller = new AnimationController();
    // animation.setSkeleton(skeletons.values().iterator().next()); // improve
    // controller.setAnimation(animation);
    // controller.setId("gltf");
    // loadedModels.add(new AnimatedModel(model, controller, texture));
    // } else {
    // TexturedModel newModel = new TexturedModel(model, texture);
    // newModel.setModelMatrix(Maths.getYUpMatrix());
    // loadedModels.add(newModel);
    // }
    // }
    // return this;
    // }
    //
    // private void createJoints() {
    // for (JUMIMesh mesh : data.getAllMeshes()) {
    // roots.add(mesh.rootBone);
    // }
    // for (JUMIBone node : roots) {
    // createJointNode(node, null);
    // }
    // }
    //
    // private Joint createJointNode(JUMIBone node, Joint parent) {
    // Joint joint = null;
    // if (joints.get(node.getName()) == null) {
    // joint = new Joint();
    // joint.setName(node.getName());
    // Matrix4f localMatrix = Maths.createTransformationMatrix(
    // Maths.createQuaternionFromEuler(node.getLocalRotation().x, node.getLocalRotation().y, node.getLocalRotation().z),
    // new Vector3f(node.getLocalTranslation().x, node.getLocalTranslation().y, node.getLocalTranslation().z),
    // new Vector3f(node.getLocalScaling().x, node.getLocalScaling().y, node.getLocalScaling().z));
    // joint.setLocalMatrix(localMatrix);
    // joints.put(joint.getName(), joint);
    // if (parent != null) {
    // joint.setParent(parent);
    // } else {
    // jointRoots.add(joint);
    // joint.setParentMatrix(new Matrix4f());
    // }
    // if (node.getDescendants() != null) {
    // for (JUMIBone child : node.getDescendants()) {
    // Joint childJoint = createJointNode(child, joint);
    // if (joint != null) {
    // joint.addChild(childJoint);
    // }
    // }
    // }
    // }
    // return joint;
    // }
    //
    // private void createSkins() {
    // for (Entry<String, Skin> skin : gltf.getSkins().entrySet()) {
    // Skeleton skeleton = new Skeleton();
    // skeleton.setName(skin.getKey());
    // List<String> jointNames = skin.getValue().getJointNames();
    // for (int i = 0; i < jointNames.size(); i++) {
    // Joint joint = joints.get(jointNames.get(i));
    // joint.setId(i);
    // skeleton.addJoint(joint);
    // if (jointRoots.contains(joint)) {
    // skeleton.addRootJoint(joint);
    // }
    // }
    // FloatBuffer fb = data.getExtractedAccessorByteBuffer(skin.getValue().getInverseBindMatrices()).asFloatBuffer();
    // for (Joint joint : skeleton.getJoints()) {
    // joint.setInverseBindMatrix(GLTFUtils.loadMatrixFromBuffer(joint.getId(), fb));
    // }
    // Matrix4f bindShape = GLTFUtils.loadMatrixFromArray(skin.getValue().getBindShapeMatrix());
    // skeleton.setBindShapeMatrix(bindShape);
    // skeletons.put(skin.getKey(), skeleton);
    // }
    // }
    //
    // private void loadAnimation() {
    // Animation result = null;
    // if (gltf.getAnimations() != null && !gltf.getAnimations().isEmpty()) {
    // result = new Animation();
    // for (de.javagl.jgltf.impl.Animation ani : gltf.getAnimations().values()) {
    // Track track = new Track();
    // track.setJointName(ani.getChannels().get(0).getTarget().getId());
    // track.setJointId(joints.get(track.getJointName()).getId());
    // String timeAccessorID = ani.getParameters().get("TIME");
    // Accessor timeAccessor = gltf.getAccessors().get(timeAccessorID);
    // FloatBuffer timeBuffer = data.getExtractedAccessorByteBuffer(timeAccessorID).asFloatBuffer();
    // for (int i = 0; i < timeAccessor.getCount(); i++) {
    // Keyframe keyframe = new Keyframe();
    // keyframe.setTime(timeBuffer.get(i));
    // for (AnimationChannel c : ani.getChannels()) {
    // if (c.getTarget().getPath().equals("translation")) {
    // FloatBuffer buffer =
    // data.getExtractedAccessorByteBuffer(ani.getParameters().get("translation")).asFloatBuffer();
    // keyframe.setTranslation(GLTFUtils.loadVectorFromBuffer(buffer, i));
    // }
    // if (c.getTarget().getPath().equals("scale")) {
    // FloatBuffer buffer = data.getExtractedAccessorByteBuffer(ani.getParameters().get("scale")).asFloatBuffer();
    // keyframe.setScaling(GLTFUtils.loadVectorFromBuffer(buffer, i));
    // }
    // if (c.getTarget().getPath().equals("rotation")) {
    // FloatBuffer buffer = data.getExtractedAccessorByteBuffer(ani.getParameters().get("rotation")).asFloatBuffer();
    // keyframe.setOrientation(GLTFUtils.loadQuaternionFromBuffer(buffer, i));
    // }
    // }
    // track.addKeyframe(keyframe);
    // }
    // result.addTrack(track);
    // }
    // }
    // if (result != null) {
    // animation = result;
    // if (!animation.getTracks().isEmpty()) {
    // Track track = animation.getTracks().values().iterator().next();
    // animation.setDuration(track.getKeyframes().get(track.getKeyframes().size() - 1).getTime());
    // animation.setFrameCount(track.getKeyframes().size());
    // }
    // }
    // }
    //
    // private List<RawModel> loadModelNodes() {
    // List<RawModel> result = new LinkedList<>();
    // for (GLTFNode root : roots) {
    // checkForMesh(root, result);
    // }
    // return result;
    // }
    //
    // private void checkForMesh(GLTFNode node, List<RawModel> result) {
    // List<String> meshes = node.getNode().getMeshes();
    // if (meshes != null && !meshes.isEmpty()) {
    // loadModel(node, result);
    // }
    // if (node.getChildNodes().size() > 0) {
    // for (GLTFNode child : node.getChildNodes().values()) {
    // checkForMesh(child, result);
    // }
    // }
    // }
    //
    // private void loadModel(GLTFNode node, List<RawModel> result) {
    // for (String meshName : node.getNode().getMeshes()) {
    // Mesh mesh = gltf.getMeshes().get(meshName);
    // RawModel model = null;
    // for (MeshPrimitive p : mesh.getPrimitives()) {
    // String indicesAccessor = p.getIndices();
    // String positionsAccessor = p.getAttributes().get("POSITION");
    // String normalsAccessor = p.getAttributes().get("NORMAL");
    // String texCoordAccessor = p.getAttributes().get("TEXCOORD_0");
    // String jointsAccessor = p.getAttributes().get("JOINT");
    // String weigthsAccessor = p.getAttributes().get("WEIGHT");
    // FloatBuffer texCoords = null;
    // if (texCoordAccessor != null) {
    // texCoords = GLTFUtils.getFloatBuffer(texCoordAccessor, data);
    // } else {
    // texCoords = ByteBuffer.allocateDirect(4 * 4).asFloatBuffer();
    // texCoords.put(new float[] { 0, 0, 1, 1 });
    // texCoords.flip();
    // }
    //
    // if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null && weigthsAccessor == null) {
    // model = Loader.loadToVAO(GLTFUtils.getFloatBuffer(positionsAccessor, data), texCoords,
    // GLTFUtils.getFloatBuffer(normalsAccessor, data), GLTFUtils.getFloatBuffer(positionsAccessor, data),
    // GLTFUtils.getIndicesIntArray(indicesAccessor, data));
    //
    // } else if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null
    // && weigthsAccessor != null && jointsAccessor != null) {
    // model = Loader.loadToVAO(GLTFUtils.getFloatBuffer(positionsAccessor, data), texCoords,
    // GLTFUtils.getFloatBuffer(normalsAccessor, data), GLTFUtils.getIndicesIntArray(indicesAccessor, data),
    // GLTFUtils.getJointIntArray(jointsAccessor, data), GLTFUtils.getFloatBuffer(weigthsAccessor, data));
    // }
    // Material materialForMesh = gltf.getMaterials().get(p.getMaterial());
    // if (materialForMesh != null) {
    // Object diffuseTexture = materialForMesh.getValues().get("diffuse");
    // if (diffuseTexture instanceof String) {
    // Texture texture = gltf.getTextures().get(diffuseTexture);
    // String fileType = "PNG";
    // if (((String) diffuseTexture).toLowerCase().contains("jpg")
    // || ((String) diffuseTexture).toLowerCase().contains("jpeg")) {
    // fileType = "jpg";
    // }
    // if (texture != null) {
    // BufferedImage bufferedTexture = data.getImageAsBufferedImage(texture.getSource());
    // int textureID = Loader.loadTexture(bufferedTexture, name + mesh.getName() + texture.getName(), fileType);
    // textures.put(model, textureID);
    // }
    // }
    // }
    // }
    // result.add(model);
    // }
    // }
    //
    // private Map<RawModel, Integer> textures = new HashMap<>();
    //
    // public String getName() {
    // return name;
    // }
    //
    // public void setName(String name) {
    // this.name = name;
    // }
    //
    // public List<TexturedModel> getLoadedModels() {
    // return loadedModels;
    // }
    //
    // public void setLoadedModels(List<TexturedModel> loadedModels) {
    // this.loadedModels = loadedModels;
    // }

}
