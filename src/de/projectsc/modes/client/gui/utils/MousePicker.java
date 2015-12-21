/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.utils;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.terrain.Terrain;

/**
 * Calculates the vertex on the terrain from the mouse cursor.
 * 
 * @author Josch Bosch
 */
public class MousePicker {

    private static final int RECURSION_COUNT = 200;

    private static final float RAY_RANGE = 600;

    private Vector3f currentRay = new Vector3f();

    private final Matrix4f projectionMatrix;

    private Matrix4f viewMatrix;

    private Vector3f currentTerrainPoint;

    private List<Terrain> terrains;

    private Terrain currentTerrain;

    private Vector3f cameraPosition;

    public MousePicker(Matrix4f projection) {
        projectionMatrix = projection;
    }

    public Vector3f getCurrentTerrainPoint() {
        return currentTerrainPoint;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    /**
     * Update picker to current camera position.
     * 
     * @param currentTerrains all terrains in the map to this point
     * @param currentCameraPosition current position of the camera to calculate the ray.
     * @param currentViewMatrix to calculate the ray
     */
    public void update(List<Terrain> currentTerrains, Vector3f currentCameraPosition, Matrix4f currentViewMatrix) {
        this.terrains = currentTerrains;
        viewMatrix = currentViewMatrix;
        cameraPosition = currentCameraPosition;
        currentRay = calculateMouseRay();
        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
        } else {
            currentTerrainPoint = null;
        }
    }

    private Vector3f calculateMouseRay() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, 0 - 1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
        Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalise();
        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
        Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
        return new Vector4f(eyeCoords.x, eyeCoords.y, 0 - 1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / Display.getWidth() - 1f;
        float y = (2.0f * mouseY) / Display.getHeight() - 1f;
        return new Vector2f(x, y);
    }

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f start = new Vector3f(cameraPosition.x, cameraPosition.y, cameraPosition.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return Vector3f.add(start, scaledRay, null);
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            Terrain terrainSearch = getTerrain(endPoint.getX(), endPoint.getZ());
            if (terrainSearch != null) {
                currentTerrain = terrainSearch;
                return endPoint;
            } else {
                currentTerrain = null;
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        return !isUnderGround(startPoint) && isUnderGround(endPoint);
    }

    private boolean isUnderGround(Vector3f testPoint) {
        Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
        float height = 0;
        if (terrain != null) {
            height = terrain.getHeightOfTerrain((int) testPoint.getX(), (int) testPoint.getZ());
        }
        return testPoint.y < height;
    }

    private Terrain getTerrain(float worldX, float worldZ) {
        for (Terrain t : terrains) {
            if (worldX >= t.getWorldPositionX() && worldZ >= t.getWorldPositionZ()
                && worldX < t.getWorldPositionX() + Terrain.TERRAIN_TILE_SIZE * Terrain.TERRAIN_CHUNK_SIZE
                && worldZ < t.getWorldPositionZ() + Terrain.TERRAIN_TILE_SIZE * Terrain.TERRAIN_CHUNK_SIZE) {
                return t;
            }
        }
        return null;
    }

    public Terrain getCurrentTerrain() {
        return currentTerrain;
    }

}
