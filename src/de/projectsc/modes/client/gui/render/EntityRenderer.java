/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.render;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.AnimatedFrame;
import de.projectsc.core.data.utils.md5loader.MD5Loader;
import de.projectsc.core.data.utils.md5loader.MD5Processor;
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

        myModels = new HashMap<>();

        List<TexturedModel> newModels =
            MD5Processor.process(MD5Loader.loadMD5MeshFile("monster.md5mesh"), MD5Loader.loadMD5AnimFile("monster.md5anim"));
        for (TexturedModel m : newModels) {
            List<String> newList = new LinkedList<>();
            newList.add("md5");
            myModels.put(m, newList);
        }

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

        entitiesWithModel.putAll(myModels);
        position.put("md5", new Vector3f(0, 0, 0));
        rotations.put("md5", new Vector3f(-90, 0, -90));
        scales.put("md5", new Vector3f(0.5f, 0.5f, 0.5f));
        time += 15;
        if (time > 1000 / 10) {
            currentFrame++;
            if (currentFrame >= 120) {
                currentFrame = 0;
            }
            time = 0;
        }

        for (TexturedModel model : entitiesWithModel.keySet()) {
            prepareTexturedModel(model);
            if (model instanceof AnimatedModel) {
                GUISettings.disableCulling();
            }
            List<String> batch = entitiesWithModel.get(model);
            for (String e : batch) {
                if (position.get(e) != null && rotations.get(e) != null && scales.get(e) != null) {
                    Matrix4f[] frame = null;
                    if (model instanceof AnimatedModel) {
                        AnimatedFrame frame1 = ((AnimatedModel) model).getAnimatedFrames().get(currentFrame);
                        AnimatedFrame frame2 = ((AnimatedModel) model).getAnimatedFrames().get((currentFrame + 1) % 120);
                        frame = calculateInterpolatedFrame(frame1, frame2);
                    }
                    prepareInstance(model.getTexture(), position.get(e), rotations.get(e), scales.get(e), frame);
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
            }
            unbindTexturedModel();
        }
    }

    private Matrix4f[] calculateInterpolatedFrame(AnimatedFrame frame1, AnimatedFrame frame2) {
        Matrix4f[] result = new Matrix4f[frame1.getJointMatrices().length];
        for (int i = 0; i < frame1.getJointMatrices().length; i++) {
            Matrix4f m1 = frame1.getJointMatrices()[i];
            Matrix4f m2 = frame2.getJointMatrices()[i];
            Quaternion q1 = new Quaternion();
            Quaternion.setFromMatrix(m1, q1);
            Quaternion q2 = new Quaternion();
            Quaternion.setFromMatrix(m1, q2);

            result[i] = m1;
        }
        return result;
    }

    private Quaternion slerp(Quaternion q0, Quaternion q1, float alpha) {
        q0 = new Quaternion(q0);
        q1 = new Quaternion(q1);

        float dot = Quaternion.dot(q0, q1);

        float DOT_THRESHOLD = 0.9995f;
        if (dot > DOT_THRESHOLD)
            return lerp(q0, q1, alpha);

        dot = Math.max(Math.min(dot, -1), 1);
        float theta = (float) Math.acos(dot) * alpha;

        Quaternion q2 = Quaternion.sub(q1, (Quaternion) q0.scale(dot)).normalize();

        return q0.mult((float) Math.cos(theta)).add(q2.mult((float) Math.sin(theta)));
    }

    private Quaternion lerp(Quaternion q0, Quaternion q1, float alpha) {
        return (Quaternion) new Quaternion(q0.x + (q1.x - q0.x) * alpha,
            q0.y + (q1.y - q0.y) * alpha,
            q0.z + (q1.z - q0.z) * alpha,
            q0.w + (q1.w - q0.w) * alpha).normalise();
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

    private void prepareInstance(ModelTexture modelTexture, Vector3f position, Vector3f rotation, Vector3f scale, Matrix4f[] frameMatrices) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(position, rotation.x, rotation.y, rotation.z, scale);
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(getTextureOffsetX(modelTexture), getTextureOffsetY(modelTexture));
        if (frameMatrices != null) {
            shader.loadJointsMatrix(frameMatrices);
            shader.loadAnimated(true);
        } else {
            shader.loadAnimated(false);
        }
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
