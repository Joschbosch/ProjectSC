/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.components;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Billboard;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.objects.ParticleEmitter;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.utils.BoundingBox;

/**
 * 
 * Abstract class for entity components.
 * 
 * @author Josch Bosch
 */
public abstract class Component {

    protected String componentName;

    protected ComponentType type;

    protected ObjectMapper mapper = new ObjectMapper();

    protected Entity owner;

    public Component(String newName, Entity owner) {
        this.componentName = newName;
        this.owner = owner;
    }

    /**
     * Update method for the component.
     * 
     * @param ownerEntity entity that has this component.
     */
    public abstract void update(Entity ownerEntity);

    /**
     * @return true, if the component has all attributes to be saved.
     */
    public abstract boolean isValidForSaving();

    /**
     * Adds everything for rendering.
     * 
     * @param ownerEntity of component
     * @param entities all others
     * @param boundingBoxes to render
     * @param lights to render
     * @param billboards to render
     * @param particles to render
     * @param camera for positions
     * @param elapsedTime since last frame
     */
    public abstract void render(Entity ownerEntity, Map<TexturedModel, List<Entity>> entities,
        Map<RawModel, List<BoundingBox>> boundingBoxes, List<Light> lights, List<Billboard> billboards, List<ParticleEmitter> particles,
        Camera camera, long elapsedTime);

    /**
     * 
     * @return serialized string
     * @throws JsonGenerationException e
     * @throws JsonMappingException e
     * @throws IOException e
     */
    public abstract String serialize() throws JsonGenerationException, JsonMappingException, IOException;

    /**
     * 
     * @param input to deserialize
     * @param schemaDir directory of schema
     * @throws JsonProcessingException e
     * @throws IOException e
     */
    public abstract void deserialize(JsonNode input, File schemaDir) throws JsonProcessingException, IOException;

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public String getComponentName() {
        return componentName;
    }

}
