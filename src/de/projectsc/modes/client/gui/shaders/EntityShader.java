/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.shaders;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.data.objects.Light;
import de.projectsc.modes.client.gui.objects.Camera;

/**
 * A shader program that does not much.
 * 
 * @author Josch Bosch
 */
public class EntityShader extends Shader {

    private static final int MAX_LIGHTS = 6;

    private static final Log LOGGER = LogFactory.getLog(EntityShader.class);

    private static final String VERTEX_FILE = "entityShader.vert";

    private static final String FRAGMENT_FILE = "entityShader.frag";

    private int locationTransformationMatrix;

    private int locationProjectionMatrix;

    private int locationViewMatrix;

    private int[] locationLightPositionEyeSpace;

    private int[] locationLightColor;

    private int[] locationAttenuation;

    private int locationShineDamper;

    private int locationUseFakeLighting;

    private int locationReflectivity;

    private int locationSkyColor;

    private int locationNumberOfRows;

    private int locationOffset;

    private int locationPlane;

    private int locationSelected;

    private int locationHighlighted;

    private int locationTextureSampler;

    private int locationNormalMap;

    private int locationHasNormalMap;

    public EntityShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        LOGGER.info("Static shader loaded.");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "tangents");
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationShineDamper = super.getUniformLocation("shineDamper");
        locationReflectivity = super.getUniformLocation("reflectivity");
        locationUseFakeLighting = super.getUniformLocation("useFakeLighting");
        locationSkyColor = super.getUniformLocation("skyColor");
        locationNumberOfRows = super.getUniformLocation("numberOfRows");
        locationOffset = super.getUniformLocation("offset");
        locationPlane = super.getUniformLocation("plane");
        locationTextureSampler = super.getUniformLocation("textureSampler");
        locationNormalMap = super.getUniformLocation("normalMap");
        locationHasNormalMap = super.getUniformLocation("hasNormalMap");

        locationLightPositionEyeSpace = new int[MAX_LIGHTS];
        locationLightColor = new int[MAX_LIGHTS];
        locationAttenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            locationLightPositionEyeSpace[i] = getUniformLocation(String.format("lightPositionEyeSpace[%s]", i));
            locationLightColor[i] = getUniformLocation(String.format("lightColor[%s]", i));
            locationAttenuation[i] = getUniformLocation(String.format("attenuation[%s]", i));
        }
        locationSelected = super.getUniformLocation("selected");
        locationHighlighted = super.getUniformLocation("highlighted");
    }

    /**
     * Connect textures.
     */
    public void connectTextureUnits() {
        super.loadInt(locationTextureSampler, 0);
        super.loadInt(locationNormalMap, 1);
    }

    /**
     * Load up if the entity has a normal map.
     * 
     * @param value true if it has a normal map.
     */
    public void loadHasNormalMap(boolean value) {
        loadBoolean(locationHasNormalMap, value);
    }

    /**
     * Load clipping plane to shader.
     * 
     * @param plane to upload.
     */
    public void loadClipPlane(Vector4f plane) {
        loadVector(locationPlane, plane);
    }

    /**
     * Loads the number of texture rows to the shader.
     * 
     * @param number to upload
     */
    public void loadNumberRows(int number) {
        loadFloat(locationNumberOfRows, number);
    }

    /**
     * Loads the texture offset to the shader.
     * 
     * @param x coordinate
     * @param y coordinate
     */
    public void loadOffset(float x, float y) {
        loadVector(locationOffset, new Vector2f(x, y));
    }

    /**
     * Give current color for the sky to the shader.
     * 
     * @param r value
     * @param g value
     * @param b value
     */
    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(locationSkyColor, new Vector3f(r, g, b));
    }

    /**
     * Loads whether lighting should be faked.
     * 
     * @param useFake ?
     */
    public void loadUseFakeLighting(boolean useFake) {
        super.loadBoolean(locationUseFakeLighting, useFake);
    }

    /**
     * Loads the transformation matrix.
     *
     * @param matrix to store the matrix in.
     */
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(locationTransformationMatrix, matrix);
    }

    /**
     * Loads the projection matrix.
     *
     * @param matrix to store the projection matrix.
     */
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(locationProjectionMatrix, matrix);
    }

    /**
     * Loads the current view matrix.
     *
     * @param camera for information
     */
    public void loadViewMatrix(Camera camera) {
        super.loadMatrix(locationViewMatrix, camera.createViewMatrix());
    }

    /**
     * Loads up lights to the shader.
     *
     * @param lights to load
     * @param viewMatrix for calculation
     */
    public void loadLights(List<Light> lights, Matrix4f viewMatrix) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                loadVector(locationLightPositionEyeSpace[i], getEyeSpacePosition(lights.get(i), viewMatrix));
                loadVector(locationLightColor[i], lights.get(i).getColor());
                loadVector(locationAttenuation[i], lights.get(i).getAttenuation());
            } else {
                loadVector(locationLightPositionEyeSpace[i], new Vector3f(0.0f, 0.0f, 0.0f));
                loadVector(locationLightColor[i], new Vector3f(0.0f, 0.0f, 0.0f));
                loadVector(locationAttenuation[i], new Vector3f(1.0f, 0.0f, 0.0f));
            }
        }

    }

    private Vector3f getEyeSpacePosition(Light light, Matrix4f viewMatrix) {
        Vector3f position = light.getPosition();
        Vector4f eyeSpacePos = new Vector4f(position.x, position.y, position.z, 1f);
        Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
        return new Vector3f(eyeSpacePos);
    }

    /**
     * Loads shiny values for the shader.
     * 
     * @param damper value
     * @param reflectivity value
     */
    public void loadShineValues(float damper, float reflectivity) {
        super.loadFloat(locationShineDamper, damper);
        super.loadFloat(locationReflectivity, reflectivity);
    }

    /**
     * @param highlighted entity
     * @param selected true, if entity is selected
     */
    public void loadSelected(boolean highlighted, boolean selected) {
        super.loadBoolean(locationHighlighted, highlighted);
        super.loadBoolean(locationSelected, selected);
    }
}
