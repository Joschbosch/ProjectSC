/*
 * Copyright (C) 2016 
 */

package de.projectsc.editor.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.editor.map.componentConfigurations.ComponentConfigurationTypes;
/**
 * Listener if a new component was chosen and should show a configuration.
 * @author Josch Bosch
 */
public class ComponentConfigurationChosenListener implements ActionListener {

    private static final Log LOGGER = LogFactory.getLog(ComponentConfigurationChosenListener.class);
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
        @SuppressWarnings("rawtypes")
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
                ComponentConfigurationPanel config = chosenType.getComponentClass().newInstance();
                config.setEntityManager(entityManager);
                config.setEntity(entity);
                config.setComponent(components.get(chosen));
                config.init();
                configurationPanel.add(config);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error(e);
            }
        }
        configurationPanel.getParent().repaint();
        configurationPanel.getParent().revalidate();
    }
}
