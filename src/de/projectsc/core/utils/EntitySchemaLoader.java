/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.editor.EntitySchema;

/**
 * Loader for the entity schemas.
 * 
 * @author Josch Bosch
 */
public final class EntitySchemaLoader {

    private static final Log LOGGER = LogFactory.getLog(EntitySchemaLoader.class);

    private EntitySchemaLoader() {

    }

    /**
     * Load a schema for the given entity.
     * 
     * @param id to load
     * @param entity to load to
     * @param componentManager to load components
     * @return the schema
     */
    public static EntitySchema loadEntitySchema(long id, Entity entity, ComponentManager componentManager) {
        try {
            if (id != 0) {
                String schemaFolder = CoreConstants.SCHEME_DIRECTORY_NAME + "/" + CoreConstants.SCHEME_DIRECTORY_PREFIX + id;
                InputStream entityFile = EntitySchemaLoader.class.getResourceAsStream(schemaFolder + "/" + CoreConstants.ENTITY_FILENAME);
                EntitySchema newSchema = new EntitySchema(id);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode tree;
                tree = mapper.readTree(entityFile);
                Iterator<String> componentNamesIterator = tree.get("components").getFieldNames();
                while (componentNamesIterator.hasNext()) {
                    String name = componentNamesIterator.next();
                    @SuppressWarnings("unchecked") Map<String, Object> serialized =
                        mapper.readValue(tree.get("components").get(name), new HashMap<String, Object>().getClass());
                    Component c = componentManager.createComponent(name);
                    if (c != null) {
                        c.setOwner(entity);
                        c.deserialize(serialized, schemaFolder);
                        newSchema.getComponents().add(c);
                    }
                }
                return newSchema;
            }
        } catch (IOException e) {
            LOGGER.error("Could not load schema file: ", e);
        }
        return null;
    }
}
