/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.entities;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.core.EventManager;
import de.projectsc.core.data.Event;

/**
 * 
 * Abstract class for entity components.
 * 
 * @author Josch Bosch
 */
public abstract class Component {

    protected String componentId;

    protected ComponentType type;

    protected ObjectMapper mapper = new ObjectMapper();

    protected List<String> requiredComponents = new LinkedList<>();

    protected List<String> requiredBy = new LinkedList<>();

    private long owner;

    public void setID(String id) {
        this.componentId = id;
    }

    protected void fireEvent(Event e) {
        EventManager.fireEvent(e);
    }

    /**
     * Update method for the component.
     * 
     * @param ownerEntity entity that has this component.
     */
    public abstract void update(long ownerEntity);

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
    public abstract Map<String, Object> serialize(File savingLocation);

    /**
     * 
     * @param input to deserialize
     * @param loadingLocation directory of schema
     * @throws JsonProcessingException e
     * @throws IOException e
     */
    public abstract void deserialize(Map<String, Object> serialized, File loadingLocation);

    public void handleEvent(Event e) {

    }

    /**
     * Removes a component from the owner.
     * 
     */
    public void remove() {

    }

    public void processEvent(Event e) {

    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public List<String> getRequiredComponents() {
        return requiredComponents;
    }

    public void addRequiredByComponent(String componentName) {
        requiredBy.add(componentName);
    }

    public void removeRequiredByComponent(String componentName) {
        requiredBy.remove(componentName);
    }

    public List<String> getRequiredBy() {
        return requiredBy;
    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public String getComponentName() {
        return componentId;

    }

}
