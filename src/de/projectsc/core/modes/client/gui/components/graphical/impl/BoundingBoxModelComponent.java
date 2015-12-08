/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.components.graphical.impl;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;

import de.projectsc.core.data.entities.Entity;
import de.projectsc.core.data.entities.components.physic.BoundingComponent;
import de.projectsc.core.modes.client.gui.components.GraphicalComponent;
import de.projectsc.core.modes.client.gui.data.Scene;

public class BoundingBoxModelComponent extends GraphicalComponent {

    /**
     * Name.
     */
    public static final String NAME = "Bounding box model component";

    public BoundingBoxModelComponent(Entity owner) {
        super(NAME, owner);
        addRequiredComponents(BoundingComponent.NAME);
    }

    @Override
    public void render(Scene scene) {}

    @Override
    public void update(Entity ownerEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isValidForSaving() {
        return false;
    }

    @Override
    public String serialize() throws JsonGenerationException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public void deserialize(JsonNode input, File schemaDir) throws JsonProcessingException, IOException {

    }
}
