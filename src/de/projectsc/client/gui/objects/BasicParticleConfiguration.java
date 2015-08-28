/*
 * Copyright (C) 2015 
 */

package de.projectsc.client.gui.objects;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class BasicParticleConfiguration {

    private float particleLifetime = 1.0f;

    private float particleLifetimeMargin = 0.5f;

    private float angle = 0;

    private Vector4f color = new Vector4f(0f, 0f, 0f, 255f);

    private float maximumAlphaValue = 128;

    private Vector3f position;

    private float positionRadiusX = 0;

    private float positionRadiusY = 0;

    private float positionRadiusZ = 0;

    private Vector3f speedDirection = new Vector3f(0, 10, 0);

    private float randomDirectionMarginX = 0;

    private float randomDirectionMarginY = 0;

    private float randomDirectionMarginZ = 0;

    private float weight = 0.0f;

    private float speed = 1.0f;

    private float spread = 1.5f;

    private float size = 1.0f;

    private float sizeMargin = 0.0f;

    private Random random;

    public BasicParticleConfiguration() {
        random = new Random();
    }

    public float createParticleLifeTime() {
        return getRandomGauss(particleLifetime, particleLifetimeMargin);
    }

    public Vector4f createRandomColor() {
        return new Vector4f(random.nextFloat() * 255, random.nextFloat() * 255, random.nextFloat() * 255, random.nextFloat()
            * maximumAlphaValue);
    }

    public Vector3f createRandomPosition() {
        Vector3f startPosition =
            new Vector3f(getRandomDistributed(position.x, positionRadiusX), getRandomDistributed(position.y, positionRadiusY),
                getRandomDistributed(position.z, positionRadiusZ));
        while (!(Vector3f.sub(startPosition, position, null).length() <= (positionRadiusX + positionRadiusY + positionRadiusZ) / 3)) {
            startPosition =
                new Vector3f(getRandomDistributed(position.x, positionRadiusX), getRandomDistributed(position.y, positionRadiusY),
                    getRandomDistributed(position.z, positionRadiusZ));

        }
        return startPosition;
    }

    public Vector3f createRandomDirection() {

        Vector3f newDir =
            new Vector3f(getRandomDistributed(0, randomDirectionMarginX), getRandomDistributed(0,
                randomDirectionMarginY), getRandomDistributed(0, randomDirectionMarginZ));
        while (!(newDir.length() <= (randomDirectionMarginY + randomDirectionMarginX + randomDirectionMarginZ) / 3)) {
            newDir =
                new Vector3f(getRandomDistributed(0, randomDirectionMarginX), getRandomDistributed(0,
                    randomDirectionMarginY), getRandomDistributed(0, randomDirectionMarginZ));
        }
        newDir.scale(spread);
        Vector3f dir = Vector3f.add(speedDirection, newDir, null);
        dir.scale(speed);
        return dir;
    }

    public float createRandomSize() {
        return getRandomDistributed(size, sizeMargin);
    }

    private float getRandomDistributed(float x, float margin) {
        return x + (2 * random.nextFloat() * margin - margin);
    }

    private float getRandomGauss(float mean, float variance) {
        return (float) (mean + random.nextGaussian() * variance);
    }

    public float getParticleLifetime() {
        return particleLifetime;
    }

    public void setParticleLifetime(float particleLifetime) {
        this.particleLifetime = particleLifetime;
    }

    public float getParticleLifetimeMargin() {
        return particleLifetimeMargin;
    }

    public void setParticleLifetimeMargin(float particleLifetimeMargin) {
        this.particleLifetimeMargin = particleLifetimeMargin;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getPositionRadiusX() {
        return positionRadiusX;
    }

    public void setPositionRadiusX(float positionRadiusX) {
        this.positionRadiusX = positionRadiusX;
    }

    public float getMaximumAlphaValue() {
        return maximumAlphaValue;
    }

    public void setMaximumAlphaValue(float maximumAlphaValue) {
        this.maximumAlphaValue = maximumAlphaValue;
    }

    public float getPositionRadiusY() {
        return positionRadiusY;
    }

    public void setPositionRadiusY(float positionRadiusY) {
        this.positionRadiusY = positionRadiusY;
    }

    public float getPositionRadiusZ() {
        return positionRadiusZ;
    }

    public void setPositionRadiusZ(float positionRadiusZ) {
        this.positionRadiusZ = positionRadiusZ;
    }

    public Vector3f getSpeedDirection() {
        return speedDirection;
    }

    public void setSpeedDirection(Vector3f speedDirection) {
        this.speedDirection = speedDirection;
    }

    public float getRandomDirectionMarginX() {
        return randomDirectionMarginX;
    }

    public void setRandomDirectionMarginX(float randomDirectionMarginX) {
        this.randomDirectionMarginX = randomDirectionMarginX;
    }

    public float getRandomDirectionMarginY() {
        return randomDirectionMarginY;
    }

    public void setRandomDirectionMarginY(float randomDirectionMarginY) {
        this.randomDirectionMarginY = randomDirectionMarginY;
    }

    public float getRandomDirectionMarginZ() {
        return randomDirectionMarginZ;
    }

    public void setRandomDirectionMarginZ(float randomDirectionMarginZ) {
        this.randomDirectionMarginZ = randomDirectionMarginZ;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getSpread() {
        return spread;
    }

    public void setSpread(float spread) {
        this.spread = spread;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getSizeMargin() {
        return sizeMargin;
    }

    public void setSizeMargin(float sizeMargin) {
        this.sizeMargin = sizeMargin;
    }

}
