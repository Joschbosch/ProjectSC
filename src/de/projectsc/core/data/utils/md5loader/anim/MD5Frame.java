package de.projectsc.core.data.utils.md5loader.anim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MD5Frame {

    private int id;

    private Float[] frameData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Float[] getFrameData() {
        return frameData;
    }

    public void setFrameData(Float[] frameData) {
        this.frameData = frameData;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("frame " + id + " [data: " + System.lineSeparator());
        for (float frameData : frameData) {
            str.append(frameData).append(System.lineSeparator());
        }
        str.append("]").append(System.lineSeparator());
        return str.toString();
    }

    public static MD5Frame parse(String blockId, List<String> blockBody) throws IOException {
        MD5Frame result = new MD5Frame();
        result.setId(Integer.parseInt(blockId));

        List<Float> data = new ArrayList<>();
        for (String line : blockBody) {
            List<Float> lineData = parseLine(line);
            if (lineData != null) {
                data.addAll(lineData);
            }
        }
        Float[] dataArr = new Float[data.size()];
        dataArr = data.toArray(dataArr);
        result.setFrameData(dataArr);

        return result;
    }

    private static List<Float> parseLine(String line) {
        String[] tokens = line.trim().split("\\s+");
        List<Float> data = new ArrayList<>();
        for (String token : tokens) {
            data.add(Float.parseFloat(token));
        }
        return data;
    }
}
