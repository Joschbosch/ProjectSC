/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.objects;

import java.util.ArrayList;
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
    public static final int MAX_PARTICLES_PER_SOURCE = 100;

    private Vector3f position;

    private final List<Particle> particles;

    private int particleCount;

    private int lastUsedParticle = 0;

    private int textureId;

    private final float particleLifetime = 1.0f;

    private final float particleLifetimeMargin = 0.5f;

    private final float[] positionBuffer;

    private final byte[] colorBuffer;

    private final float[] uvBuffer;

    private Vector3f cameraPostion;

    private boolean useColors;

    private float numberOfRows;

    private Vector2f offset;

    public ParticleEmitter(Vector3f position, String textureName, Vector2f offset, float numberOfRows, boolean useColors) {
        positionBuffer = new float[MAX_PARTICLES_PER_SOURCE * 4];
        colorBuffer = new byte[MAX_PARTICLES_PER_SOURCE * 4];
        uvBuffer = new float[MAX_PARTICLES_PER_SOURCE * 8];
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

        uvBuffer[i * 8] = 0;
        uvBuffer[i * 8 + 1] = 0;
        uvBuffer[i * 8 + 2] = 0;
        uvBuffer[i * 8 + 3] = 0;
        uvBuffer[i * 8 + 4] = 0;
        uvBuffer[i * 8 + 5] = 0;
        uvBuffer[i * 8 + 6] = 0;
        uvBuffer[i * 8 + 7] = 0;

        p.setDirection(new Vector3f(1.0f, 1.0f, 1.0f));
        p.setWeight(0.0f);
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
            newparticles = (int) (MAX_PARTICLES_PER_SOURCE * 1f / 100f);
        }
        for (int i = 0; i < newparticles; i++) {
            int index = findUnsuedSlot();
            Particle particle = particles.get(index);
            if (particle.getLifetime() < 0) {
                particle.setLifetime(
                    (float) random(particleLifetime, particleLifetimeMargin));
                particle.setStartLifeTime(particle.getLifetime());
                float radius = 5f;
                Vector3f startPos =
                    new Vector3f((float) (position.x + ((2 * Math.random() - 1) * radius)),
                        (float) (position.y + ((2 * Math.random() - 1) * radius)),
                        (float) (position.z + ((2 * Math.random() - 1) * radius)));

                while (!(Vector3f.sub(startPos, position, null).length() <= radius)) {
                    startPos =
                        new Vector3f((float) (position.x + ((2 * Math.random() - 1) * radius)),
                            (float) (position.y + ((2 * Math.random() - 1) * radius)),
                            (float) (position.z + ((2 * Math.random() - 1) * radius)));

                }
                particle.setPosition(startPos);

                if (useColors) {
                    particle.setColor(
                        new Vector4f((float) (Math.random() * 255), (float) (Math.random() * 255), (float) (Math.random() * 255),
                            (float) (Math
                                .random() * 255)));
                } else {
                    particle.setColor(new Vector4f(255, 255, 255, 255));

                }
                float spread = 1f;

                Vector3f dir = new Vector3f(0, 0, 0);

                Vector3f randDir = new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random());
                randDir.scale(spread);
                particle.setDirection(dir);
                particle.setSize((float) Math.random());
            }
        }
        particleCount = 0;
        for (int i = 0; i < MAX_PARTICLES_PER_SOURCE; i++) {
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

                    int stages = (int) (Math.pow(numberOfRows, 2));
                    float timePerStage = (p.getStartLifeTime() / stages);
                    int index = stages - (int) (p.getLifetime() / timePerStage);
                    int column = (int) (index % getNumberOfRows());
                    float xCoord0 = (column / getNumberOfRows());
                    int row = (int) (index / getNumberOfRows());
                    float yCoord0 = (row / getNumberOfRows());
                    float size = 1.0f / getNumberOfRows();

                    positionBuffer[4 * particleCount + 0] = newPosition.x;
                    positionBuffer[4 * particleCount + 1] = newPosition.y;
                    positionBuffer[4 * particleCount + 2] = newPosition.z;
                    positionBuffer[4 * particleCount + 3] = p.getSize();

                    colorBuffer[4 * particleCount + 0] = (byte) p.getColor().x;
                    colorBuffer[4 * particleCount + 1] = (byte) p.getColor().y;
                    colorBuffer[4 * particleCount + 2] = (byte) p.getColor().z;
                    colorBuffer[4 * particleCount + 3] = (byte) p.getColor().w;

                    uvBuffer[particleCount * 8] = xCoord0;
                    uvBuffer[particleCount * 8 + 1] = yCoord0 + size;
                    uvBuffer[particleCount * 8 + 2] = xCoord0 + size;
                    uvBuffer[particleCount * 8 + 3] = yCoord0 + size;
                    uvBuffer[particleCount * 8 + 4] = xCoord0;
                    uvBuffer[particleCount * 8 + 5] = yCoord0;
                    uvBuffer[particleCount * 8 + 6] = xCoord0 + size;
                    uvBuffer[particleCount * 8 + 7] = yCoord0;

                } else {
                    p.setCameradistance(-1);
                }
                particleCount++;
            }
        }
        // Collections.sort(particles);
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

    public float[] getUVBuffer() {
        return uvBuffer;
    }
}
