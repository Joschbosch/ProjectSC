/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.javagl.jgltf.impl.GlTF;
import de.javagl.jgltf.model.GltfData;
import de.javagl.jgltf.model.GltfDataLoader;
import de.projectsc.modes.client.gui.models.TexturedModel;

public class GLTFLoader {

    private static Log LOGGER = LogFactory.getLog(GLTFLoader.class);

    private GltfData data;

    private GlTF gltf;

    public List<TexturedModel> loadGLTF(String filename) {
        Consumer consumer = null;
        List<TexturedModel> list = new LinkedList<>();

        try {
            data = GltfDataLoader.load(GLTFLoader.class.getResource("/models/animated/" + filename).toURI(), consumer);
            gltf = data.getGltf();
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Could not load gltf file: " + filename, e);
        }

        
        
//        list.add(new AnimatedModel());
        return list;
    }

}
