/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.entities;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.core.data.Event;

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

    protected Entity owner;

    protected List<String> requiredComponents = new LinkedList<>();

    public Component(String newName, Entity owner) {
        this.componentName = newName;
        this.owner = owner;
    }

    /**
     * Update method for the component.
     * 
     * @param ownerEntity entity that has this component.
     */
    public abstract void update(Entity ownerEntity);

    /**
     * @return true, if the component has all attributes to be saved.
     */
    public abstract boolean isValidForSaving();

    /**
     * 
     * @return serialized string
     * @throws JsonGenerationException e
     * @throws JsonMappingException e
     * @throws IOException e
     */
    public abstract String serialize() throws JsonGenerationException, JsonMappingException, IOException;

    /**
     * 
     * @param input to deserialize
     * @param schemaDir directory of schema
     * @throws JsonProcessingException e
     * @throws IOException e
     */
    public abstract void deserialize(JsonNode input, File schemaDir) throws JsonProcessingException, IOException;

    public void addRequiredComponents(String componentName) {

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

    /**
     * Removes a component from the owner.
     * 
     */
    public void remove() {

    }

    public void processEvent(Event e) {

    }

}
