/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.components;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.core.entities.Entity;

/**
 * 
 * Abstract class for entity components.
 * 
 * @author Josch Bosch
 */
public abstract class Component {

    protected String componentName;

    protected ComponentType type;

    protected ObjectMapper mapper = new ObjectMapper();

    public Component(String newName) {
        this.componentName = newName;
    }

    public abstract void update(Entity owner);

    public abstract String serialize() throws JsonGenerationException, JsonMappingException, IOException;

    public abstract void deserialize(JsonNode input) throws JsonProcessingException, IOException;

    public void receiveMessage(ComponentMessage message) {

    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public String getComponentName() {
        return componentName;
    }

}
