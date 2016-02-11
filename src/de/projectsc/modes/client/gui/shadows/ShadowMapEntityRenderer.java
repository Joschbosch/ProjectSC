package de.projectsc.modes.client.gui.shadows;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.render.MasterRenderer;
import de.projectsc.modes.client.gui.textures.ModelTexture;

/**
 * Renders the shadow map for entities.
 * 
 * @author Josch Bosch
 */
public class ShadowMapEntityRenderer {

    private Matrix4f projectionViewMatrix;

    private ShadowShader shader;

    /**
     * @param shader - the simple shader program being used for the shadow render pass.
     * @param projectionViewMatrix - the orthographic projection matrix multiplied by the light's "view" matrix.
     */
    protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
        this.shader = shader;
        this.projectionViewMatrix = projectionViewMatrix;
    }

    /**
     * Renders entities to the shadow map. Each model is first bound and then all of the entities using that model are rendered to the
     * shadow map.
     * 
     * @param entitiesWithModel to rendet
     * @param position of entites
     * @param rotations of entites
     * @param scales of entites
     */
    public void render(Map<TexturedModel, List<String>> entitiesWithModel,
        Map<String, Vector3f> position, Map<String, Vector3f> rotations, Map<String, Vector3f> scales) {
        for (TexturedModel model : entitiesWithModel.keySet()) {
            RawModel rawModel = model.getRawModel();
            bindModel(rawModel);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
            if (model.getTexture().isTransparent()) {
                MasterRenderer.disableCulling();
            }
            List<String> batch = entitiesWithModel.get(model);
            for (String e : batch) {
                if (position.get(e) != null && rotations.get(e) != null && scales.get(e) != null) {
                    prepareInstance(model.getTexture(), position.get(e), rotations.get(e), scales.get(e));
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
            }
            if (model.getTexture().isTransparent()) {
                MasterRenderer.enableCulling();
            }
        }
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    /**
     * Binds a raw model before rendering. Only the attribute 0 is enabled here because that is where the positions are stored in the VAO,
     * and only the positions are required in the vertex shader.
     * 
     * @param rawModel - the model to be bound.
     */
    private void bindModel(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
    }

    /**
     * Prepares an entity to be rendered. The model matrix is created in the usual way and then multiplied with the projection and view
     * matrix (often in the past we've done this in the vertex shader) to create the mvp-matrix. This is then loaded to the vertex shader as
     * a uniform.
     * 
     * @param entity - the entity to be prepared for rendering.
     */
    private void prepareInstance(ModelTexture modelTexture, Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f modelMatrix =
            Maths.createTransformationMatrix(position, rotation.x, rotation.y, rotation.z, scale);
        Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
        shader.loadMvpMatrix(mvpMatrix);
    }

}
