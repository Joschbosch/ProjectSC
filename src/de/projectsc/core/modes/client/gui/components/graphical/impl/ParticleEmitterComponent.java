/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.components.graphical.impl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.entities.ComponentType;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.modes.client.gui.Scene;
import de.projectsc.core.modes.client.gui.components.GraphicalComponent;
import de.projectsc.core.modes.client.gui.objects.BasicParticleConfiguration;
import de.projectsc.core.modes.client.gui.objects.ParticleEmitter;

/**
 * Component for adding particle emitter.
 * 
 * @author Josch Bosch
 */
public class ParticleEmitterComponent extends GraphicalComponent {

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
        ParticleEmitter e =
            new ParticleEmitter(position, "particleTexture.png", 1.0f, true, new BasicParticleConfiguration());
        emitter.add(e);
    }

    @Override
    public void update(Entity owner) {
        for (ParticleEmitter e : emitter) {
            e.update();
        }
    }

    @Override
    public void render(Scene scene) {
        scene.getParticles().addAll(emitter);
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
