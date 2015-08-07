/*
 * Copyright (C) 2015 
 */

package de.projectsc.client.gui.objects;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class ParticleSource {

    private static final int MAX_PARTICLES = 1000;

    private Vector3f position;

    private List<Particle> particles;

    private int lastUsedParticle = 0;

    private int textureId;

    private float particleLifetime = 5.0f;

    private float particleLifetimeMargin = 0.5f;

    public ParticleSource() {
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
