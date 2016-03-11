/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.interfaces;

/**
 * Interface for the entity container.
 * 
 * @author Josch Bosch
 */
public interface Entity {

    /**
     * Check if entity has the specified component.
     * 
     * @param clazz to check
     * @return true if it has the component.
     */
    boolean hasComponent(Class<? extends Component> clazz);

    /**
     * Get the component specified .
     * 
     * @param clazz to get
     * @return component specified from this entity
     */
    Component getComponent(Class<? extends Component> clazz);

    /**
     * Get entity type id.
     * 
     * @return id
     */
    long getEntityTypeId();

    /**
     * @param id of entity type
     */
    void setEntityTypeId(long id);

    /**
     * @return layer the entity is in (used for collision and rendering)
     */
    int getLayer();

    /**
     * @return unique id of the entity.
     */
    String getID();

    /**
     * set the layer of the entity.
     * 
     * @param layer to set
     */
    void setLayer(int layer);

    /**
     * @return tag of the entity
     */
    String getTag();

    /**
     * @param tag to set
     */
    void setTag(String tag);

}
