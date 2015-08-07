/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.objects;

import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.tools.Loader;

public class ParticleSource {

    private static final int MAX_PARTICLES = 1000;

    private Vector3f position;

    private List<Particle> particles;

    private int lastUsedParticle = 0;

    private int textureId;

    private final float particleLifetime = 5.0f;

    private final float particleLifetimeMargin = 0.5f;

    private final Camera camera;

    private final Object quad;

    public ParticleSource(Camera camera, Loader loader) {
        for (int i = 0; i < MAX_PARTICLES; i++) {
            Particle p = new Particle();
            p.setAngle(0.0f);
            p.setCameradistance(-1);
            p.setColor(new Vector4f(0, 0, 0, 0));
            p.setLifetime(-1);
            p.setPosition(new Vector3f(0, 0, 0));
            p.setSize(1.0f);
            p.setSpeed(new Vector3f(1.0f, 1.0f, 1.0f));
            p.setWeight(1.0f);
            particles.add(p);
        }
        this.camera = camera;
        // Just x and z vertex positions here, y is set to 0 in v.shader
        float[] vertices = {
            -1, -1,
            1, -1,
            -1, 1,
            1, -1,
            -1, 1,
            1, 1 };
        quad = null;
        // quad = loader.loadParticlesToVAO(vertices, 2);
    }

    public void update() {
        float delta = 0.016f;
        int newparticles = (int) (delta * 10000.0);
        if (newparticles > (int) (0.016f * 10000.0))
            newparticles = (int) (0.016f * 10000.0);

        for (int i = 0; i < newparticles; i++) {
            int index = findUnsuedSlot();
            particles.get(index).setLifetime(
                (float) (particleLifetime + 2 * (particleLifetimeMargin * Math.random()) - particleLifetimeMargin));
            particles.get(index).setPosition(position);
            particles.get(index).setSpeed(new Vector3f(1.0f, 1.0f, 1.0f));
        }
        for (int i = 0; i < MAX_PARTICLES; i++) {
            Particle p = particles.get(i);
            if (p.getLifetime() > 0.0f) {
                p.setLifetime(p.getLifetime() - delta);
                Vector3f gravity = new Vector3f(0.0f, -9.81f, 0.0f);
                gravity.scale(delta * 0.5f);
                Vector3f newSpeed = Vector3f.add(p.getSpeed(), gravity, null);
                p.setSpeed(newSpeed);
                newSpeed.scale(delta);
                Vector3f newPosition = Vector3f.add(p.getPosition(), newSpeed, null);
                p.setPosition(newPosition);
                p.setCameradistance(Vector3f.sub(p.getPosition(), camera.getPosition(), null).lengthSquared());
            } else {
                p.setCameradistance(-1);
            }
        }
        Collections.sort(particles);
    }

    private int findUnsuedSlot() {

        for (int i = lastUsedParticle; i < MAX_PARTICLES; i++) {
            if (particles.get(i).getLifetime() < 0) {
                lastUsedParticle = i;
                return i;
            }
        }

        for (int i = 0; i < lastUsedParticle; i++) {
            if (particles.get(i).getLifetime() < 0) {
                lastUsedParticle = i;
                return i;
            }
        }
        return 0;
    }

    public int getTextureAtlas() {
        return textureId;
    }

}
