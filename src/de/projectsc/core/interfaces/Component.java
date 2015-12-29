/*
 * Copyright (C) 2015
 */

package de.projectsc.core.interfaces;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.projectsc.core.data.Scene;

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
     * Adds everything to the scene that is needed for debugging mode. Might not be used.
     * 
     * @param entity
     * 
     * @param scene to add to.
     */
    void addSceneInformation(Scene scene);

    /**
     * @return true, if the component can be saved at the moment.
     */
    boolean isValidForSaving();

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
     * @param schemaDirectory directory to load from
     */
    void deserialize(Map<String, Object> serialized, File schemaDirectory);

    void update(long elapsed);

    /**
     * @return name of the component.
     */
    String getComponentName();

    String getId();

    void createNewId();

    void setOwner(Entity entity);

    String serializeForNetwork();

    void deserializeFromNetwork(String serial);
}
