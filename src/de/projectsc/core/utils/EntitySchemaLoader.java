/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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
import de.projectsc.editor.MapEditorGraphicsCore;

public class EntitySchemaLoader {

    private static final Log LOGGER = LogFactory.getLog(EntitySchemaLoader.class);

    public static EntitySchema loadEntitySchema(long id, Entity entity, ComponentManager componentManager) {
        try {
            File schemaRoot =
                new File(MapEditorGraphicsCore.class.getResource(
                    CoreConstants.SCHEME_DIRECTORY_NAME).toURI());
            File schemaFolder = new File(schemaRoot, CoreConstants.SCHEME_DIRECTORY_PREFIX + id);
            if (schemaFolder.exists()) {
                EntitySchema newSchema = new EntitySchema(Integer.parseInt(schemaFolder.getName().substring(1)));
                ObjectMapper mapper = new ObjectMapper();
                File entityFile = new File(schemaFolder, CoreConstants.ENTITY_FILENAME);
                JsonNode tree = mapper.readTree(entityFile);
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
            } else {
                LOGGER.error("Schema folder does not exist: " + schemaFolder.getAbsolutePath());
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error("Failed to load entity schema: ", e);
        }
        return null;
    }
}
