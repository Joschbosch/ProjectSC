package de.projectsc.core.data.utils.md5loader.anim;

import java.util.ArrayList;
import java.util.List;

public class MD5AnimModel {

    private MD5AnimHeader header;

    private MD5Hierarchy hierarchy;

    private MD5BoundInfo boundInfo;

    private MD5BaseFrame baseFrame;

    private List<MD5Frame> frames;

    public MD5AnimModel() {
        frames = new ArrayList<>();
    }

    public MD5AnimHeader getHeader() {
        return header;
    }

    public void setHeader(MD5AnimHeader header) {
        this.header = header;
    }

    public MD5Hierarchy getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(MD5Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    public MD5BoundInfo getBoundInfo() {
        return boundInfo;
    }

    public void setBoundInfo(MD5BoundInfo boundInfo) {
        this.boundInfo = boundInfo;
    }

    public MD5BaseFrame getBaseFrame() {
        return baseFrame;
    }

    public void setBaseFrame(MD5BaseFrame baseFrame) {
        this.baseFrame = baseFrame;
    }

    public List<MD5Frame> getFrames() {
        return frames;
    }

    public void setFrames(List<MD5Frame> frames) {
        this.frames = frames;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("MD5AnimModel: " + System.lineSeparator());
        str.append(getHeader()).append(System.lineSeparator());
        str.append(getHierarchy()).append(System.lineSeparator());
        str.append(getBoundInfo()).append(System.lineSeparator());
        str.append(getBaseFrame()).append(System.lineSeparator());

        for (MD5Frame frame : frames) {
            str.append(frame).append(System.lineSeparator());
        }
        return str.toString();
    }
}
