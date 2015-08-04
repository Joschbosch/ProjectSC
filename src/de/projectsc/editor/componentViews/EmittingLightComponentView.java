/*
 * Copyright (C) 2015 
 */

package de.projectsc.editor.componentViews;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.Light;
import de.projectsc.core.components.impl.EmittingLightComponent;
import de.projectsc.core.entities.Entity;

public class EmittingLightComponentView extends JDialog {

    private JTable lightsTable;

    private JTextField nameText;

    private JTextField positionXText;

    private JTextField positionYText;

    private JTextField positionZText;

    private JTextField colorRText;

    private JTextField colorGText;

    private JTextField colorBText;

    private JTextField attenuation1Text;

    private JTextField attenuation2Text;

    private JTextField attenuation3Text;

    private EmittingLightComponent component;

    private Entity entity;

    public EmittingLightComponentView(EmittingLightComponent component, Entity entity) {
        this.component = component;
        this.entity = entity;
        setTitle("Emitting Light Component");
        getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(10, 11, 668, 450);
        getContentPane().add(panel);
        panel.setLayout(null);

        lightsTable = new JTable();
        lightsTable.setModel(new DefaultTableModel(
            new Object[][] {
            },
            new String[] {
                "Name", "Position", "Color", "Attenation"
            }
            ));
        lightsTable.setBorder(new TitledBorder(null, "Lights", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        lightsTable.setBounds(0, 103, 668, 347);
        panel.add(lightsTable);

        JLabel lblName = new JLabel("Name");
        lblName.setBounds(10, 25, 46, 14);
        panel.add(lblName);

        nameText = new JTextField();
        nameText.setText("light");
        nameText.setBounds(95, 22, 137, 20);
        panel.add(nameText);
        nameText.setColumns(10);

        JLabel lblPosition = new JLabel("Position offset");
        lblPosition.setBounds(10, 50, 81, 14);
        panel.add(lblPosition);

        JLabel lblNewLabel = new JLabel("Color");
        lblNewLabel.setBounds(10, 75, 46, 14);
        panel.add(lblNewLabel);

        positionXText = new JTextField();
        positionXText.setText("0");
        positionXText.setBounds(95, 47, 41, 20);
        panel.add(positionXText);
        positionXText.setColumns(10);

        positionYText = new JTextField();
        positionYText.setText("0");
        positionYText.setColumns(10);
        positionYText.setBounds(143, 47, 41, 20);
        panel.add(positionYText);

        positionZText = new JTextField();
        positionZText.setText("0");
        positionZText.setColumns(10);
        positionZText.setBounds(191, 47, 41, 20);
        panel.add(positionZText);

        colorRText = new JTextField();
        colorRText.setText("255");
        colorRText.setColumns(10);
        colorRText.setBounds(95, 72, 41, 20);
        panel.add(colorRText);

        colorGText = new JTextField();
        colorGText.setText("255");
        colorGText.setColumns(10);
        colorGText.setBounds(143, 72, 41, 20);
        panel.add(colorGText);

        colorBText = new JTextField();
        colorBText.setText("255");
        colorBText.setColumns(10);
        colorBText.setBounds(191, 72, 41, 20);
        panel.add(colorBText);

        JLabel lblAttenuation = new JLabel("Attenuation");
        lblAttenuation.setBounds(291, 25, 81, 14);
        panel.add(lblAttenuation);

        attenuation1Text = new JTextField();
        attenuation1Text.setText("1");
        attenuation1Text.setColumns(10);
        attenuation1Text.setBounds(364, 22, 41, 20);
        panel.add(attenuation1Text);

        attenuation2Text = new JTextField();
        attenuation2Text.setText("0");
        attenuation2Text.setColumns(10);
        attenuation2Text.setBounds(415, 22, 41, 20);
        panel.add(attenuation2Text);

        attenuation3Text = new JTextField();
        attenuation3Text.setText("0");
        attenuation3Text.setColumns(10);
        attenuation3Text.setBounds(466, 22, 41, 20);
        panel.add(attenuation3Text);

        JButton addButton = new JButton("Add");
        addButton.setBounds(291, 69, 89, 23);
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Light light = readAndCreateLight();
                    if (light != null) {
                        component.addLight(entity, light);
                    }
                } catch (NumberFormatException e1) {

                }
                fillTable();
            }

        });
        panel.add(addButton);

        JButton editButton = new JButton("Edit");
        editButton.setBounds(390, 69, 89, 23);
        editButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (lightsTable.getSelectedRow() > -1) {
                    removeSelectedLight(component);
                    Light l = readAndCreateLight();
                    if (l != null) {
                        component.addLight(entity, l);
                    }
                }
            }
        });
        panel.add(editButton);

        JButton removeButton = new JButton("Remove");
        removeButton.setBounds(489, 69, 89, 23);
        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedLight(component);
            }

        });
        panel.add(removeButton);

        fillTable();
    }

    private void removeSelectedLight(EmittingLightComponent component) {
        if (lightsTable.getSelectedRow() > -1) {
            String lightsName = (String) lightsTable.getModel().getValueAt(lightsTable.getSelectedRow(), 0);
            Light remove = null;
            for (Light l : component.getLights()) {
                if (l.getName().equals(lightsName)) {
                    remove = l;
                }
            }
            component.getLights().remove(remove);
            fillTable();
        }
    }

    private Light readAndCreateLight() {
        String name = nameText.getText();
        for (Light l : component.getLights()) {
            if (l.getName().equals(name)) {
                return null;
            }
        }
        Vector3f position =
            new Vector3f(Float.parseFloat(positionXText.getText()), Float.parseFloat(positionYText.getText()), Float
                .parseFloat(positionZText.getText()));
        Vector3f color =
            new Vector3f(Float.parseFloat(colorRText.getText()) / 255.0f,
                Float.parseFloat(colorGText.getText()) / 255.0f, Float
                    .parseFloat(colorBText.getText()) / 255.0f);
        Vector3f attenuation =
            new Vector3f(Float.parseFloat(attenuation1Text.getText()), Float.parseFloat(attenuation2Text.getText()), Float
                .parseFloat(attenuation3Text.getText()));
        Light light = new Light(position, color, attenuation, name);
        return light;
    }

    private void fillTable() {

        if (component != null) {
            List<Light> lights = component.getLights();
            Collections.sort(lights);
            TableModel dataModel = new AbstractTableModel() {

                @Override
                public int getColumnCount() {
                    return 4;
                }

                @Override
                public int getRowCount() {
                    return lights.size();
                }

                @Override
                public Object getValueAt(int row, int col) {
                    Light l = lights.get(row);
                    switch (col) {
                    case 0:
                        return l.getName();
                    case 1:
                        return l.getPosition();
                    case 2:
                        return l.getColor();
                    case 3:
                        return l.getAttenuation();
                    default:
                        return l.getName();
                    }
                }
            };
            lightsTable.setModel(dataModel);
        }
    }
}
