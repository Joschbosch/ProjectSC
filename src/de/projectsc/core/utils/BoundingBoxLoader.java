/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.EntityType;
import de.projectsc.core.WorldEntity;

public class BoundingBoxLoader {

    private static final Log LOGGER = LogFactory.getLog(BoundingBoxLoader.class);

    private static Map<String, BoundingBox> boxes = new HashMap<>();

    public static BoundingBox readBoundingBox(WorldEntity entity) {
        if (entity.getType() != EntityType.BACKGROUND_OBJECT) {
            if (!boxes.containsKey(entity.getModel())) {
                try {
                    List<String> lines =
                        FileUtils.readLines(new File(WorldEntity.class.getResource("/meshes/" + entity.getModel() + ".obj").toURI()));
                    Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
                    Vector3f max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

                    for (String s : lines) {
                        if (s.startsWith("v ")) {
                            String[] split = s.split(" +");
                            float x = Float.parseFloat(split[1]);
                            float y = Float.parseFloat(split[2]);
                            float z = Float.parseFloat(split[3]);
                            if (min.x > x) {
                                min.x = x;
                            }
                            if (min.y > y) {
                                min.y = y;
                            }
                            if (min.z > z) {
                                min.z = z;
                            }
                            if (max.x < x) {
                                max.x = x;
                            }
                            if (max.y < y) {
                                max.y = y;
                            }
                            if (max.z < z) {
                                max.z = z;
                            }
                        }
                    }
                    min.scale(entity.getScale());
                    min.y = 0;
                    max.scale(entity.getScale());
                    BoundingBox box = new BoundingBox(min, max);
                    // LOGGER.debug("Read bounding box for " + model + ": min=" + min + " max =" + max +
                    // " center=" + box.getCenter() +
                    // " Size = "
                    // + box.getSize());
                    boxes.put(entity.getModel(), box);
                    return box;
                } catch (IOException | URISyntaxException e) {
                    LOGGER.error("Could not read bounding box: " + entity.getModel(), e);
                }
            } else {
                BoundingBox box = boxes.get(entity.getModel());
                Vector3f max = new Vector3f(box.getMax().x, box.getMax().y, box.getMax().z);
                Vector3f min = new Vector3f(box.getMin().x, box.getMin().y, box.getMin().z);

                return new BoundingBox(min, max);
            }
        }
        return null;
    }
}
