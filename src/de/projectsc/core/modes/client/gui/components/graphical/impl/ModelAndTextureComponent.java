/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.gui.components.graphical.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.data.ModelData;
import de.projectsc.core.data.OBJFileLoader;
import de.projectsc.core.data.entities.ComponentType;
import de.projectsc.core.data.entities.Entity;
import de.projectsc.core.modes.client.gui.components.GraphicalComponent;
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

    private RawModel model;

    private ModelTexture modelTexture;

    private int textureIndex = 0;

    public ModelAndTextureComponent(Entity owner) {
        super(NAME, owner);
        textureIndex = 0;
        type = ComponentType.GRAPHICS;
    }

    public ModelAndTextureComponent(int textureIndex, Entity owner) {
        super(NAME, owner);
        this.textureIndex = textureIndex;
        type = ComponentType.GRAPHICS;

    }

    @Override
    public void update(Entity owner) {

    }

    @Override
    public void render(Scene scene) {
        TexturedModel entityModel = getTexturedModel();
        modelTexture.setActiveTextureIndex(textureIndex);
        List<Entity> batch = scene.getEntities().get(entityModel);
        if (batch != null) {
            batch.add(owner);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(owner);
            scene.getEntities().put(entityModel, newBatch);
        }
    }

    /**
     * Load model and texture from given files.
     * 
     * @param modelFile model file
     * @param textureFile texture image
     */
    public void loadModel(File modelFile, File textureFile) {
        if (modelFile != null) {
            ModelData data = OBJFileLoader.loadOBJ(modelFile);
            model = Loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
            loadAndApplyTexture(textureFile);
        }
    }

    /**
     * Load model and texture using the owners id for the path.
     * 
     * @param owner with the id.
     */
    public void loadModel(Entity owner) {
        try {
            String filePath =
                String.format("/%s/%s%d", CoreConstants.SCHEME_DIRECTORY_NAME, CoreConstants.SCHEME_DIRECTORY_PREFIX,
                    owner.getEntityTypeId());

            File pathToSchema = new File(this.getClass().getResource(filePath).toURI());

            ModelData data = OBJFileLoader.loadOBJ(filePath);
            model = Loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
            File textureFile = new File(pathToSchema, CoreConstants.TEXTURE_FILENAME);
            if (textureFile.exists()) {
                loadAndApplyTexture(textureFile);
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
     * @param textureFile to load
     */
    public void loadAndApplyTexture(File textureFile) {
        if (textureFile != null) {
            int texture = 0 - 1;
            texture = Loader.loadTexture(textureFile);
            if (modelTexture == null) {
                modelTexture = new ModelTexture(texture);
            } else {
                modelTexture.setTextureID(texture);
            }
        }
    }

    @Override
    public void remove() {
        owner.setMoveable(false);
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
    public void deserialize(JsonNode input, File schemaDir) {

    }

    @Override
    public boolean isValidForSaving() {
        return model != null;
    }
}
