/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.objects.particles;

import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.data.utils.Timer;

public class ParticleSystem {

    private static int idCount = 0;

    private float pps, averageSpeed, gravityComplient, averageLifeLength, averageScale;

    private float speedError, lifeError, scaleError = 0;

    private boolean randomRotation = false;

    private Vector3f direction;

    private float directionDeviation = 0;

    private Random random = new Random();

    private ParticleTexture texture;

    private boolean uniformScale = true;

    private Vector3f systemCenter;

    private int id;

    public ParticleSystem(Vector3f systemCenter, float pps, float speed, float gravityComplient, float lifeLength, float scale,
        boolean uniformScale, ParticleTexture texture) {
        this.pps = pps;
        this.averageSpeed = speed;
        this.gravityComplient = gravityComplient;
        this.averageLifeLength = lifeLength;
        this.averageScale = scale;
        this.uniformScale = uniformScale;
        this.texture = texture;
        this.systemCenter = systemCenter;
        this.id = idCount++;
    }

    /**
     * @param direction - The average direction in which particles are emitted.
     * @param deviation - A value between 0 and 1 indicating how far from the chosen direction particles can deviate.
     */
    public void setDirection(Vector3f direction, float deviation) {
        this.direction = new Vector3f(direction);
        this.directionDeviation = (float) (deviation * Math.PI);
    }

    public void randomizeRotation() {
        randomRotation = true;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setSpeedError(float error) {
        this.speedError = error * averageSpeed;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setLifeError(float error) {
        this.lifeError = error * averageLifeLength;
    }

    /**
     * @param error - A number between 0 and 1, where 0 means no error margin.
     */
    public void setScaleError(float error) {
        this.scaleError = error * averageScale;
    }

    public void generateParticles() {
        float delta = Timer.getDelta() / 1000.0f;
        float particlesToCreate = pps * delta;
        int count = (int) Math.floor(particlesToCreate);
        float partialParticle = particlesToCreate % 1;
        for (int i = 0; i < count; i++) {
            emitParticle(systemCenter);
        }
        if (Math.random() < partialParticle) {
            emitParticle(systemCenter);
        }
    }

    private void emitParticle(Vector3f center) {
        Vector3f velocity = null;
        if (direction != null) {
            velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
        } else {
            velocity = generateRandomUnitVector();
        }
        velocity.normalise();
        velocity.scale(generateValue(averageSpeed, speedError));
        Vector3f scale = null;
        if (uniformScale) {
            float value = generateValue(averageScale, scaleError);
            scale = new Vector3f(value, value, value);
        } else {
            scale =
                new Vector3f(generateValue(averageScale, scaleError), generateValue(averageScale, scaleError), generateValue(averageScale,
                    scaleError));
        }
        float lifeLength = generateValue(averageLifeLength, lifeError);
        new Particle(texture, new Vector3f(center), velocity, generateRotation(), scale, gravityComplient, lifeLength);
    }

    private float generateValue(float average, float errorMargin) {
        float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
        return average + offset;
    }

    private Vector3f generateRotation() {
        if (randomRotation) {
            return new Vector3f(0, 0, random.nextFloat() * 360f);
        } else {
            return new Vector3f(0, 0, 0);
        }
    }

    private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
        float cosAngle = (float) Math.cos(angle);
        Random random = new Random();
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));

        Vector4f direction = new Vector4f(x, y, z, 1);
        if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
            Vector3f rotateAxis = Vector3f.cross(coneDirection, new Vector3f(0, 0, 1), null);
            rotateAxis.normalise();
            float rotateAngle = (float) Math.acos(Vector3f.dot(coneDirection, new Vector3f(0, 0, 1)));
            Matrix4f rotationMatrix = new Matrix4f();
            rotationMatrix.rotate(-rotateAngle, rotateAxis);
            Matrix4f.transform(rotationMatrix, direction, direction);
        } else if (coneDirection.z == -1) {
            direction.z *= -1;
        }
        return new Vector3f(direction);
    }

    private Vector3f generateRandomUnitVector() {
        float theta = (float) (random.nextFloat() * 2f * Math.PI);
        float z = (random.nextFloat() * 2) - 1;
        float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
        float x = (float) (rootOneMinusZSquared * Math.cos(theta));
        float y = (float) (rootOneMinusZSquared * Math.sin(theta));
        return new Vector3f(x, y, z);
    }

    public Vector3f getSystemCenter() {
        return systemCenter;
    }

    public void setSystemCenter(Vector3f position) {
        this.systemCenter = position;
    }

    public int getId() {
        return id;
    }
}
