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
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.data.objects.Light;
import de.projectsc.modes.client.gui.objects.Camera;

/**
 * Shader only for the Terrain.
 * 
 * @author Josch Bosch
 */
public class TerrainShader extends Shader {

    private static final int MAX_LIGHTS = 6;

    private static final Log LOGGER = LogFactory.getLog(EntityShader.class);

    private static final String VERTEX_FILE = "terrainShader.vert";

    private static final String FRAGMENT_FILE = "terrainShader.frag";

    private int locationTransformationMatrix;

    private int locationProjectionMatrix;

    private int locationViewMatrix;

    private int[] locationLightPosition;

    private int[] locationLightColor;

    private int[] locationAttenuation;

    private int locationShineDamper;

    private int locationReflectivity;

    private int locationSkyColor;

    private int locationBackgroundTexture;

    private int locationRTexture;

    private int locationGTexture;

    private int locationBTexture;

    private int locationBlendMap;

    private int locationPlane;

    private int locationToShadowMapSpace;

    private int locationShadowMap;

    private int locationShadowDistance;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        LOGGER.info("Terrain shader loaded.");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");

    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationViewMatrix = super.getUniformLocation("viewMatrix");
        locationShineDamper = super.getUniformLocation("shineDamper");
        locationReflectivity = super.getUniformLocation("reflectivity");
        locationSkyColor = super.getUniformLocation("skyColor");
        locationBackgroundTexture = super.getUniformLocation("backgroundTexture");
        locationRTexture = super.getUniformLocation("rTexture");
        locationGTexture = super.getUniformLocation("gTexture");
        locationBTexture = super.getUniformLocation("bTexture");
        locationBlendMap = super.getUniformLocation("blendMap");
        locationPlane = super.getUniformLocation("plane");
        locationToShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        locationShadowMap = super.getUniformLocation("shadowMap");
        locationShadowDistance = super.getUniformLocation("shadowDistance");
        locationLightPosition = new int[MAX_LIGHTS];
        locationLightColor = new int[MAX_LIGHTS];
        locationAttenuation = new int[MAX_LIGHTS];
        for (int i = 0; i < MAX_LIGHTS; i++) {
            locationLightPosition[i] = getUniformLocation(String.format("lightPosition[%s]", i));
            locationLightColor[i] = getUniformLocation(String.format("lightColor[%s]", i));
            locationAttenuation[i] = getUniformLocation(String.format("attenuation[%s]", i));
        }

    }

    /**
     * Conntect the textures for the blend mapping.
     */
    public void connectTextureUnits() {
        super.loadInt(locationBackgroundTexture, 0);
        super.loadInt(locationRTexture, 1);
        super.loadInt(locationGTexture, 2);
        super.loadInt(locationBTexture, 3);
        super.loadInt(locationBlendMap, 4);
        super.loadInt(locationShadowMap, 5);
    }

    /**
     * Load up clipping plane.
     * 
     * @param plane to upload
     */
    public void loadClipPlane(Vector4f plane) {
        loadVector(locationPlane, plane);
    }

    /**
     * Load up matrix for shadows.
     * 
     * @param matrix to load.
     */
    public void loadToShadowSpaceMatrix(Matrix4f matrix) {
        super.loadMatrix(locationToShadowMapSpace, matrix);
    }

    /**
     * Load up shadow distance.
     * 
     * @param distance to load
     */
    public void loadlocationShadowDistance(float distance) {
        super.loadFloat(locationShadowDistance, distance);
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
     */
    public void loadLights(List<Light> lights) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                loadVector(locationLightPosition[i], lights.get(i).getPosition());
                loadVector(locationLightColor[i], lights.get(i).getColor());
                loadVector(locationAttenuation[i], lights.get(i).getAttenuation());

            } else {
                loadVector(locationLightPosition[i], new Vector3f(0.0f, 0.0f, 0.0f));
                loadVector(locationLightColor[i], new Vector3f(0.0f, 0.0f, 0.0f));
                loadVector(locationAttenuation[i], new Vector3f(1.0f, 0.0f, 0.0f));

            }
        }

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

}
