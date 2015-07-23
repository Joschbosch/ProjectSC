/*
 * Copyright (C) 2015
 */

package de.projectsc.client.core.elements;

import java.util.ArrayList;
import java.util.List;

public class Chat implements UIElement {

    List<String> lines = new ArrayList<>();

    public void addLine(String line) {
        lines.add(line);
    }

    public List<String> getLines() {
        return lines;
    }
}
