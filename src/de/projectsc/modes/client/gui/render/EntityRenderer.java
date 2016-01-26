/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.render;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.ModelData;
import de.projectsc.core.data.utils.OBJFileLoader;
import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.shaders.EntityShader;
import de.projectsc.modes.client.gui.textures.ModelTexture;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * This class will get {@link GraphicalEntity} objects to render onto the screen.
 * 
 * @author Josch Bosch
 */
public class EntityRenderer {

    private final EntityShader shader;

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
     * @param selected entities
     * @param highlighted entities
     */
    public void render(Map<TexturedModel, List<String>> entitiesWithModel,
        Map<String, Vector3f> position, Map<String, Vector3f> rotations, Map<String, Vector3f> scales,
        List<String> selected, List<String> highlighted) {
        ModelData loadOBJFromResources = OBJFileLoader.loadOBJFromResources("/meshes/objects/boulder.obj");
        TexturedModel barrel =
            new TexturedModel(Loader.loadToVAO(loadOBJFromResources.getVertices(), loadOBJFromResources.getTextureCoords(),
                loadOBJFromResources.getNormals(), loadOBJFromResources.getTangents(), loadOBJFromResources.getIndices()),
                new ModelTexture(
                    Loader.loadTexture("objects/boulder.png")));
        barrel.getTexture().setNormalMap(Loader.loadTexture("objects/boulderNormal.png"));
        barrel.getTexture().setShineDamper(10);
        barrel.getTexture().setReflectivity(0.5f);
        List<String> barrels = entitiesWithModel.get(barrel);
        if (barrels == null) {
            barrels = new LinkedList<>();
        }
        barrels.add("0");
        entitiesWithModel.put(barrel, barrels);
        position.put("0", new Vector3f(0, 20, 0));
        rotations.put("0", new Vector3f());
        scales.put("0", new Vector3f(0.8f, 0.8f, 0.8f));

        for (TexturedModel model : entitiesWithModel.keySet()) {
            prepareTexturedModel(model);
            List<String> batch = entitiesWithModel.get(model);
            for (String e : batch) {
                if (position.get(e) != null && rotations.get(e) != null && scales.get(e) != null) {
                    prepareInstance(model.getTexture(), position.get(e), rotations.get(e), scales.get(e), highlighted.contains(e),
                        selected.contains(e));
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
            }
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModel(TexturedModel tModel) {
        RawModel model = tModel.getRawModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        ModelTexture texture = tModel.getTexture();
        if (texture.isTransparent()) {
            MasterRenderer.disableCulling();
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
        MasterRenderer.enableCulling();
    }

    private void prepareInstance(ModelTexture modelTexture, Vector3f position, Vector3f rotation, Vector3f scale, boolean isSelected,
        boolean isHighlighted) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(position, rotation.x, rotation.y, rotation.z, scale);
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(getTextureOffsetX(modelTexture), getTextureOffsetY(modelTexture));
        shader.loadSelected(isHighlighted, isSelected);

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
