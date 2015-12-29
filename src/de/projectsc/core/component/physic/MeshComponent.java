/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.physic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.ComponentType;
import de.projectsc.core.data.physics.ModelData;
import de.projectsc.core.data.utils.OBJFileLoader;

/**
 * Component that represents the mesh an entity uses.
 * 
 * @author Josch Bosch
 */
public class MeshComponent extends PhysicsComponent {

    /**
     * ID.
     */
    public static final String NAME = "Mesh Component";

    private static final Log LOGGER = LogFactory.getLog(MeshComponent.class);

    private File modelFile;

    private ModelData model;

    public MeshComponent() {
        setType(ComponentType.PHYSICS);
        setComponentName(NAME);
    }

    @Override
    public void update(long elapsed) {
        if (model == null && modelFile != null) {
            loadModel(modelFile);
        }
    }

    @Override
    public boolean isValidForSaving() {
        return model != null;
    }

    /**
     * Load model and texture from given files.
     * 
     * @param incModelFile model file
     */
    public void loadModel(File incModelFile) {
        if (incModelFile != null) {
            this.modelFile = incModelFile;
            model = OBJFileLoader.loadOBJ(incModelFile);
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
        return new HashMap<>();
    }

    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {
        if (new File(loadingLocation, CoreConstants.MODEL_FILENAME).exists()) {
            modelFile = new File(loadingLocation, CoreConstants.MODEL_FILENAME);
        }
        if (modelFile != null) {
            loadModel(modelFile);
        }
    }

    public ModelData getModel() {
        return model;
    }

    /**
     * Change the mesh model.
     * 
     * @param newModel to change to.
     */
    public void changeMesh(File newModel) {
        this.modelFile = newModel;
        loadModel(newModel);
    }

    public void setModelFile(File modelFile) {
        this.modelFile = modelFile;
    }

    public void setModel(ModelData model) {
        this.model = model;
    }
}
