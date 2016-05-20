package de.projectsc.core.data.utils.md5loader.mesh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.utils.md5loader.MD5Utils;

public class MD5Joints {

    private List<MD5JointData> joints;

    public List<MD5JointData> getJoints() {
        return joints;
    }

    public void setJoints(List<MD5JointData> joints) {
        this.joints = joints;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("joints [" + System.lineSeparator());
        for (MD5JointData joint : joints) {
            str.append(joint).append(System.lineSeparator());
        }
        str.append("]").append(System.lineSeparator());
        return str.toString();
    }

    public static MD5Joints parse(List<String> lines) throws IOException {
        MD5Joints result = new MD5Joints();
        List<MD5JointData> joints = new ArrayList<>();
        int numLines = lines != null ? lines.size() : 0;
        if (numLines == 0) {
            throw new IOException("Cannot parse empty joints.");
        }
        for (String line : lines) {
            MD5JointData jointData = MD5JointData.parseLine(line);
            if (jointData != null) {
                joints.add(jointData);
            }
        }
        result.setJoints(joints);
        return result;
    }

    public static class MD5JointData {

        private static final String PARENT_INDEX_REGEXP = "([-]?\\d+)";

        private static final String NAME_REGEXP = "\\\"([^\\\"]+)\\\"";

        private static final String JOINT_REGEXP = "\\s*" + NAME_REGEXP + "\\s*" + PARENT_INDEX_REGEXP + "\\s*"
            + MD5Utils.VECTOR3_REGEXP + "\\s*" + MD5Utils.VECTOR3_REGEXP + ".*";

        private static final Pattern PATTERN_JOINT = Pattern.compile(JOINT_REGEXP);

        private String name;

        private int parentIndex;

        private Vector3f position;

        private Quaternion orientation;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getParentIndex() {
            return parentIndex;
        }

        public void setParentIndex(int parentIndex) {
            this.parentIndex = parentIndex;
        }

        public Vector3f getPosition() {
            return position;
        }

        public void setPosition(Vector3f position) {
            this.position = position;
        }

        public Quaternion getOrientation() {
            return orientation;
        }

        public void setOrientation(Vector3f vec) {
            float w = 0;
            float temp = 1.0f - (vec.x * vec.x) - (vec.y * vec.y) - (vec.z * vec.z);
            if (temp >= 0.0f) {
                w = -(float) (Math.sqrt(temp));
            }
            this.orientation = new Quaternion(vec.x, vec.y, vec.z, w);
        }

        @Override
        public String toString() {
            return "[name: " + name + ", parentIndex: " + parentIndex + ", position: " + position + ", orientation: " + orientation + "]";
        }

        public static MD5JointData parseLine(String line) {
            MD5JointData result = null;
            Matcher matcher = PATTERN_JOINT.matcher(line);
            if (matcher.matches()) {
                result = new MD5JointData();
                result.setName(matcher.group(1));
                result.setParentIndex(Integer.parseInt(matcher.group(2)));
                float x = Float.parseFloat(matcher.group(3));
                float y = Float.parseFloat(matcher.group(4));
                float z = Float.parseFloat(matcher.group(5));
                result.setPosition(new Vector3f(x, y, z));

                x = Float.parseFloat(matcher.group(6));
                y = Float.parseFloat(matcher.group(7));
                z = Float.parseFloat(matcher.group(8));
                result.setOrientation(new Vector3f(x, y, z));
            }
            return result;
        }
    }
}
