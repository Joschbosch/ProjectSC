/*
 * Copyright (C) 2016 
 */

package de.projectsc.editor.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.editor.map.componentConfigurations.ComponentConfigurationTypes;

public class ComponentConfigurationChosenListener implements ActionListener {

    private String entity;

    private Map<String, Component> components;

    private EntityManager entityManager;

    private JPanel configurationPanel;

    public ComponentConfigurationChosenListener(String e, Map<String, Component> components, JPanel configurationPanel,
        EntityManager entityManager) {
        this.entity = e;
        this.components = components;
        this.configurationPanel = configurationPanel;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String chosen = (String) ((JComboBox) arg0.getSource()).getSelectedItem();
        configurationPanel.removeAll();
        ComponentConfigurationTypes chosenType = null;
        for (ComponentConfigurationTypes t : ComponentConfigurationTypes.values()) {
            if (t.getComponentName().equals(chosen)) {
                chosenType = t;
            }
        }
        if (chosenType != null) {
            try {
                ComponentConfiguration config = chosenType.getComponentClass().newInstance();
                config.setEntityManager(entityManager);
                config.setEntity(entity);
                config.setComponent(components.get(chosen));
                config.init();
                configurationPanel.add(config);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        configurationPanel.getParent().repaint();
        configurationPanel.getParent().revalidate();
    }
}
