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
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.data.physics.ModelData;
import de.projectsc.core.data.utils.OBJFileLoader;
import de.projectsc.core.interfaces.Component;

/**
 * Component that represents the mesh an entity uses.
 * 
 * @author Josch Bosch
 */
public class MeshComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Mesh Component";

    private static final Log LOGGER = LogFactory.getLog(MeshComponent.class);

    private String modelPath;

    private ModelData model;

    public MeshComponent() {
        setType(ComponentType.PHYSICS);
        setComponentName(NAME);
    }

    @Override
    public boolean isValidForEntitySaving() {
        return model != null;
    }

    /**
     * Load up the model of a mesh.
     */
    public void loadModel() {
        if (modelPath != null) {
            loadModel(modelPath);
        }
    }

    /**
     * Load model and texture from given files.
     * 
     * @param incModelPath model file
     */
    public void loadModel(String incModelPath) {
        if (incModelPath != null) {
            this.modelPath = incModelPath;
            if (new File(modelPath).exists()) {
                model = OBJFileLoader.loadOBJFromFileSystem(incModelPath);
            } else {
                model = OBJFileLoader.loadOBJFromSchema(incModelPath);
            }
        }
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        File savedModelFile = new File(savingLocation, CoreConstants.MODEL_FILENAME);
        if (modelPath != null && !savedModelFile.exists()) {
            try {
                FileUtils.copyFile(new File(modelPath), savedModelFile);
                LOGGER.info(String.format("Copied mesh file from %s to %s.", modelPath, savedModelFile));
            } catch (IOException e) {
                LOGGER.error("Could not save model file: " + e.getMessage());
            }
        }
        return new HashMap<>();
    }

    @Override
    public void deserialize(Map<String, Object> serialized, String loadingLocation) {
        modelPath = loadingLocation + "/" + CoreConstants.MODEL_FILENAME;
        if (modelPath != null) {
            loadModel(modelPath);
        }
    }

    @Override
    public Component cloneComponent() {
        MeshComponent mc = new MeshComponent();
        mc.setModel(model);
        mc.setModelPath(modelPath);
        return mc;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public void setModel(ModelData model) {
        this.model = model;
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
        modelPath = newModel.getAbsolutePath();
        model = OBJFileLoader.loadOBJFromFileSystem(newModel.getAbsolutePath());
    }

}
