/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.manager.EntityManager;

/**
 * Loader for the map.
 * 
 * @author Josch Bosch
 */
public final class MapLoader {

    protected static final Log LOGGER = LogFactory.getLog(MapLoader.class);

    private MapLoader() {

    }

    /**
     * load the map.
     * 
     * @param mapName to load
     * @param entityManager for adding entities
     */
    @SuppressWarnings("unchecked")
    public static void loadMap(String mapName, EntityManager entityManager) {
        LOGGER.debug("Start loading map " + mapName);
        InputStream mapStream = MapLoader.class.getResourceAsStream("/level/" + mapName);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> content;
        try {
            content = mapper.readValue(mapStream, new HashMap<String, Object>().getClass());
            loadEntites(entityManager, (Map<String, Object>) content.get("Entities"));
            LOGGER.debug("Loading map done.");
        } catch (IOException e) {
            LOGGER.error("Error loading map: ", e);
        }
    }

    @SuppressWarnings("unchecked")
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
