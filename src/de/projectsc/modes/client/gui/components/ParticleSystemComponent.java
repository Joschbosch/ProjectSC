/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.components;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.data.Scene;
import de.projectsc.core.data.physics.WireFrame;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.objects.particles.ParticleSystem;
import de.projectsc.modes.client.gui.objects.particles.ParticleTexture;
import de.projectsc.modes.client.gui.utils.Loader;

public class ParticleSystemComponent extends GraphicalComponent {

    /**
     * Name.
     */
    public static final String NAME = "Particle System Component";

    private static final Log LOGGER = LogFactory.getLog(ParticleSystemComponent.class);

    private List<ParticleSystem> particleSystems = new LinkedList<>();

    private Map<Integer, Vector3f> offsets = new HashMap<>();

    public ParticleSystemComponent() {
        setID(NAME);
        setType(ComponentType.GRAPHICS);

    }

    @Override
    public void render(String entity, GUIScene scene) {

    }

    @Override
    public void update() {
        if (particleSystems.size() == 0) {
            addNewParticleSystem();
        }
        for (ParticleSystem s : particleSystems) {
            if (offsets.get(s.getId()) != null && owner.getTransform() != null) {
                Vector3f position =
                    Vector3f.add(owner.getTransform().getPosition(), offsets.get(s.getId()), null);
                s.setSystemCenter(position);
                s.generateParticles();
            }
        }
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        return new HashMap<>();
    }

    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {

    }

    @Override
    public void addSceneInformation(Scene scene) {
        for (ParticleSystem s : particleSystems) {
            WireFrame w = new WireFrame(WireFrame.SPHERE, s.getSystemCenter(), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
            scene.getWireFrames().add(w);
        }
    }

    public void addNewParticleSystem() {
        ParticleSystem particleSystem = new ParticleSystem(new Vector3f(0, 0, 0), 100, 10, 1, 5, 1, true, new ParticleTexture(Loader
            .loadTexture("particles/particleStar.png"), 1));
        particleSystems.add(particleSystem);
        offsets.put(particleSystem.getId(), new Vector3f(0, 0, 0));
    }
}
