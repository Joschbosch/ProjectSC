/*
 * Copyright (C) 2016 
 */

package de.projectsc.editor.map.componentConfigurations;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.projectsc.editor.map.ComponentConfiguration;
import de.projectsc.modes.server.game.ai.PathPointComponent;

public class PathPointComponentConfiguration extends ComponentConfiguration {

    private PathPointComponent ppc;

    public PathPointComponentConfiguration() {}

    @Override
    public void init() {
        this.setLayout(new GridLayout(0, 2));
        ppc = (PathPointComponent) component;
        JLabel groupLabel = new JLabel("Group Order Position: ");
        add(groupLabel);
        JSpinner groupSpinner = new JSpinner(new SpinnerNumberModel(ppc.getGroupOrderNumber(), -1, 100, 1));
        add(groupSpinner);
        groupSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) groupSpinner.getValue();
                ppc.setGroupOrderNumber(value);
            }
        });

        JLabel pathIDLabel = new JLabel("Path ID: ");
        add(pathIDLabel);
        JSpinner idSpinner = new JSpinner(new SpinnerNumberModel(ppc.getPathID(), -1, 100, 1));
        add(idSpinner);
        idSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) idSpinner.getValue();
                ppc.setPathID(value);
            }
        });
    }
}
