/*
 * Copyright (C) 2015
 */

package de.projectsc.editor.componentViews;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ParticleEmitterComponentView extends JDialog {

    public ParticleEmitterComponentView() {
        setTitle("Particle Component View");
        getContentPane().setLayout(null);

        JButton addEmitterButton = new JButton("Add Emitter");
        addEmitterButton.setBounds(31, 11, 89, 23);
        getContentPane().add(addEmitterButton);

        JButton removeEmitterButton = new JButton("Remove");
        removeEmitterButton.setBounds(31, 45, 89, 23);
        getContentPane().add(removeEmitterButton);

        JList emitterList = new JList();
        emitterList.setBounds(20, 79, 118, 286);
        getContentPane().add(emitterList);

        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        separator.setBounds(148, 11, 2, 354);
        getContentPane().add(separator);

        JLabel positionLabel = new JLabel("Position");
        positionLabel.setBounds(171, 15, 71, 14);
        getContentPane().add(positionLabel);

        positionXText = new JTextField();
        positionXText.setBounds(271, 12, 38, 20);
        getContentPane().add(positionXText);
        positionXText.setColumns(10);

        positionYText = new JTextField();
        positionYText.setColumns(10);
        positionYText.setBounds(319, 12, 38, 20);
        getContentPane().add(positionYText);

        positionZText = new JTextField();
        positionZText.setColumns(10);
        positionZText.setBounds(367, 12, 38, 20);
        getContentPane().add(positionZText);

        JLabel particlesLabel = new JLabel("Particles");
        particlesLabel.setBounds(171, 49, 46, 14);
        getContentPane().add(particlesLabel);

        JSlider particleCountSlider = new JSlider();
        particleCountSlider.setBounds(271, 45, 134, 23);
        getContentPane().add(particleCountSlider);

        JLabel particleCOuntLabel = new JLabel("0");
        particleCOuntLabel.setBounds(415, 49, 46, 14);
        getContentPane().add(particleCOuntLabel);

        JLabel textureLabel = new JLabel("Texture");
        textureLabel.setBounds(471, 15, 56, 14);
        getContentPane().add(textureLabel);

        JLabel texturePreviewLabel = new JLabel("New label");
        texturePreviewLabel.setBounds(608, 11, 128, 128);
        getContentPane().add(texturePreviewLabel);

        JButton loadTextureButton = new JButton("Load texture");
        loadTextureButton.setBounds(471, 45, 118, 23);
        getContentPane().add(loadTextureButton);

        JLabel numberOfRowsLabel = new JLabel("Number of Rows");
        numberOfRowsLabel.setBounds(471, 94, 118, 14);
        getContentPane().add(numberOfRowsLabel);

        numberOfRowsText = new JTextField();
        numberOfRowsText.setBounds(471, 119, 56, 20);
        getContentPane().add(numberOfRowsText);
        numberOfRowsText.setColumns(10);

        JCheckBox glowingCheckbox = new JCheckBox("Glowing");
        glowingCheckbox.setBounds(171, 79, 97, 23);
        getContentPane().add(glowingCheckbox);

        JCheckBox useCOlorsCheckbox = new JCheckBox("Use Colors");
        useCOlorsCheckbox.setBounds(281, 79, 97, 23);
        getContentPane().add(useCOlorsCheckbox);

        JComboBox particleConfigurationCombo = new JComboBox();
        particleConfigurationCombo.setBounds(171, 119, 266, 20);
        getContentPane().add(particleConfigurationCombo);

        JLabel lifetimeLabel = new JLabel("Lifetime");
        lifetimeLabel.setBounds(171, 166, 46, 14);
        getContentPane().add(lifetimeLabel);

        JSlider lifetimeSlider = new JSlider();
        lifetimeSlider.setBounds(219, 162, 200, 23);
        getContentPane().add(lifetimeSlider);

        JLabel lifetimeCountLabel = new JLabel("0");
        lifetimeCountLabel.setBounds(429, 166, 38, 14);
        getContentPane().add(lifetimeCountLabel);

        JLabel lifetimeMarginLabel = new JLabel("Lifetime Margin");
        lifetimeMarginLabel.setBounds(458, 166, 86, 14);
        getContentPane().add(lifetimeMarginLabel);

        lifetimeMarginText = new JTextField();
        lifetimeMarginText.setBounds(554, 163, 46, 20);
        getContentPane().add(lifetimeMarginText);
        lifetimeMarginText.setColumns(10);

        JLabel radiusXLabel = new JLabel("Radius X");
        radiusXLabel.setBounds(171, 203, 46, 14);
        getContentPane().add(radiusXLabel);

        JSlider radiusXSlider = new JSlider();
        radiusXSlider.setBounds(219, 199, 200, 23);
        getContentPane().add(radiusXSlider);

        JLabel radiusXValueLabel = new JLabel("0");
        radiusXValueLabel.setBounds(429, 203, 38, 14);
        getContentPane().add(radiusXValueLabel);

        JSlider radiusYSlider = new JSlider();
        radiusYSlider.setBounds(219, 229, 200, 23);
        getContentPane().add(radiusYSlider);

        radiusYLabel = new JLabel("Radius Y");
        radiusYLabel.setBounds(171, 233, 46, 14);
        getContentPane().add(radiusYLabel);

        JLabel radiusYValueLabel = new JLabel("0");
        radiusYValueLabel.setBounds(429, 233, 38, 14);
        getContentPane().add(radiusYValueLabel);

        JSlider radiusZSlider = new JSlider();
        radiusZSlider.setBounds(219, 263, 200, 23);
        getContentPane().add(radiusZSlider);

        JLabel radiusZLabel = new JLabel("Radius Z");
        radiusZLabel.setBounds(171, 267, 46, 14);
        getContentPane().add(radiusZLabel);

        JLabel radiusZValueLabel = new JLabel("0");
        radiusZValueLabel.setBounds(429, 267, 38, 14);
        getContentPane().add(radiusZValueLabel);

        JLabel directionLabel = new JLabel("Direction");
        directionLabel.setBounds(171, 292, 46, 14);
        getContentPane().add(directionLabel);

        directionXText = new JTextField();
        directionXText.setColumns(10);
        directionXText.setBounds(219, 286, 38, 20);
        getContentPane().add(directionXText);

        directionYText = new JTextField();
        directionYText.setColumns(10);
        directionYText.setBounds(267, 286, 38, 20);
        getContentPane().add(directionYText);

        directionZText = new JTextField();
        directionZText.setColumns(10);
        directionZText.setBounds(315, 286, 38, 20);
        getContentPane().add(directionZText);

        JLabel directionXLabel = new JLabel("Dir X");
        directionXLabel.setBounds(171, 321, 46, 14);
        getContentPane().add(directionXLabel);

        JSlider directionXSlider = new JSlider();
        directionXSlider.setBounds(219, 317, 200, 23);
        getContentPane().add(directionXSlider);

        JLabel directionXValueLabel = new JLabel("0");
        directionXValueLabel.setBounds(429, 321, 38, 14);
        getContentPane().add(directionXValueLabel);

        JLabel directionYLabel = new JLabel("Dir Y");
        directionYLabel.setBounds(171, 346, 46, 14);
        getContentPane().add(directionYLabel);

        JSlider directionYSlider = new JSlider();
        directionYSlider.setBounds(219, 342, 200, 23);
        getContentPane().add(directionYSlider);

        JLabel directionYValueLabel = new JLabel("0");
        directionYValueLabel.setBounds(429, 346, 38, 14);
        getContentPane().add(directionYValueLabel);

        JLabel directionZLabel = new JLabel("Dir Z");
        directionZLabel.setBounds(171, 379, 46, 14);
        getContentPane().add(directionZLabel);

        JSlider directionZSlider = new JSlider();
        directionZSlider.setBounds(219, 375, 200, 23);
        getContentPane().add(directionZSlider);

        JLabel directionZValueLabel = new JLabel("0");
        directionZValueLabel.setBounds(429, 379, 38, 14);
        getContentPane().add(directionZValueLabel);

        JLabel weightLabel = new JLabel("Weight");
        weightLabel.setBounds(458, 198, 46, 14);
        getContentPane().add(weightLabel);

        JSlider weightSlider = new JSlider();
        weightSlider.setBounds(506, 194, 200, 23);
        getContentPane().add(weightSlider);

        JLabel weightValueLabel = new JLabel("0");
        weightValueLabel.setBounds(716, 198, 38, 14);
        getContentPane().add(weightValueLabel);

        JLabel speedLabel = new JLabel("Speed");
        speedLabel.setBounds(458, 233, 46, 14);
        getContentPane().add(speedLabel);

        JSlider speedSlider = new JSlider();
        speedSlider.setBounds(506, 229, 200, 23);
        getContentPane().add(speedSlider);

        JLabel speedValueLabel = new JLabel("0");
        speedValueLabel.setBounds(716, 233, 38, 14);
        getContentPane().add(speedValueLabel);

        JLabel spreadLabel = new JLabel("Spread");
        spreadLabel.setBounds(458, 267, 46, 14);
        getContentPane().add(spreadLabel);

        JSlider spreadSlider = new JSlider();
        spreadSlider.setBounds(506, 263, 200, 23);
        getContentPane().add(spreadSlider);

        JLabel spreadValueLabel = new JLabel("0");
        spreadValueLabel.setBounds(716, 267, 38, 14);
        getContentPane().add(spreadValueLabel);

        JLabel sizeLabel = new JLabel("Size");
        sizeLabel.setBounds(458, 296, 46, 14);
        getContentPane().add(sizeLabel);

        JSlider sizeSlider = new JSlider();
        sizeSlider.setBounds(506, 292, 200, 23);
        getContentPane().add(sizeSlider);

        JLabel sizeValueLabel = new JLabel("0");
        sizeValueLabel.setBounds(716, 296, 38, 14);
        getContentPane().add(sizeValueLabel);

        JLabel sizeMarginLabel = new JLabel("Size Margin");
        sizeMarginLabel.setBounds(458, 324, 86, 14);
        getContentPane().add(sizeMarginLabel);

        sizeMarginText = new JTextField();
        sizeMarginText.setColumns(10);
        sizeMarginText.setBounds(554, 321, 46, 20);
        getContentPane().add(sizeMarginText);

        JSlider angleSlider = new JSlider();
        angleSlider.setBounds(506, 346, 200, 23);
        getContentPane().add(angleSlider);

        JLabel angleLabel = new JLabel("Angle");
        angleLabel.setBounds(458, 350, 46, 14);
        getContentPane().add(angleLabel);

        JLabel angleValueLabel = new JLabel("0");
        angleValueLabel.setBounds(716, 350, 38, 14);
        getContentPane().add(angleValueLabel);

        JLabel colorLabel = new JLabel("Color");
        colorLabel.setBounds(171, 414, 46, 14);
        getContentPane().add(colorLabel);

        colorRText = new JTextField();
        colorRText.setColumns(10);
        colorRText.setBounds(219, 411, 38, 20);
        getContentPane().add(colorRText);

        colorGText = new JTextField();
        colorGText.setColumns(10);
        colorGText.setBounds(267, 411, 38, 20);
        getContentPane().add(colorGText);

        colorBText = new JTextField();
        colorBText.setColumns(10);
        colorBText.setBounds(315, 411, 38, 20);
        getContentPane().add(colorBText);

        colorAlphaText = new JTextField();
        colorAlphaText.setColumns(10);
        colorAlphaText.setBounds(367, 411, 38, 20);
        getContentPane().add(colorAlphaText);

        JSlider spawnrateSlider = new JSlider();
        spawnrateSlider.setBounds(506, 371, 200, 23);
        getContentPane().add(spawnrateSlider);

        JLabel spawnrateLabel = new JLabel("Spawnrate");
        spawnrateLabel.setBounds(458, 375, 46, 14);
        getContentPane().add(spawnrateLabel);

        JLabel spawnrateValueLabel = new JLabel("0");
        spawnrateValueLabel.setBounds(716, 375, 38, 14);
        getContentPane().add(spawnrateValueLabel);

        JCheckBox customColorsCheckbox = new JCheckBox("Use random colors");
        customColorsCheckbox.setBounds(458, 410, 307, 23);
        getContentPane().add(customColorsCheckbox);
    }

    /**
     * .
     */
    private static final long serialVersionUID = 8035229736643117775L;

    private final JTextField positionXText;

    private final JTextField positionYText;

    private final JTextField positionZText;

    private final JTextField numberOfRowsText;

    private final JTextField lifetimeMarginText;

    private final JTextField directionXText;

    private final JTextField directionYText;

    private final JTextField directionZText;

    private final JTextField sizeMarginText;

    private final JTextField colorRText;

    private final JTextField colorGText;

    private final JTextField colorBText;

    private final JTextField colorAlphaText;

    private final JLabel radiusYLabel;

    private static class __Tmp {

        private static void __tmp() {
            javax.swing.JPanel __wbp_panel = new javax.swing.JPanel();
        }
    }
}
