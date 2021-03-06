/*
 * Copyright (C) 2015
 */

package de.projectsc.core.interfaces;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Basic class for all components in the world.
 * 
 * @author Josch Bosch
 */
public interface Component {

    /**
     * 
     * @return a list of all required component's names
     */
    List<String> getRequiredComponents();

    /**
     * @return a list of all component's names that require the current component
     */
    List<String> getRequiredBy();

    /**
     * Add a component that requires the current component.
     * 
     * @param componentName that requires this component.
     */
    void addRequiredByComponent(String componentName);

    /**
     * Removes the component that requires this.
     * 
     * @param componentName to remove.
     */
    void removeRequiredByComponent(String componentName);

    /**
     * @param active if the component is current active (which can have different meanings in editor and game).
     */
    void setActive(boolean active);

    /**
     * @return true, if the component can be saved at the moment.
     */
    boolean isValidForEntitySaving();

    /**
     * Save component.
     * 
     * @param schemaFolder to save to
     * @return saving structure
     */
    Map<String, Object> serialize(File schemaFolder);

    /**
     * Load component.
     * 
     * @param serialized loaded structure
     * @param schemaFolder directory to load from
     */
    void deserialize(Map<String, Object> serialized, String schemaFolder);

    /**
     * Update the current component.
     * 
     * @param elapsed time since last update
     */
    // void update(long elapsed);

    /**
     * @return name of the component.
     */
    String getComponentName();

    /**
     * @return id of the component
     */
    String getId();

    /**
     * Creates a new ID for the component (e.g. after cloning).
     */
    void createNewId();

    /**
     * Set the owner of the component.
     * 
     * @param entity owner
     */
    void setOwner(Entity entity);

    /**
     * Create serialized string for the component to send to clients. Should be as small as possible.
     * 
     * @return serialized string
     */
    String serializeForNetwork();

    /**
     * Deserialize a string for this component.
     * 
     * @param serial to read
     */
    void deserializeFromNetwork(String serial);

    /**
     * Make a clone of the current component.
     * 
     * @return the cloned component.
     */
    Component cloneComponent();

    /**
     * Is the configuration for this component valid for saving.
     * 
     * @return true, if it is, false otherwise.
     */
    boolean configurationValid();

    /**
     * Get the individual configuration of a component.
     * 
     * @return config
     */
    Map<String, Object> getConfiguration();

    /**
     * Load the configuration of a component.
     * 
     * @param loadedConfiguration that was stored
     */
    void loadConfiguration(Map<String, Object> loadedConfiguration);

}
