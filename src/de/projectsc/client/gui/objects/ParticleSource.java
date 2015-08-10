/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.objects;

import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class ParticleSource {

    public static final int MAX_PARTICLES_PER_SOURCE = 1000;

    private Vector3f position;

    private List<Particle> particles;

    private int lastUsedParticle = 0;

    private int textureId;

    private final float particleLifetime = 5.0f;

    private final float particleLifetimeMargin = 0.5f;

    private int particleCount;

    private float[] positionBuffer;

    private byte[] colorBuffer;

    private Vector3f cameraPostion;

    public ParticleSource() {
        float[] positions = new float[MAX_PARTICLES_PER_SOURCE * 4];
        byte[] color = new byte[MAX_PARTICLES_PER_SOURCE * 4];
        for (int i = 0; i < MAX_PARTICLES_PER_SOURCE; i++) {
            Particle p = new Particle();
            p.setAngle(0.0f);
            p.setCameradistance(-1);
            p.setColor(new Vector4f(0, 0, 0, 0));
            color[i] = 0;
            color[i + 1] = 0;
            color[i + 2] = 0;
            color[i + 3] = 0;
            p.setLifetime(-1);
            p.setPosition(new Vector3f(0, 0, 0));
            p.setSize(1.0f);
            positions[i * 4] = 0;
            positions[i * 4 + 1] = 0;
            positions[i * 4 + 2] = 0;
            positions[i * 4 + 3] = 1.0f;
            p.setSpeed(new Vector3f(1.0f, 1.0f, 1.0f));
            p.setWeight(1.0f);
            particles.add(p);
        }
        particleCount = MAX_PARTICLES_PER_SOURCE;
        this.cameraPostion = new Vector3f(0, 0, 0);
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
        particleCount = 0;
        for (int i = 0; i < MAX_PARTICLES_PER_SOURCE; i++) {
            Particle p = particles.get(i);
            if (p.getLifetime() > 0.0f) {
                p.setLifetime(p.getLifetime() - delta);
                if (p.getLifetime() > 0.0f) {
                    Vector3f gravity = new Vector3f(0.0f, -9.81f, 0.0f);
                    gravity.scale(delta * 0.5f);
                    Vector3f newSpeed = Vector3f.add(p.getSpeed(), gravity, null);
                    p.setSpeed(newSpeed);
                    newSpeed.scale(delta);
                    Vector3f newPosition = Vector3f.add(p.getPosition(), newSpeed, null);
                    p.setPosition(newPosition);
                    p.setCameradistance(Vector3f.sub(p.getPosition(), cameraPostion, null).lengthSquared());

                    positionBuffer[4 * particleCount + 0] = newPosition.x;
                    positionBuffer[4 * particleCount + 1] = newPosition.y;
                    positionBuffer[4 * particleCount + 2] = newPosition.z;
                    positionBuffer[4 * particleCount + 0] = p.getSize();

                    colorBuffer[4 * particleCount + 0] = (byte) p.getColor().x;
                    colorBuffer[4 * particleCount + 1] = (byte) p.getColor().y;
                    colorBuffer[4 * particleCount + 2] = (byte) p.getColor().z;
                    colorBuffer[4 * particleCount + 3] = (byte) p.getColor().w;

                } else {
                    p.setCameradistance(-1);
                }
                particleCount++;
            }
        }
        Collections.sort(particles);
    }

    private int findUnsuedSlot() {

        for (int i = lastUsedParticle; i < MAX_PARTICLES_PER_SOURCE; i++) {
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

    public float[] getPositionBuffer() {
        return positionBuffer;
    }

    public byte[] getColorBuffer() {
        return colorBuffer;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public void setCameraPostion(Vector3f cameraPostion) {
        this.cameraPostion = cameraPostion;
    }
}
