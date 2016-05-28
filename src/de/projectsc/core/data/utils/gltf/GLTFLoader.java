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

import de.javagl.jgltf.impl.Mesh;
import de.javagl.jgltf.impl.MeshPrimitive;
import de.javagl.jgltf.model.GltfData;
import de.javagl.jgltf.model.GltfDataLoader;
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
            for (Mesh mesh : result.getGltf().getMeshes().values()) {
                for (MeshPrimitive p : mesh.getPrimitives()) {
                    String indicesAccessor = p.getIndices();
                    String positionsAccessor = p.getAttributes().get("POSITION");
                    String normalsAccessor = p.getAttributes().get("NORMAL");
                    String texCoordAccessor = p.getAttributes().get("TEXCOORD_1");
                    String jointsAccessor = p.getAttributes().get("JOINT");
                    String weigthsAccessor = p.getAttributes().get("WEIGHT");
                    if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null && texCoordAccessor != null) {
                        RawModel model =
                            Loader.loadToVAO(getFloatBuffer(positionsAccessor, result), getFloatBuffer(texCoordAccessor, result),
                                getFloatBuffer(normalsAccessor, result), getFloatBuffer(positionsAccessor, result),
                                getIntBuffer(indicesAccessor, result));
                        ModelTexture white =
                            new ModelTexture(Loader.loadTexture(
                                GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale.png"), "PNG"));
                        white.setNormalMap(Loader.loadTexture(
                            GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale_n.png"), "PNG"));
                        TexturedModel texturedModel = new TexturedModel(model, white);
                        list.add(texturedModel);
                    } else if (indicesAccessor != null && positionsAccessor != null && normalsAccessor != null && texCoordAccessor != null
                        && weigthsAccessor != null && jointsAccessor != null) {
                        RawModel model =
                            Loader.loadToVAO(getFloatBuffer(positionsAccessor, result), getFloatBuffer(texCoordAccessor, result),
                                getFloatBuffer(normalsAccessor, result), getIntBuffer(indicesAccessor, result),
                                getIntBuffer(jointsAccessor, result), getFloatBuffer(weigthsAccessor, result));
                        ModelTexture white =
                            new ModelTexture(Loader.loadTexture(
                                GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale.png"), "PNG"));
                        white.setNormalMap(Loader.loadTexture(
                            GLTFLoader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + "dragon/dragon_scale_n.png"), "PNG"));
                        TexturedModel texturedModel = new TexturedModel(model, white);
                        list.add(texturedModel);
                    }

                }
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return list;

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
