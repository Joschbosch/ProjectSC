/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.gui.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.data.ModelData;
import de.projectsc.core.data.OBJFileLoader;
import de.projectsc.core.entities.ComponentType;
import de.projectsc.core.modes.client.gui.data.Scene;
import de.projectsc.core.modes.client.gui.models.RawModel;
import de.projectsc.core.modes.client.gui.models.TexturedModel;
import de.projectsc.core.modes.client.gui.textures.ModelTexture;
import de.projectsc.core.modes.client.gui.utils.Loader;

/**
 * Entity component to add a model and a texture to the entity.
 * 
 * @author Josch Bosch
 * 
 */
public class ModelAndTextureComponent extends GraphicalComponent {

    /**
     * Name.
     */
    public static final String NAME = "Model and Texture Component";

    private static final Log LOGGER = LogFactory.getLog(ModelAndTextureComponent.class);

    private File modelFile;

    private File textureFile;

    private RawModel model;

    private ModelTexture modelTexture;

    private int textureIndex = 0;

    private float scale = 1.0f;

    public ModelAndTextureComponent() {
        setID(NAME);
        setType(ComponentType.GRAPHICS);
        textureIndex = 0;
    }

    @Override
    public void update(long owner) {

    }

    @Override
    public void render(long entity, Scene scene) {
        TexturedModel entityModel = getTexturedModel();
        if (entityModel.getRawModel() != null) {
            if (modelTexture != null) {
                modelTexture.setActiveTextureIndex(textureIndex);
            }
            List<Long> batch = scene.getModels().get(entityModel);
            if (batch != null) {
                batch.add(entity);
            } else {
                List<Long> newBatch = new ArrayList<>();
                newBatch.add(entity);
                scene.getModels().put(entityModel, newBatch);
            }
        }
        scene.getScales().put(entity, scale);
    }

    /**
     * Load model and texture from given files.
     * 
     * @param incModelFile model file
     * @param incTextureFile texture image
     */
    public void loadModel(File incModelFile, File incTextureFile) {
        if (incModelFile != null) {
            this.modelFile = incModelFile;
            ModelData data = OBJFileLoader.loadOBJ(incModelFile);
            model = Loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
            if (incTextureFile != null) {
                this.textureFile = incTextureFile;
                loadAndApplyTexture(incTextureFile);
            }
        }
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        File savedModelFile = new File(savingLocation, CoreConstants.MODEL_FILENAME);
        if (modelFile != null && modelFile.exists() && !savedModelFile.exists()) {
            try {
                FileUtils.copyFile(modelFile, savedModelFile);
            } catch (IOException e) {
                LOGGER.error("Could not save model file: " + e.getMessage());
            }
        }
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
        serialized.put("scale", scale);
        return serialized;
    }

    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {
        if (new File(loadingLocation, CoreConstants.MODEL_FILENAME).exists()) {
            modelFile = new File(loadingLocation, CoreConstants.MODEL_FILENAME);
        }
        if (new File(loadingLocation, CoreConstants.TEXTURE_FILENAME).exists()) {
            textureFile = new File(loadingLocation, CoreConstants.TEXTURE_FILENAME);
        }
        if (modelFile != null) {
            loadModel(modelFile, textureFile);
        }
        setNumberOfRows((int) serialized.get("numColumns"));
        setReflectivity((float) (double) serialized.get("reflectivity"));
        setShineDamper((float) (double) serialized.get("shineDamper"));
        setScale((float) (double) serialized.get("scale"));
    }

    public void setScale(float scale) {
        this.scale = scale;
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

    public float getScale() {
        return scale;
    }

    public float getReflectivity() {
        if (modelTexture != null) {
            return modelTexture.getReflectivity();
        } else {
            return 1;
        }
    }

}
