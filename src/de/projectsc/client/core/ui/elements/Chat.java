/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.core.ui.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat implementation for ui.
 * 
 * @author Josch Bosch
 */
public class Chat extends UIElement {

    private final List<String> lines = new ArrayList<>();

    /**
     * New line.
     * 
     * @param line to add
     */
    public void addLine(String line) {
        lines.add(line);
    }

    public List<String> getLines() {
        return lines;
    }
}
