/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.data.physics.BoundingVolumeType;
import de.projectsc.core.data.physics.boundings.Sphere;

/**
 * Mathematics helper methods.
 * 
 * @author Josch Bosch
 */
public final class Maths {

    private Maths() {

    }

    /**
     * 
     * Calculates the barry center of a vertex.
     * 
     * @param p1 vertex 1
     * @param p2 vertex 2
     * @param p3 vertex 3
     * @param pos on the vertex
     * @return height of the position in the vertex
     */
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    /**
     * creates a transformation matrix for the UI.
     * 
     * @param translation of ui element
     * @param scale of element
     * @return transformation matrix.
     */
    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
        return matrix;
    }

    /**
     * Create the transformation matrix for a model.
     * 
     * @param translation in (x,y,z) direction
     * @param rx rotation in x direction
     * @param ry rotation in y direction
     * @param rz rotation in z direction
     * @param scale factor for the model
     * @return matrix with all transformation
     */
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x, scale.y, scale.z), matrix, matrix);
        return matrix;
    }

    /**
     * Get center of bounding volume.
     * 
     * @param region to get center from
     * @return center vector
     */
    public static Vector3f getCenter(BoundingVolume region) {
        Vector3f center = Vector3f.add(region.getMinima(), region.getMaxima(), null);
        center.scale(1.0f / 2.0f);
        return center;
    }

    /**
     * Get size of volume.
     * 
     * @param boundingVolume to get size from
     * @return size
     */
    public static Vector3f getSize(BoundingVolume boundingVolume) {
        return Vector3f.sub(boundingVolume.getMaxima(), boundingVolume.getMinima(), null);
    }

    /**
     * Check if the given bounding volume intersects with the given ray.
     * 
     * @param position of bounding volume to check
     * @param boundingVolume to check
     * @param currentRay to check
     * @param currentPointOfOrigin to check
     * @return value of intersection.
     */
    public static float intersects(Vector3f position, BoundingVolume boundingVolume, Vector3f currentRay, Vector3f currentPointOfOrigin) {
        if (boundingVolume.getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
            Vector3f lb = Vector3f.add(position, boundingVolume.getMinima(), null);
            Vector3f rt = Vector3f.add(position, boundingVolume.getMaxima(), null);
            return calculateBoxIntersection(currentRay, currentPointOfOrigin, lb, rt);
        } else if (boundingVolume.getType() == BoundingVolumeType.SPHERE) {
            Vector3f centerPosition = Vector3f.add(boundingVolume.getPositionOffset(), position, null);
            float radius = ((Sphere) boundingVolume).getRadius();
            return calculateSphereIntersection(currentRay, currentPointOfOrigin, centerPosition, radius);
        } else {
            return Float.NaN;
        }
    }

    /**
     * 
     * @param position of box
     * @param halfSize of box
     * @param currentRay to check
     * @param currentPointOfOrigin to check
     * @return intersection position
     */
    public static float intersects(Vector3f position, float halfSize, Vector3f currentRay, Vector3f currentPointOfOrigin) {
        Vector3f min = Vector3f.sub(position, new Vector3f(halfSize, halfSize, halfSize), null);
        Vector3f max = Vector3f.add(position, new Vector3f(halfSize, halfSize, halfSize), null);
        return calculateBoxIntersection(currentRay, currentPointOfOrigin, min, max);
    }

    private static float calculateSphereIntersection(Vector3f rayDirection, Vector3f rayOrigin, Vector3f centerPosition,
        float radius) {
        Vector3f length = Vector3f.sub(rayOrigin, centerPosition, null);
        float a = Vector3f.dot(rayDirection, rayDirection);
        float b = 2 * Vector3f.dot(rayDirection, length);
        float c = (float) (Vector3f.dot(length, length) - Math.pow(radius, 2));
        float[] solution = solveQuadratic(a, b, c);
        if (solution != null) {
            float t0 = solution[0];
            float t1 = solution[1];

            if (t0 > t1) {
                float temp = t0;
                t0 = t1;
                t1 = temp;
            }

            if (t0 < 0) {
                t0 = t1; // if t0 is negative, let's use t1 instead
            }
            if (t0 < 0) {
                return Float.NaN; // both t0 and t1 are negative
            }

            return t0;
        } else {
            return Float.NaN;
        }
    }

    private static float[] solveQuadratic(float a, float b, float c) {
        float discr = b * b - 4 * a * c;
        float[] result = new float[2];
        if (discr < 0) {
            return null;
        } else if (discr == 0) {
            result[0] = -0.5f * b / a;
            result[1] = -0.5f * b / a;
        } else {
            float q = (float) ((b > 0) ? -0.5 * (b + Math.sqrt(discr)) : -0.5 * (b - Math.sqrt(discr)));
            result[0] = q / a;
            result[1] = c / q;
        }
        if (result[0] > result[1]) {
            float temp = result[0];
            result[0] = result[1];
            result[1] = temp;
        }
        return result;
    }

    private static float calculateBoxIntersection(Vector3f ray, Vector3f org, Vector3f lb, Vector3f rt) {
        // r.dir is unit direction vector of ray
        float dirfracx = 1.0f / ray.x;
        float dirfracy = 1.0f / ray.y;
        float dirfracz = 1.0f / ray.z;
        // lb is the corner of AABB with minimal coordinates - left bottom, rt is maximal corner
        // r.org is origin of ray
        float t1 = (lb.x - org.x) * dirfracx;
        float t2 = (rt.x - org.x) * dirfracx;
        float t3 = (lb.y - org.y) * dirfracy;
        float t4 = (rt.y - org.y) * dirfracy;
        float t5 = (lb.z - org.z) * dirfracz;
        float t6 = (rt.z - org.z) * dirfracz;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        float t;
        // if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behing us
        if (tmax < 0) {
            t = tmax;
            return -1;
        }

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax) {
            t = tmax;
            return -1;
        }

        t = tmin;
        return t;
    }

}
