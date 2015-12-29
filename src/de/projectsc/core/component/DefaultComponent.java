/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.Scene;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.manager.EventManager;

/**
 * 
 * Abstract class for entity components.
 * 
 * @author Josch Bosch
 */
public abstract class DefaultComponent implements Component {

    protected String componentId;

    protected String uid;

    protected ComponentType type;

    protected ObjectMapper mapper = new ObjectMapper();

    protected List<String> requiredComponents = new LinkedList<>();

    protected List<String> requiredBy = new LinkedList<>();

    protected Entity owner;

    private boolean isActive = false;

    private EventManager eventManager;

    public DefaultComponent() {
        createNewId();
    }

    public void setID(String id) {
        this.componentId = id;
    }

    public void setEventManager(EventManager manager) {
        this.eventManager = manager;
    }

    protected void fireEvent(Event e) {
        eventManager.fireEvent(e);
    }

    /**
     * @return true, if the component has all attributes to be saved.
     */
    @Override
    public abstract boolean isValidForSaving();

    /**
     * 
     * @return serialized string
     * @throws JsonGenerationException e
     * @throws JsonMappingException e
     * @throws IOException e
     */
    @Override
    public abstract Map<String, Object> serialize(File savingLocation);

    /**
     * 
     * @param input to deserialize
     * @param loadingLocation directory of schema
     * @throws JsonProcessingException e
     * @throws IOException e
     */
    @Override
    public abstract void deserialize(Map<String, Object> serialized, File loadingLocation);

    /**
     * Default handle event .
     * 
     * @param e to handle
     */
    public void handleEvent(EntityEvent e) {

    }

    @Override
    public String serializeForNetwork() {
        return "";
    }

    @Override
    public void deserializeFromNetwork(String serialized) {}

    /**
     * Removes a component from the owner.
     * 
     */
    public void remove() {

    }

    public Entity getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    @Override
    public List<String> getRequiredComponents() {
        return requiredComponents;
    }

    @Override
    public void addRequiredByComponent(String componentName) {
        requiredBy.add(componentName);
    }

    @Override
    public void removeRequiredByComponent(String componentName) {
        requiredBy.remove(componentName);
    }

    @Override
    public List<String> getRequiredBy() {
        return requiredBy;
    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    @Override
    public String getComponentName() {
        return componentId;

    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public void addSceneInformation(Scene scene) {

    }

    @Override
    public String getId() {
        return uid;
    }

    @Override
    public void createNewId() {
        uid = UUID.randomUUID().toString();
    }

}
