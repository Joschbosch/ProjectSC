/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.entities;

import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.manager.EntityManager;

/**
 * All game objects in the game are entities. All entities have components, that define their role and behavior.
 * 
 * @author Josch Bosch
 */
public class EntityImpl implements Entity {

    private long entityTypeID;

    private final String entityUID;

    private String tag;

    private final Transform transform;

    private EntityManager entityManager;

    public EntityImpl(EntityManager entityManager, String uid) {
        entityUID = uid;
        transform = new Transform();
        this.entityManager = entityManager;
    }

    @Override
    public boolean hasComponent(Class<? extends Component> clazz) {
        return entityManager.hasComponent(entityUID, clazz);
    }

    @Override
    public Component getComponent(Class<? extends Component> clazz) {
        return entityManager.getComponent(entityUID, clazz);
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public long getEntityTypeId() {
        return entityTypeID;
    }

    @Override
    public String getID() {
        return entityUID;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return String.valueOf(entityUID);
    }

    @Override
    public void setEntityTypeId(long id) {
        this.entityTypeID = id;
    }

}
