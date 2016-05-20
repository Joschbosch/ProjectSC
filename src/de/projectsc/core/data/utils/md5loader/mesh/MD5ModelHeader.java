package de.projectsc.core.data.utils.md5loader.mesh;

import java.io.IOException;
import java.util.List;

public class MD5ModelHeader {

    private String version;

    private String commandLine;

    private int numJoints;

    private int numMeshes;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public int getNumJoints() {
        return numJoints;
    }

    public void setNumJoints(int numJoints) {
        this.numJoints = numJoints;
    }

    public int getNumMeshes() {
        return numMeshes;
    }

    public void setNumMeshes(int numMeshes) {
        this.numMeshes = numMeshes;
    }

    @Override
    public String toString() {
        return "[version: " + version + ", commandLine: " + commandLine +
            ", numJoints: " + numJoints + ", numMeshes: " + numMeshes + "]";
    }

    public static MD5ModelHeader parse(List<String> lines) throws IOException {
        MD5ModelHeader header = new MD5ModelHeader();
        int numLines = lines != null ? lines.size() : 0;
        if (numLines == 0) {
            throw new IOException("Cannot parse empty header");
        }
        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            int numTokens = tokens != null ? tokens.length : 0;
            if (numTokens > 1) {
                String paramName = tokens[0];
                String paramValue = tokens[1];
                switch (paramName) {
                case "MD5Version":
                    header.setVersion(paramValue);
                    break;
                case "commandline":
                    header.setCommandLine(line.substring("commandline".length() + 1));
                    break;
                case "numJoints":
                    header.setNumJoints(Integer.parseInt(paramValue));
                    break;
                case "numMeshes":
                    header.setNumMeshes(Integer.parseInt(paramValue));
                    break;
                default:
                    break;
                }
            }
        }
        return header;
    }
}
