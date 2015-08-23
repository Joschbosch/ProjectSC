/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.client.gui.tools.Loader;

/**
 * Emitter class for particles.
 *
 * @author Josch Bosch
 */
public class ParticleEmitter {

    /**
     * Maximal particles for one emitter.
     */
    public static final int MAX_PARTICLES_PER_SOURCE = 100000;

    private Vector3f position;

    private final List<Particle> particles;

    private int particleCount;

    private int lastUsedParticle = 0;

    private int textureId;

    private final float particleLifetime = 5.0f;

    private final float particleLifetimeMargin = 0.5f;

    private final float[] positionBuffer;

    private final byte[] colorBuffer;

    private Vector3f cameraPostion;

    private boolean useColors;

    private float numberOfRows;

    private Vector2f offset;

    public ParticleEmitter(Vector3f position, String textureName, Vector2f offset, float numberOfRows, boolean useColors) {
        positionBuffer = new float[MAX_PARTICLES_PER_SOURCE * 4];
        colorBuffer = new byte[MAX_PARTICLES_PER_SOURCE * 4];
        particles = new ArrayList<>();
        this.position = position;
        for (int i = 0; i < MAX_PARTICLES_PER_SOURCE; i++) {
            createNewParticle(i);
        }
        particleCount = MAX_PARTICLES_PER_SOURCE;
        this.cameraPostion = new Vector3f(0, 0, 0);
        textureId = Loader.loadTexture(textureName);
        this.useColors = useColors;
        this.setNumberOfRows(numberOfRows);
        this.setOffset(offset);
    }

    private void createNewParticle(int i) {
        Particle p = new Particle();
        p.setAngle((float) (Math.random()));
        p.setCameradistance(-1);
        p.setColor(new Vector4f((byte) (Math.random() * 255), (byte) (Math.random() * 255), (byte) (Math.random() * 255), (byte) (Math
            .random() * 255)));
        colorBuffer[i] = (byte) p.getColor().x;
        colorBuffer[i + 1] = (byte) p.getColor().y;
        colorBuffer[i + 2] = (byte) p.getColor().z;
        colorBuffer[i + 3] = (byte) p.getColor().w;
        p.setLifetime(-1);
        p.setPosition(position);
        p.setSize(1.0f);
        positionBuffer[i * 4] = 0;
        positionBuffer[i * 4 + 1] = 0;
        positionBuffer[i * 4 + 2] = 0;
        positionBuffer[i * 4 + 3] = 1.0f;
        p.setSpeed(new Vector3f(1.0f, 1.0f, 1.0f));
        p.setWeight(1.0f);
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
        for (int i = 0; i < newparticles; i++) {
            int index = findUnsuedSlot();
            if (particles.get(index).getLifetime() < 0) {
                particles.get(index).setLifetime(
                    (float) random(particleLifetime, particleLifetimeMargin));
                particles.get(index).setPosition(position);
                if (useColors) {
                    particles.get(index).setColor(
                        new Vector4f((float) (Math.random() * 255), (float) (Math.random() * 255), (float) (Math.random() * 255),
                            (float) (Math
                                .random() * 255)));
                } else {
                    particles.get(index).setColor(new Vector4f(255, 255, 255, 255));

                }
                float spread = 1.5f;

                Vector3f dir = new Vector3f(0, 10, 0);

                Vector3f randDir = new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random());
                randDir.scale(spread);
                particles.get(index).setSpeed(Vector3f.add(randDir, dir, null));
                particles.get(index).setSize((float) Math.random());
            }
        }
        particleCount = 0;
        for (int i = 0; i < MAX_PARTICLES_PER_SOURCE; i++) {
            Particle p = particles.get(i);
            if (p != null && p.getLifetime() > 0.0f) {
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
                    positionBuffer[4 * particleCount + 3] = p.getSize();

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

    private double random(float base, float margin) {
        return base + 2 * (margin * Math.random()) - margin;
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

    public float getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(float numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }
}
