/*
 * Copyright (C) 2015
 */

package de.projectsc.core.components.impl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Billboard;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.objects.ParticleEmitter;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.utils.BoundingBox;

/**
 * Component for adding particle emitter.
 * 
 * @author Josch Bosch
 */
public class ParticleEmitterComponent extends Component {

    /**
     * Name.
     */
    public static final String NAME = "Particle emitter component";

    private final List<ParticleEmitter> emitter = new LinkedList<>();

    public ParticleEmitterComponent(Entity owner) {
        super(NAME, owner);
        type = ComponentType.GRAPHICS;
    }

    /**
     * Add Emitter.
     * 
     * @param e to add
     */
    public void addNewEmitter(ParticleEmitter e) {
        emitter.add(e);
    }

    /**
     * Create a new Emitter at position.
     * 
     * @param position to create the emitter at
     */
    public void createNewEmitter(Vector3f position) {
        ParticleEmitter e = new ParticleEmitter(position, "particleTexture.png", new Vector2f(0, 0), 1.0f, true);
        emitter.add(e);
    }

    @Override
    public void update(Entity owner) {
        for (ParticleEmitter e : emitter) {
            e.update();
        }
    }

    @Override
    public void render(Entity owner, Map<TexturedModel, List<Entity>> entities, Map<RawModel, List<BoundingBox>> boundingBoxes,
        List<Light> lights, List<Billboard> billboards, List<ParticleEmitter> particles, Camera camera, long elapsedTime) {
        particles.addAll(emitter);
    }

    @Override
    public String serialize() throws JsonGenerationException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public void deserialize(JsonNode input, File schemaDir) throws JsonProcessingException, IOException {

    }

    @Override
    public boolean isValidForSaving() {
        return false;
    }
}
