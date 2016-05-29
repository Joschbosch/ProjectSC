/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.util.vector.Matrix4f;

import de.javagl.jgltf.impl.GlTF;
import de.javagl.jgltf.impl.Mesh;
import de.javagl.jgltf.impl.MeshPrimitive;
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

    public static List<TexturedModel> loadGLTF(String filename) {
        Consumer consumer = null;
        List<TexturedModel> list = new LinkedList<>();
        try {
            GltfData result = GltfDataLoader.load(GLTFLoader.class.getResource("/models/animated/" + filename).toURI(), consumer);

            ModelTexture white =
                new ModelTexture(Loader.loadTexture(
                    GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale.png"), "PNG"));
            white.setNormalMap(Loader.loadTexture(
                GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale_n.png"), "PNG"));
            GlTF gltf = result.getGltf();
            RawModel model = null;
            for (Mesh mesh : gltf.getMeshes().values()) {
                for (MeshPrimitive p : mesh.getPrimitives()) {
                    String indicesAccessor = p.getIndices();
                    String positionsAccessor = p.getAttributes().get("POSITION");
                    String normalsAccessor = p.getAttributes().get("NORMAL");
                    String texCoordAccessor = p.getAttributes().get("TEXCOORD_1");
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
            if (!gltf.getSkins().isEmpty()) {
                for (Skin s : gltf.getSkins().values()) {
                    AnimatedModel animated = new AnimatedModel(model, readJointMatrices(s, result), null, white);
                    animated.setGltf(gltf);
                    list.add(animated);
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return list;

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
