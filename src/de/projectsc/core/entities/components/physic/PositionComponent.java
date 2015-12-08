/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.entities.components.physic;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.entities.ComponentType;

public class PositionComponent extends PhysicsComponent {

    public static final String NAME = "PositionComponent";

    private Vector3f position;

    private Vector3f rotation;

    public PositionComponent() {
        setID(NAME);
        setType(ComponentType.PHYSICS);
    }

    @Override
    public void update(long ownerEntity) {

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

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

}
