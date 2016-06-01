/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.animation.AnimatedFrame;
import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.AnimatedModel;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.settings.GUISettings;
import de.projectsc.modes.client.gui.shaders.EntityShader;
import de.projectsc.modes.client.gui.textures.ModelTexture;

/**
 * This class will get {@link GraphicalEntity} objects to render onto the screen.
 * 
 * @author Josch Bosch
 */
public class EntityRenderer {

    private final EntityShader shader;

    private HashMap<TexturedModel, List<String>> myModels;

    private int currentFrame = 0;

    private int time = 0;

    public EntityRenderer(EntityShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();

    }

    /**
     * Renders all textured models without switching vaos to often.
     * 
     * @param entitiesWithModel to render.
     * @param position to render.
     * @param rotations to render.
     * @param scales to render.
     */
    public void render(Map<TexturedModel, List<String>> entitiesWithModel,
        Map<String, Vector3f> position, Map<String, Vector3f> rotations, Map<String, Vector3f> scales) {
        float fps = 1;
        int maxFrames = 3;
        time += 15;
        if (time > 1000 / fps) {
            currentFrame++;
            if (currentFrame >= maxFrames) {
                currentFrame = 0;
            }
            time = (int) (time % (1000 / fps));
        }
        float delta = time / (1000.0f / fps);
        for (TexturedModel model : entitiesWithModel.keySet()) {
            prepareTexturedModel(model);
            if (model instanceof AnimatedModel) {
                GUISettings.disableCulling();
            }
            List<String> batch = entitiesWithModel.get(model);
            for (String e : batch) {
                if (position.get(e) != null && rotations.get(e) != null && scales.get(e) != null) {
                    Matrix4f[] frame = null;
                    if (model instanceof AnimatedModel && ((AnimatedModel) model).getAnimatedFrames() != null) {
                        AnimatedFrame frame1 = ((AnimatedModel) model).getAnimatedFrames().get(currentFrame);
                        AnimatedFrame frame2 = ((AnimatedModel) model).getAnimatedFrames().get((currentFrame + 1) % maxFrames);
                        frame = calculateInterpolatedFrame(frame1, frame2, delta);
                        prepareInstance(model.getTexture(), position.get(e), rotations.get(e), scales.get(e), frame);
                    } else if (model instanceof AnimatedModel && ((AnimatedModel) model).getAnimationController() != null) {
                        prepareInstance(model.getTexture(), position.get(e), rotations.get(e), scales.get(e), ((AnimatedModel) model)
                            .getAnimationController().getJointMatrices());
                    } else {
                        prepareInstance(model.getTexture(), position.get(e), rotations.get(e), scales.get(e));
                    }
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
            }
            unbindTexturedModel();
        }
    }

    private Matrix4f[] calculateInterpolatedFrame(AnimatedFrame frame1, AnimatedFrame frame2, float delta) {
        Matrix4f[] result = new Matrix4f[frame1.getJointMatrices().length];
        for (int i = 0; i < frame1.getJointMatrices().length; i++) {
            Matrix4f m1 = new Matrix4f(frame1.getJointMatrices()[i]);
            Matrix4f m2 = new Matrix4f(frame2.getJointMatrices()[i]);
            Vector3f position1 = new Vector3f(m1.m30, m1.m31, m1.m32);
            Vector3f position2 = new Vector3f(m2.m30, m2.m31, m2.m32);
            Quaternion q1 = new Quaternion();
            Quaternion.setFromMatrix(m1, q1);
            Quaternion q2 = new Quaternion();
            Quaternion.setFromMatrix(m2, q2);
            Quaternion interpolatedRotation = Maths.slerp(q1, q2, delta);

            Vector3f interpolatedPostion = Maths.lerp(position1, position2, delta);

            result[i] = Maths.createTransformationMatrix(interpolatedRotation, interpolatedPostion, new Vector3f(1, 1, 1));
        }
        return result;
    }

    private void prepareTexturedModel(TexturedModel tModel) {
        RawModel model = tModel.getRawModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        if (tModel instanceof AnimatedModel) {
            GL20.glEnableVertexAttribArray(4);
            GL20.glEnableVertexAttribArray(5);
        }

        ModelTexture texture = tModel.getTexture();
        if (texture.isTransparent()) {
            GUISettings.disableCulling();
        }
        shader.loadNumberRows(texture.getNumberOfRows());
        shader.loadShineValues(texture.getShineDamper(), texture.getReflectivity());
        shader.loadUseFakeLighting(texture.isFakeLighting());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
        if (texture.getNormalMap() != -1) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getNormalMap());
        }
        shader.loadHasNormalMap(texture.getNormalMap() != -1);
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);
        GUISettings.enableCulling();
    }

    private void prepareInstance(ModelTexture modelTexture, Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(position, rotation.x, rotation.y, rotation.z, scale);
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(getTextureOffsetX(modelTexture), getTextureOffsetY(modelTexture));
        shader.loadAnimated(false);
    }

    private void prepareInstance(ModelTexture modelTexture, Vector3f position, Vector3f rotation, Vector3f scale,
        Matrix4f[] jointMatrices) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(position, rotation.x, rotation.y, rotation.z, scale);
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(getTextureOffsetX(modelTexture), getTextureOffsetY(modelTexture));
        shader.loadJointsMatrix(jointMatrices);
        shader.loadAnimated(true);
    }

    /**
     * Returns the X offset for a texture map.
     * 
     * @param modelTexture to get the values from
     * 
     * @return position offset of the texture
     */
    public float getTextureOffsetX(ModelTexture modelTexture) {
        int column = modelTexture.getActiveTextureIndex() % modelTexture.getNumberOfRows();
        return (column / (float) modelTexture.getNumberOfRows());
    }

    /**
     * Returns the Y offset for a texture map.
     * 
     * @param modelTexture to get the values from
     * 
     * @return position offset of the texture
     */
    public float getTextureOffsetY(ModelTexture modelTexture) {
        int row = modelTexture.getActiveTextureIndex() / modelTexture.getNumberOfRows();
        return (row / (float) modelTexture.getNumberOfRows());
    }

}
