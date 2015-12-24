/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.manager.EntityManager;

public final class MapLoader {

    protected static final Log LOGGER = LogFactory.getLog(MapLoader.class);

    private MapLoader() {

    }

    public static void loadMap(String mapName, EntityManager entityManager) {
        LOGGER.debug("Start loading map " + mapName);
        try {
            File mapFolder = new File(MapLoader.class.getResource("/level/").toURI());
            File mapFile = new File(mapFolder, mapName);
            if (mapFile.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                @SuppressWarnings("unchecked") Map<String, Object> content =
                    mapper.readValue(mapFile, new HashMap<String, Object>().getClass());
                loadEntites(entityManager, (Map<String, Object>) content.get("Entities"));
            } else {
                LOGGER.debug("Could not load map. It does not exist.");
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.debug("Failed loading map " + mapName, e);

        }
        LOGGER.debug("Loading map done.");
    }

    private static void loadEntites(EntityManager entityManager, Map<String, Object> entities) {
        for (String uid : entities.keySet()) {
            Map<String, Object> entityValues = (Map<String, Object>) entities.get(uid);
            String newEntity = entityManager.createNewEntityFromSchema((int) entityValues.get("type"), uid);
            if (!newEntity.isEmpty()) {
                Transform t = entityManager.getEntity(newEntity).getTransform();
                t.parseTransformValues((Map<String, Map<String, Double>>) entityValues.get("transform"));
                LOGGER.debug(String.format("Added entity %s of type %s with transform %s", newEntity, entityManager.getEntity(newEntity)
                    .getEntityTypeId(), t));
            } else {
                LOGGER.error("Failed to load entity " + entityValues);
            }
        }
    }
}
