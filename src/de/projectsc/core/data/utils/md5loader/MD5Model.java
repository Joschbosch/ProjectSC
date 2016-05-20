package de.projectsc.core.data.utils.md5loader;

import java.util.ArrayList;
import java.util.List;

import de.projectsc.core.data.utils.md5loader.mesh.MD5Joints;
import de.projectsc.core.data.utils.md5loader.mesh.MD5Mesh;
import de.projectsc.core.data.utils.md5loader.mesh.MD5ModelHeader;

public class MD5Model {

    private MD5Joints jointInfo;

    private MD5ModelHeader header;

    private List<MD5Mesh> meshes;

    public MD5Model() {
        meshes = new ArrayList<>();
    }

    public MD5Joints getJointInfo() {
        return jointInfo;
    }

    public void setJoints(MD5Joints jointInfo) {
        this.jointInfo = jointInfo;
    }

    public MD5ModelHeader getHeader() {
        return header;
    }

    public void setHeader(MD5ModelHeader header) {
        this.header = header;
    }

    public List<MD5Mesh> getMeshes() {
        return meshes;
    }

    public void setMeshes(List<MD5Mesh> meshes) {
        this.meshes = meshes;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("MD5MeshModel: " + System.lineSeparator());
        str.append(getHeader()).append(System.lineSeparator());
        str.append(getJointInfo()).append(System.lineSeparator());

        for (MD5Mesh mesh : meshes) {
            str.append(mesh).append(System.lineSeparator());
        }
        return str.toString();
    }

}
