package de.projectsc.modes.client.gui.objects.particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.modes.client.gui.utils.GUIConstants;

/**
 * One particle.
 * 
 * @author Josch Bosch
 */
public class Particle implements Comparable<Particle> {

    private Vector3f position;

    private Vector3f velocity;

    private Vector3f rotation;

    private Vector3f scale;

    private float gravityEffect;

    private float lifeLength;

    private float elapsedTime = 0;

    private ParticleTexture texture;

    private Vector2f texOffset1 = new Vector2f();

    private Vector2f texOffset2 = new Vector2f();

    private float blendFactor = 0;

    private float distance;

    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, Vector3f rotation, Vector3f scale, float gravityEffect,
        float lifeLength) {
        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        ParticleMaster.addParticle(this);
    }

    /**
     * Update particle.
     * 
     * @param deltaMs time since last frame.
     * @param camPosition for calculation.
     * @return if particle is still alive
     */
    public boolean update(float deltaMs, Vector3f camPosition) {
        velocity.y += GUIConstants.GRAVITY_PARTICLES.y * gravityEffect * deltaMs;
        Vector3f change = new Vector3f(velocity);
        change.scale(deltaMs);
        Vector3f.add(change, position, position);
        distance = Vector3f.sub(camPosition, position, null).lengthSquared();
        updateTextureCoordInfo();
        elapsedTime += deltaMs;
        return elapsedTime < lifeLength;
    }

    @Override
    public int compareTo(Particle arg0) {
        float dist = (this.distance - arg0.getDistance());
        if (dist > 0 && dist <= 1) {
            return -1;
        } else if (dist < 0 && dist >= -1) {
            return 1;
        } else {
            return (int) -(this.distance - arg0.getDistance());
        }
    }

    private void updateTextureCoordInfo() {
        float lifeFactor = elapsedTime / lifeLength;
        int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
        float atlasPrograssion = lifeFactor * stageCount;
        int index1 = (int) Math.floor(atlasPrograssion);
        int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
        this.blendFactor = atlasPrograssion % 1;
        setTextureOffset(texOffset1, index1);
        setTextureOffset(texOffset2, index2);
    }

    private void setTextureOffset(Vector2f offset, int index) {
        int column = index % texture.getNumberOfRows();
        int row = index / texture.getNumberOfRows();
        offset.x = (float) column / texture.getNumberOfRows();
        offset.y = (float) row / texture.getNumberOfRows();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    public Vector2f getTexOffset1() {
        return texOffset1;
    }

    public Vector2f getTexOffset2() {
        return texOffset2;
    }

    public float getBlendFactor() {
        return blendFactor;
    }

    public float getDistance() {
        return distance;
    }
}
