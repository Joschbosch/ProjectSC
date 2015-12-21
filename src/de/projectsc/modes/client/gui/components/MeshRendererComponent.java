/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.components;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.impl.physic.MeshComponent;
import de.projectsc.core.data.physics.ModelData;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.modes.client.gui.GUIConstants;
import de.projectsc.modes.client.gui.data.GUIScene;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.models.TexturedModel;
import de.projectsc.modes.client.gui.textures.ModelTexture;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Entity component to add a model and a texture to the entity.
 * 
 * @author Josch Bosch
 * 
 */
public class MeshRendererComponent extends GraphicalComponent {

    /**
     * Name.
     */
    public static final String NAME = "Mesh Renderer Component";

    private static final Log LOGGER = LogFactory.getLog(MeshRendererComponent.class);

    private File textureFile;

    private RawModel model;

    private ModelTexture modelTexture;

    private int textureIndex = 0;

    private TexturedModel texturedModel;

    public MeshRendererComponent() {
        setID(NAME);
        setType(ComponentType.GRAPHICS);
        textureIndex = 0;
        requiredComponents.add(MeshComponent.NAME);
        try {
            textureFile =
                new File(MeshRendererComponent.class.getResource(GUIConstants.TEXTURE_ROOT + GUIConstants.BASIC_TEXTURE_WHITE)
                    .toURI());
        } catch (URISyntaxException e) {
            LOGGER.error("Could not load default texture file: ", e);
        }
    }

    @Override
    public void update(long owner) {
        if (model == null && EntityManager.hasComponent(owner, MeshComponent.class)) {
            ModelData mesh = ((MeshComponent) EntityManager.getComponent(owner, MeshComponent.class)).getModel();
            if (mesh != null) {
                loadModel(mesh);
            }
        }
        if (modelTexture == null && textureFile != null) {
            loadAndApplyTexture(textureFile);
        }
        if (texturedModel == null) {
            texturedModel = getTexturedModel();
        }
    }

    @Override
    public void render(long entity, GUIScene scene) {
        TexturedModel m = getTexturedModel();
        if (m != null && m.getRawModel() != null && m.getTexture() != null) {
            if (modelTexture != null) {
                modelTexture.setActiveTextureIndex(textureIndex);
            }
            List<Long> batch = scene.getModels().get(m);
            if (batch != null) {
                batch.add(entity);
            } else {
                List<Long> newBatch = new ArrayList<>();
                newBatch.add(entity);
                scene.getModels().put(m, newBatch);
            }
        }
    }

    /**
     * Load model and texture from given files.
     * 
     * @param data of the mesh
     */
    public void loadModel(ModelData data) {
        model = Loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        File savedTextureFile = new File(savingLocation, CoreConstants.TEXTURE_FILENAME);
        if (textureFile != null && textureFile.exists() && !savedTextureFile.exists()) {
            try {
                FileUtils.copyFile(textureFile, savedTextureFile);
            } catch (IOException e) {
                LOGGER.error("Could not save texture file: " + e.getMessage());
            }
        }
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("numColumns", modelTexture.getNumberOfRows());
        serialized.put("reflectivity", modelTexture.getReflectivity());
        serialized.put("shineDamper", modelTexture.getShineDamper());
        return serialized;
    }

    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {
        if (new File(loadingLocation, CoreConstants.TEXTURE_FILENAME).exists()) {
            textureFile = new File(loadingLocation, CoreConstants.TEXTURE_FILENAME);
        }
        setNumberOfRows((int) serialized.get("numColumns"));
        setReflectivity((float) (double) serialized.get("reflectivity"));
        setShineDamper((float) (double) serialized.get("shineDamper"));
    }

    /**
     * loads and applies the given texture file.
     * 
     * @param incTextureFile to load
     */
    public void loadAndApplyTexture(File incTextureFile) {
        if (incTextureFile != null) {
            this.textureFile = incTextureFile;
            int texture = 0 - 1;
            texture = Loader.loadTexture(incTextureFile);
            if (modelTexture == null) {
                modelTexture = new ModelTexture(texture);
            } else {
                modelTexture.setTextureID(texture);
            }
        }
    }

    @Override
    public void remove() {}

    /**
     * 
     * @param value if the texture is transparent.
     */
    public void setIsTransparent(boolean value) {
        if (modelTexture != null) {
            modelTexture.setTransparent(value);
        }
    }

    /**
     * @param value if the model uses fake lighting
     */
    public void setFakeLighting(boolean value) {
        if (modelTexture != null) {
            modelTexture.setFakeLighting(value);
        }
    }

    /**
     * @param value for the shine damper
     */
    public void setShineDamper(float value) {
        if (modelTexture != null) {
            modelTexture.setShineDamper(value);
        }
    }

    /**
     * @param value for the reflectivity
     */
    public void setReflectivity(float value) {
        if (modelTexture != null) {
            modelTexture.setReflectivity(value);
        }
    }

    /**
     * 
     * @param value number of rows in the texture file
     */
    public void setNumberOfRows(int value) {
        if (modelTexture != null) {
            modelTexture.setNumberOfRows(value);
        }
    }

    public TexturedModel getTexturedModel() {
        return new TexturedModel(model, modelTexture);
    }

    public RawModel getModel() {
        return model;
    }

    public ModelTexture getModelTexture() {
        return modelTexture;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    @Override
    public boolean isValidForSaving() {
        return model != null;
    }

    /**
     * 
     * @return reflectivity, if a texture is attached.
     */
    public float getReflectivity() {
        if (modelTexture != null) {
            return modelTexture.getReflectivity();
        } else {
            return 1;
        }
    }

}
