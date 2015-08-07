/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.components.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.textures.ModelTexture;
import de.projectsc.client.gui.tools.Loader;
import de.projectsc.client.gui.tools.ModelData;
import de.projectsc.client.gui.tools.NewOBJFileLoader;
import de.projectsc.core.CoreConstants;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.entities.Entity;

/**
 * Entity component to add a model and a texture to the entity.
 * 
 * @author Josch Bosch
 * 
 */
public class ModelAndTextureComponent extends Component {

    /**
     * Name.
     */
    public static final String NAME = "Model and Texture Component";

    private static final Log LOGGER = LogFactory.getLog(ModelAndTextureComponent.class);

    private RawModel model;

    private ModelTexture modelTexture;

    private int textureIndex = 0;

    public ModelAndTextureComponent() {
        super(NAME);
        textureIndex = 0;
        type = ComponentType.GRAPHICS;
    }

    public ModelAndTextureComponent(int textureIndex) {
        super(NAME);
        this.textureIndex = textureIndex;
        type = ComponentType.GRAPHICS;

    }

    @Override
    public void update(Entity owner) {

    }

    /**
     * Returns the X offset for a texture map.
     * 
     * @return position
     */
    public float getTextureOffsetX() {
        int column = textureIndex % modelTexture.getNumberOfRows();
        return (column / (float) modelTexture.getNumberOfRows());
    }

    /**
     * Returns the Y offset for a texture map.
     * 
     * @return position
     */
    public float getTextureOffsetY() {
        int row = textureIndex / modelTexture.getNumberOfRows();
        return (row / (float) modelTexture.getNumberOfRows());
    }

    /**
     * Load model and texture from given files.
     * 
     * @param loader to load
     * @param modelFile model file
     * @param textureFile texture image
     */
    public void loadModel(Loader loader, File modelFile, File textureFile) {
        if (modelFile != null) {
            ModelData data = NewOBJFileLoader.loadOBJ(modelFile);
            model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
            loadAndApplyTexture(loader, textureFile);
        }
    }

    /**
     * Load model and texture using the owners id for the path.
     * 
     * @param loader to load
     * @param owner with the id.
     */
    public void loadModel(Loader loader, Entity owner) {
        try {
            String filePath =
                String.format("/%s/%s%d", CoreConstants.MODEL_DIRECTORY_NAME, CoreConstants.MODEL_DIRECTORY_PREFIX,
                    owner.getEntityTypeId());

            File pathToSchema = new File(this.getClass().getResource(filePath).toURI());

            ModelData data = NewOBJFileLoader.loadOBJ(filePath);
            model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
            File textureFile = new File(pathToSchema, CoreConstants.TEXTURE_FILENAME);
            if (textureFile.exists()) {
                loadAndApplyTexture(loader, textureFile);
            }
            // load texture settings for this model
            ObjectMapper mapper = new ObjectMapper();
            JsonNode tree = mapper.readTree(new File(filePath, CoreConstants.ENTITY_FILENAME));
            setNumberOfRows(tree.get("numColumns").getIntValue());
            setReflectivity((float) tree.get("reflectivity").getDoubleValue());
            setShineDamper((float) tree.get("shineDamper").getDoubleValue());
            owner.setScale((float) tree.get("scale").getDoubleValue());
        } catch (URISyntaxException | IOException e) {
            LOGGER.error("Could not load schema for id " + owner.getEntityTypeId(), e);
        }
    }

    /**
     * loads and applys the given texture file.
     * 
     * @param loader to load
     * @param textureFile to load
     */
    public void loadAndApplyTexture(Loader loader, File textureFile) {
        if (textureFile != null) {
            int texture = 0 - 1;
            texture = loader.loadTexture(textureFile);
            if (modelTexture == null) {
                modelTexture = new ModelTexture(texture);
            } else {
                modelTexture.setTextureID(texture);
            }
        }
    }

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
    public String serialize() {
        return null;
    }

    @Override
    public void deserialize(JsonNode input) {

    }

    @Override
    public boolean isValidForSaving() {
        return model != null;
    }
}
