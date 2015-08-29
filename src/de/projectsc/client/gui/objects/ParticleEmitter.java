/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.tools.Loader;

/**
 * Emitter class for particles.
 *
 * @author Josch Bosch
 */
public class ParticleEmitter implements Comparable<ParticleEmitter> {

    /**
     * Maximal particles for one emitter.
     */
    public static final int MAX_PARTICLES_PER_SOURCE = 50000;

    private final Vector3f position;

    private final List<Particle> particles;

    private int particleCount;

    private int lastUsedParticle = 0;

    private final int textureId;

    private final float[] positionBuffer;

    private final byte[] colorBuffer;

    private final float[] lifetimeBuffer;

    private Vector3f cameraPostion;

    private final boolean useColors;

    private float numberOfRows;

    private float cameraDistance;

    private int numberOfParticles = MAX_PARTICLES_PER_SOURCE;

    private boolean glowy = false;

    private final BasicParticleConfiguration config;

    public ParticleEmitter(Vector3f position, String textureName, float numberOfRows, boolean useColors,
        BasicParticleConfiguration config) {
        positionBuffer = new float[MAX_PARTICLES_PER_SOURCE * 4];
        colorBuffer = new byte[MAX_PARTICLES_PER_SOURCE * 4];
        lifetimeBuffer = new float[MAX_PARTICLES_PER_SOURCE * 2];
        particles = new ArrayList<>();
        this.position = position;

        particleCount = MAX_PARTICLES_PER_SOURCE;
        this.cameraPostion = new Vector3f(0, 0, 0);
        textureId = Loader.loadTexture(textureName);
        this.useColors = useColors;
        this.setNumberOfRows(numberOfRows);
        this.config = config;
        config.setPosition(position);

        for (int i = 0; i < MAX_PARTICLES_PER_SOURCE; i++) {
            createNewParticle(i);
        }
    }

    private void createNewParticle(int i) {
        Particle p = new Particle();
        p.setAngle(config.getAngle());
        p.setCameradistance(-1);
        p.setColor(config.createRandomColor());
        p.setLifetime(-1);
        p.setPosition(position);
        p.setSize(config.createRandomSize());
        p.setDirection(config.createRandomDirection());
        p.setWeight(config.getWeight());

        colorBuffer[i] = (byte) p.getColor().x;
        colorBuffer[i + 1] = (byte) p.getColor().y;
        colorBuffer[i + 2] = (byte) p.getColor().z;
        colorBuffer[i + 3] = (byte) p.getColor().w;
        positionBuffer[i * 4] = 0;
        positionBuffer[i * 4 + 1] = 0;
        positionBuffer[i * 4 + 2] = 0;
        positionBuffer[i * 4 + 3] = 1.0f;

        lifetimeBuffer[i * 2] = 0;
        lifetimeBuffer[i * 2 + 1] = 0;
        particles.add(p);
    }

    /**
     * update particles.
     */
    public void update() {
        float delta = 0.016f;
        int newparticles = (int) (delta * 10000.0);
        if (newparticles > (int) (0.016f * 10000.0)) {
            newparticles = (int) (0.016f * 10000.0);
        }
        if (newparticles == 0) {
            newparticles = (int) (numberOfParticles * 1f / 100f);
        }
        for (int i = 0; i < newparticles; i++) {
            int index = findUnsuedSlot();
            Particle particle = particles.get(index);
            if (particle.getLifetime() < 0) {
                particle.setLifetime(config.createParticleLifeTime());
                particle.setStartLifeTime(particle.getLifetime());
                particle.setPosition(config.createRandomPosition());
                particle.setSize(config.createRandomSize());
                if (useColors) {
                    particle.setColor(config.createRandomColor());
                } else {
                    particle.setColor(new Vector4f(255, 255, 255, 255));

                }
                particle.setDirection(config.createRandomDirection());
            }
        }
        particleCount = 0;
        for (int i = 0; i < numberOfParticles; i++) {
            Particle p = particles.get(i);
            if (p != null && p.getLifetime() > 0.0f) {
                p.setLifetime(p.getLifetime() - delta);
                if (p.getLifetime() > 0.0f) {
                    Vector3f gravity = new Vector3f(0.0f, -9.81f, 0.0f);
                    gravity.scale(delta * p.getWeight());
                    Vector3f newSpeed = Vector3f.add(p.getDirection(), gravity, null);
                    p.setDirection(newSpeed);
                    newSpeed.scale(delta);
                    Vector3f newPosition = Vector3f.add(p.getPosition(), newSpeed, null);
                    p.setPosition(newPosition);
                    p.setCameradistance(Vector3f.sub(p.getPosition(), cameraPostion, null).length());

                    positionBuffer[4 * particleCount] = newPosition.x;
                    if (newPosition.y < 0) {
                        p.setLifetime(-1);
                    } else {
                        positionBuffer[4 * particleCount + 1] = newPosition.y;
                    }
                    positionBuffer[4 * particleCount + 2] = newPosition.z;
                    positionBuffer[4 * particleCount + 3] = p.getSize();

                    colorBuffer[4 * particleCount + 0] = (byte) p.getColor().x;
                    colorBuffer[4 * particleCount + 1] = (byte) p.getColor().y;
                    colorBuffer[4 * particleCount + 2] = (byte) p.getColor().z;
                    colorBuffer[4 * particleCount + 3] = (byte) p.getColor().w;

                    lifetimeBuffer[particleCount * 2] = p.getStartLifeTime();
                    lifetimeBuffer[particleCount * 2 + 1] = p.getLifetime();

                } else {
                    p.setCameradistance(-1);
                }
                particleCount++;
            }
        }
        for (int i = numberOfParticles; i < MAX_PARTICLES_PER_SOURCE; i++) {
            Particle p = particles.get(i);
            p.setLifetime(-1);
        }
        Collections.sort(particles);
        this.setCameraDistance(Vector3f.sub(position, cameraPostion, null).length());
    }

    private void setCameraDistance(float length) {
        cameraDistance = length;
    }

    public float getCameraDistance() {
        return cameraDistance;
    }

    private double random(float base, float margin) {
        return base + 2 * (margin * Math.random()) - margin;
    }

    private int findUnsuedSlot() {

        for (int i = lastUsedParticle; i < numberOfParticles; i++) {
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

    public float getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(float numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public float[] getLifetimeBuffer() {
        return lifetimeBuffer;
    }

    @Override
    public int compareTo(ParticleEmitter o) {
        float dist = (this.cameraDistance - o.getCameraDistance());
        if (dist > 0 && dist <= 1) {
            return -1;
        } else if (dist < 0 && dist >= -1) {
            return 1;
        } else {
            return (int) -(this.cameraDistance - o.getCameraDistance());
        }
    }

    public boolean isGlowy() {
        return glowy;
    }

    public void setGlowy(boolean glowy) {
        this.glowy = glowy;
    }

    public int getNumberOfParticles() {
        return numberOfParticles;
    }

    public void setNumberOfParticles(int numberOfParticles) {
        this.numberOfParticles = numberOfParticles;
    }
}
