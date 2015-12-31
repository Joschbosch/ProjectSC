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
import de.projectsc.core.data.Scene;
import de.projectsc.core.data.physics.WireFrame;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.objects.particles.ParticleSystem;
import de.projectsc.modes.client.gui.objects.particles.ParticleTexture;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Component for particels.
 * 
 * @author Josch Bosch
 */
public class ParticleSystemComponent extends GraphicalComponent {

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
    public void render(String entity, GUIScene scene) {
        for (ParticleSystem s : particleSystems) {
            if (offsets.get(s.getId()) != null && owner.getTransform() != null) {
                Vector3f position =
                    Vector3f.add(owner.getTransform().getPosition(), offsets.get(s.getId()), null);
                s.setSystemCenter(position);
            }
        }
    }

    @Override
    public void update(long elapsed) {
        if (particleSystems.size() == 0) {
            addNewParticleSystem();
        }
        for (ParticleSystem s : particleSystems) {
            if (offsets.get(s.getId()) != null) {
                s.generateParticles(elapsed);
            }
        }
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    @Override
    public void addSceneInformation(Scene scene) {
        for (ParticleSystem s : particleSystems) {
            WireFrame w = new WireFrame(WireFrame.SPHERE, s.getSystemCenter(), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
            scene.getWireFrames().add(w);
        }
    }

    /**
     * Add a new system.
     */
    public void addNewParticleSystem() {
        ParticleSystem particleSystem = new ParticleSystem(new Vector3f(0, 0, 0), 100, 10, 1, 5, 1, true, new ParticleTexture(Loader
            .loadTexture("particles/particleStar.png"), 1));
        particleSystems.add(particleSystem);
        offsets.put(particleSystem.getId(), new Vector3f(0, 0, 0));
    }
}
