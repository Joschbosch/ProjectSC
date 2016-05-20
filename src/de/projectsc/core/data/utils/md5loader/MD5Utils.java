package de.projectsc.core.data.utils.md5loader;

import org.lwjgl.util.vector.Vector3f;

public class MD5Utils {

    public static final String FLOAT_REGEXP = "[+-]?\\d*\\.?\\d*";

    public static final String VECTOR3_REGEXP = "\\(\\s*(" + FLOAT_REGEXP + ")\\s*(" + FLOAT_REGEXP + ")\\s*(" + FLOAT_REGEXP + ")\\s*\\)";

    private MD5Utils() {}

    public static float calculateWValue(Vector3f vec) {
        float w = 0;
        float temp = 1.0f - (vec.x * vec.x) - (vec.y * vec.y) - (vec.z * vec.z);
        if (temp >= 0.0f) {
            w = -(float) (Math.sqrt(temp));
        }
        return w;
    }
}
