package de.projectsc.core.data.utils.md5loader;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class AnimVertex {

    public Vector3f position;

    public Vector2f textCoords;

    public Vector3f normal;

    public float[] weights;

    public int[] jointIndices;

    public AnimVertex() {
        super();
        normal = new Vector3f(0, 0, 0);
    }
}
