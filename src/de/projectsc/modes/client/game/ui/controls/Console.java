/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.game.ui.controls;

import java.util.ArrayList;
import java.util.List;

import de.projectsc.core.data.Event;
import de.projectsc.core.interfaces.EventListener;
import de.projectsc.modes.client.core.data.NewCommandLineEvent;
import de.projectsc.modes.client.core.manager.ClientEventManager;
import de.projectsc.modes.client.core.ui.UIElement;

/**
 * Chat implementation for ui.
 * 
 * @author Josch Bosch
 */
public class Console extends UIElement implements EventListener {

    private final List<String> lines = new ArrayList<>();

    public Console() {
        super("Console", 1);
        setActive(false);
        ClientEventManager.getInstance().registerForEvent(NewCommandLineEvent.class, this);
    }

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

    @Override
    public void processEvent(Event e) {
        lines.add(((NewCommandLineEvent) e).getCommand());
    }

    @Override
    public Class<?> getSource() {
        return this.getClass();
    }

}
