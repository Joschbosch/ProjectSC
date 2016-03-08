/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.modes.client.gui.objects.particles.ParticleSystem;
import de.projectsc.modes.client.gui.objects.particles.ParticleTexture;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Component for particles.
 * 
 * @author Josch Bosch
 */
public class ParticleSystemComponent extends DefaultComponent {

    /**
     * Name.
     */
    public static final String NAME = "Particle System Component";

    private List<ParticleSystem> particleSystems = new LinkedList<>();

    private Map<Integer, Vector3f> offsets = new HashMap<>();

    public ParticleSystemComponent() {
        setComponentName(NAME);
        setType(ComponentType.GRAPHICS);
    }

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }

    /**
     * Add a new system.
     */
    public void addNewParticleSystem() {
        ParticleSystem particleSystem = new ParticleSystem(new Vector3f(0, 10, 0), 100, 0.1f, -1, 5, 1, true, new ParticleTexture(Loader
            .loadTexture("particles/smoke.png"), 5));
        particleSystems.add(particleSystem);
        offsets.put(particleSystem.getId(), new Vector3f(0, 10, 0));
    }

    @Override
    public Component cloneComponent() {
        ParticleSystemComponent target = new ParticleSystemComponent();
        for (ParticleSystem ps : particleSystems) {
            target.addNewParticleSystem();
            ps.getSystemCenter();
            System.out.println("TODO");
        }
        addNewParticleSystem();
        return target;
    }

    public List<ParticleSystem> getParticleSystems() {
        return particleSystems;
    }

    public Map<Integer, Vector3f> getOffsets() {
        return offsets;
    }
}
