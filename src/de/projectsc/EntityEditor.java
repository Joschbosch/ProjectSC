/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.impl.BoundingComponent;
import de.projectsc.core.components.impl.EmittingLightComponent;
import de.projectsc.core.components.impl.MovingComponent;
import de.projectsc.editor.Editor3DCore;
import de.projectsc.editor.EditorData;
import de.projectsc.editor.componentViews.BoundingComponentView;
import de.projectsc.editor.componentViews.EmittingLightComponentView;
import de.projectsc.editor.componentViews.MovingComponentView;

/**
 * Entity editor.
 * 
 * @author Josch Bosch
 */
public class EntityEditor extends JFrame {

    /**
     * Smallest id for models.
     */
    public static final int MINIMUM_ID = 10000;

    private static final String COULD_NOT_SET_CURRENT_DIRECTORY = "Could not set current directory.";

    private static final String ENTITY_ENT = CoreConstants.ENTITY_FILENAME;

    private static final String TEXTURE_PNG = CoreConstants.TEXTURE_FILENAME;

    private static final String MODEL_OBJ = CoreConstants.MODEL_FILENAME;

    private static final String COMPONENTS = "components";

    private static final String DOT = ".";

    private static final String FONT = "Monospaced";

    private static final Log LOGGER = LogFactory.getLog(EntityEditor.class);

    private static final long serialVersionUID = 3313139728699706144L;

    private static final String SLASHED_MODEL_DIR = "/" + CoreConstants.MODEL_DIRECTORY_NAME + "/";

    private JPanel contentPane;

    private JTextField idTextfield;

    private JTextField shineDamperTextfield;

    private JTextField reflectivityTextfield;

    private Canvas displayParent;

    private Thread gameThread;

    private Editor3DCore editor3dCore;

    private BlockingQueue<String> messageQueue;

    private JLabel modelNameLabel;

    private EditorData data;

    private JButton loadModelButton;

    private JSlider scaleSlider;

    private JLabel scaleValuelabel;

    private JButton loadTextureButton;

    private JLabel textureNameLabel;

    private JLabel iconPreviewLabel;

    private JCheckBox transparentCheckbox;

    private JCheckBox fakelightCheckbox;

    private JSpinner numColumSpinner;

    private JCheckBox rotateCheckbox;

    private JCheckBox cycleCheckbox;

    private JCheckBox moveEntityCheckBox;

    private JCheckBox lightPositionCheckBox;

    private JComboBox<String> componentCombo;

    private final String[] componentNames = { EmittingLightComponent.NAME, MovingComponent.NAME, BoundingComponent.NAME };

    private JList<String> componentList;

    /**
     * Create the frame.
     */
    public EntityEditor() {
        data = new EditorData();
        createContent();
        try {
            File folder = new File(EntityEditor.class.getResource(SLASHED_MODEL_DIR).toURI());
            for (int i = 0; i < 10000; i++) {
                File f = new File(folder, CoreConstants.MODEL_DIRECTORY_PREFIX + (MINIMUM_ID + i));
                if (!f.exists()) {
                    data.setId(MINIMUM_ID + i);
                    break;
                }
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Could not read model data.");
        }
        updateEditor(data);
    }

    private void updateEditor(EditorData d) {
        String modelName = "";
        if (d.getModelFile() != null) {
            modelName = FilenameUtils.removeExtension(d.getModelFile().getName());
        }
        modelNameLabel.setText(modelName);
        String textureName = "";
        if (d.getTextureFile() != null) {
            textureName = FilenameUtils.removeExtension(d.getTextureFile().getName());
        }
        textureNameLabel.setText(textureName);

        idTextfield.setText(String.valueOf(d.getId()));

        numColumSpinner.setValue(d.getNumColums());
        scaleSlider.setValue((int) (d.getScale() * 10));
        scaleValuelabel.setText("" + (d.getScale()));
        reflectivityTextfield.setText("" + d.getReflectivity());
        shineDamperTextfield.setText("" + d.getShineDamper());
        transparentCheckbox.setSelected(d.isTransparent());
        cycleCheckbox.setSelected(d.isCycleTextures());
        moveEntityCheckBox.setSelected(d.isMoveEntity());
        fakelightCheckbox.setSelected(d.isFakeLighting());
        rotateCheckbox.setSelected(d.isRotateCamera());
        lightPositionCheckBox.setSelected(d.isLightAtCameraPostion());

    }

    /**
     * Launch the application.
     * 
     * @param args program arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                EntityEditor frame = new EntityEditor();
                frame.setVisible(true);
            }
        });
    }

    private void createContent() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1024, 768);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                remove(displayParent);
                dispose();
            }
        });

        createMenue();
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        createMainSettings();

        createPreviewOptions();

        createComponentPanel();

        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BorderLayout(0, 0));
        previewPanel.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        previewPanel.setBounds(371, 214, 627, 484);
        contentPane.add(previewPanel);

        displayParent = new Canvas() {

            /**
             * UID.
             */
            private static final long serialVersionUID = -6934397843291223284L;

            @Override
            public void addNotify() {
                super.addNotify();
                startLWJGL();
            }

            @Override
            public void removeNotify() {
                stopLWJGL();
                super.removeNotify();
            }
        };
        displayParent.setFocusable(true);
        displayParent.requestFocus();
        displayParent.setIgnoreRepaint(true);
        previewPanel.add(displayParent);
    }

    private void createComponentPanel() {
        JPanel componentPanel = new JPanel();
        componentPanel.setBorder(new TitledBorder(null, "Components", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        componentPanel.setBounds(5, 305, 356, 393);
        contentPane.add(componentPanel);
        componentPanel.setLayout(null);

        componentList = new JList<String>();
        componentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        componentList.setValueIsAdjusting(true);
        componentList.setBorder(new LineBorder(new Color(0, 0, 0)));
        componentList.setBounds(10, 99, 336, 283);
        componentPanel.add(componentList);

        JButton addComponentButton = new JButton("Add");
        addComponentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (componentCombo.getSelectedItem() != null) {
                    String component = (String) componentCombo.getSelectedItem();
                    if (!component.isEmpty()) {
                        data.getComponentsAdded().add(component);
                    }
                    fillComponentComboAndList();
                    editor3dCore.addComponent(component);
                }
            }
        });
        addComponentButton.setBounds(10, 65, 95, 23);
        componentPanel.add(addComponentButton);

        JButton editComponentButton = new JButton("Edit");
        editComponentButton.setBounds(115, 65, 95, 23);
        editComponentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (componentList.getSelectedValue() != null && !componentList.getSelectedValue().isEmpty()) {
                    if (componentList.getSelectedValue().equals(EmittingLightComponent.NAME)) {
                        EmittingLightComponent component = (EmittingLightComponent) editor3dCore.getComponent(EmittingLightComponent.NAME);
                        EmittingLightComponentView dialog =
                            new EmittingLightComponentView(component, editor3dCore.getCurrentEntity());
                        dialog.setSize(800, 600);
                        dialog.setVisible(true);
                    } else if (componentList.getSelectedValue().equals(MovingComponent.NAME)) {
                        MovingComponent component = (MovingComponent) editor3dCore.getComponent(MovingComponent.NAME);
                        MovingComponentView dialog =
                            new MovingComponentView(component, editor3dCore.getCurrentEntity());
                        dialog.setSize(450, 130);
                        dialog.setVisible(true);
                    } else if (componentList.getSelectedValue().equals(BoundingComponent.NAME)) {
                        BoundingComponent component = (BoundingComponent) editor3dCore.getComponent(BoundingComponent.NAME);
                        BoundingComponentView dialog =
                            new BoundingComponentView(component, editor3dCore);
                        dialog.setSize(450, 130);
                        dialog.setVisible(true);
                    }
                }
            }
        });
        componentPanel.add(editComponentButton);

        JButton removeComponentButton = new JButton("Remove");
        removeComponentButton.setBounds(220, 65, 95, 23);
        removeComponentButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (componentList.getSelectedValue() != null) {
                    String component = componentList.getSelectedValue();
                    data.getComponentsAdded().remove(component);
                    editor3dCore.removeComponent(component);
                }
                fillComponentComboAndList();
            }

        });
        componentPanel.add(removeComponentButton);

        componentCombo = new JComboBox<String>();
        componentCombo.setBounds(10, 34, 305, 20);
        fillComponentComboAndList();
        componentPanel.add(componentCombo);
    }

    private void fillComponentComboAndList() {
        componentCombo.removeAllItems();
        componentList.removeAll();
        for (String componentName : componentNames) {
            if (data != null && !data.getComponentsAdded().contains(componentName)) {
                componentCombo.addItem(componentName);
            }
        }
        if (data != null && data.getComponentsAdded() != null) {
            componentList.setListData(data.getComponentsAdded().toArray(new String[data.getComponentsAdded().size()]));
        }
    }

    private void createPreviewOptions() {
        JPanel previewOptionsPanel = new JPanel();
        previewOptionsPanel.setBorder(new TitledBorder(null, "Preview Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        previewOptionsPanel.setBounds(371, 11, 627, 204);
        contentPane.add(previewOptionsPanel);
        previewOptionsPanel.setLayout(null);

        rotateCheckbox = new JCheckBox("Rotate Camera");
        rotateCheckbox.setBounds(6, 20, 142, 23);
        rotateCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                data.setRotateCamera(rotateCheckbox.isSelected());
                updateEditor(data);
            }
        });
        previewOptionsPanel.add(rotateCheckbox);

        cycleCheckbox = new JCheckBox("Cycle Textures");
        cycleCheckbox.setBounds(6, 46, 142, 23);
        cycleCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                data.setCycleTextures(cycleCheckbox.isSelected());
                if (editor3dCore != null) {
                    editor3dCore.updateData();
                }
            }
        });
        previewOptionsPanel.add(cycleCheckbox);

        lightPositionCheckBox = new JCheckBox("Light at camera position");
        lightPositionCheckBox.setBounds(6, 72, 142, 23);
        lightPositionCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                data.setLightAtCameraPostion(lightPositionCheckBox.isSelected());
                if (editor3dCore != null) {
                    editor3dCore.updateData();
                }
            }
        });
        previewOptionsPanel.add(lightPositionCheckBox);

        moveEntityCheckBox = new JCheckBox("Move entity");
        moveEntityCheckBox.setBounds(6, 98, 142, 23);
        moveEntityCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                data.setEntityMoving(moveEntityCheckBox.isSelected());
                if (editor3dCore != null) {
                    editor3dCore.moveEntity(moveEntityCheckBox.isSelected());
                }
            }
        });
        previewOptionsPanel.add(moveEntityCheckBox);
    }

    private void createMainSettings() {
        JPanel mainSettingsPanel = new JPanel();
        mainSettingsPanel.setBounds(5, 5, 356, 294);
        mainSettingsPanel.setBorder(new TitledBorder(null, "Main Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        contentPane.add(mainSettingsPanel);
        mainSettingsPanel.setLayout(null);

        JLabel iDLabel = new JLabel("ID");
        iDLabel.setFont(new Font(FONT, Font.BOLD, 16));
        iDLabel.setBounds(10, 19, 21, 14);
        mainSettingsPanel.add(iDLabel);

        idTextfield = new JTextField();
        idTextfield.setBounds(37, 18, 68, 20);
        idTextfield.setText("12312");
        mainSettingsPanel.add(idTextfield);
        idTextfield.setColumns(10);
        idTextfield.setDocument(new IDDocument());

        modelPanel(mainSettingsPanel);

        JPanel texturePanel = new JPanel();
        texturePanel.setBorder(new TitledBorder(null, "Texture", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        texturePanel.setBounds(10, 98, 336, 185);
        mainSettingsPanel.add(texturePanel);
        texturePanel.setLayout(null);

        loadTextureButton = new JButton("Load Texture");
        loadTextureButton.setFont(new Font(FONT, Font.PLAIN, 9));
        loadTextureButton.setBounds(10, 21, 97, 23);
        loadTextureButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                try {
                    File folder = new File(EntityEditor.class.getResource(SLASHED_MODEL_DIR).toURI());
                    chooser.setCurrentDirectory(folder);
                } catch (URISyntaxException e1) {
                    LOGGER.info(COULD_NOT_SET_CURRENT_DIRECTORY);
                }

                FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Files", "png");
                chooser.setFileFilter(filter);
                chooser.showOpenDialog(null);
                File chosen = chooser.getSelectedFile();
                if (chosen != null && chosen.exists()) {
                    data.setTextureFile(chosen);
                    updateTexturePreview(chosen);
                    if (data.getModelFile() != null) {
                        editor3dCore.triggerUpdateTexture();
                    }
                    editor3dCore.updateData();
                }
            }

        });
        texturePanel.add(loadTextureButton);

        textureNameLabel = new JLabel("Texture Name");
        textureNameLabel.setBounds(20, 54, 140, 14);
        texturePanel.add(textureNameLabel);

        iconPreviewLabel = new JLabel();
        iconPreviewLabel.setBounds(198, 11, 128, 128);
        texturePanel.add(iconPreviewLabel);

        transparentCheckbox = new JCheckBox("Transparent");
        transparentCheckbox.setFont(new Font(FONT, Font.PLAIN, 9));
        transparentCheckbox.setBounds(10, 73, 86, 23);
        transparentCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                data.setTransparent(transparentCheckbox.isSelected());
                if (editor3dCore != null) {
                    editor3dCore.updateData();
                }
            }
        });
        texturePanel.add(transparentCheckbox);

        JLabel shineDamperLabel = new JLabel("Shine damper");
        shineDamperLabel.setBounds(10, 106, 81, 14);
        texturePanel.add(shineDamperLabel);

        shineDamperTextfield = new JTextField();
        shineDamperTextfield.setBounds(105, 103, 68, 20);
        texturePanel.add(shineDamperTextfield);
        shineDamperTextfield.setDocument(new TextureDocument(shineDamperTextfield, 1));
        shineDamperTextfield.setColumns(10);

        JLabel reflectivityLabel = new JLabel("Reflectivity");
        reflectivityLabel.setBounds(10, 131, 81, 14);
        texturePanel.add(reflectivityLabel);

        reflectivityTextfield = new JTextField();
        reflectivityTextfield.setColumns(10);
        reflectivityTextfield.setBounds(105, 128, 68, 20);
        reflectivityTextfield.setDocument(new TextureDocument(reflectivityTextfield, 0));
        texturePanel.add(reflectivityTextfield);

        JLabel numColumsLabel = new JLabel("Num Columns");
        numColumsLabel.setBounds(10, 156, 81, 14);
        texturePanel.add(numColumsLabel);

        fakelightCheckbox = new JCheckBox("Fake Lighting");
        fakelightCheckbox.setFont(new Font(FONT, Font.PLAIN, 9));
        fakelightCheckbox.setBounds(95, 73, 97, 23);
        fakelightCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                data.setFakeLighting(fakelightCheckbox.isSelected());
                if (editor3dCore != null) {
                    editor3dCore.updateData();
                }
            }
        });
        texturePanel.add(fakelightCheckbox);

        numColumSpinner = new JSpinner();
        numColumSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
        numColumSpinner.setBounds(105, 153, 71, 20);
        numColumSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                data.setNumColums((int) numColumSpinner.getValue());
                if (editor3dCore != null) {
                    editor3dCore.updateData();
                }
            }
        });
        texturePanel.add(numColumSpinner);
    }

    private void updateTexturePreview(File chosen) {
        ImageIcon imageIcon = new ImageIcon(chosen.getAbsolutePath());
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        iconPreviewLabel.setIcon(imageIcon);
    }

    private void modelPanel(JPanel mainSettingsPanel) {
        JPanel modelPanel = new JPanel();
        modelPanel.setBorder(new TitledBorder(null, "Model", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        modelPanel.setBounds(110, 11, 236, 88);
        mainSettingsPanel.add(modelPanel);

        loadModelButton = new JButton("Load Model");
        loadModelButton.setFont(new Font(FONT, Font.PLAIN, 10));
        loadModelButton.setBounds(10, 21, 87, 23);
        loadModelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Model Files", "obj");
                try {
                    File folder = new File(EntityEditor.class.getResource(SLASHED_MODEL_DIR).toURI());
                    chooser.setCurrentDirectory(folder);
                } catch (URISyntaxException e1) {
                    LOGGER.info(COULD_NOT_SET_CURRENT_DIRECTORY);
                }
                chooser.setFileFilter(filter);
                chooser.showOpenDialog(null);
                File chosen = chooser.getSelectedFile();
                if (chosen != null && chosen.exists()) {
                    modelNameLabel.setText(chosen.getName().substring(0, chosen.getName().lastIndexOf(DOT)));
                    data.setModelFile(chosen);
                    editor3dCore.triggerLoadModel();
                    if (data.getTextureFile() != null) {
                        editor3dCore.updateTexture();
                    }
                    editor3dCore.updateData();
                }
            }
        });
        modelPanel.setLayout(null);
        modelPanel.add(loadModelButton);

        modelNameLabel = new JLabel("Modelname");
        modelNameLabel.setBounds(107, 25, 119, 14);
        modelPanel.add(modelNameLabel);

        scaleSlider = new JSlider();
        scaleSlider.setValue(10);
        scaleSlider.setMajorTickSpacing(10);
        scaleSlider.setMinorTickSpacing(1);
        scaleSlider.setMaximum(1000);
        scaleSlider.setBounds(40, 55, 161, 23);
        scaleSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                data.setScale(scaleSlider.getValue() / 10.0f);
                if (editor3dCore != null) {
                    editor3dCore.updateData();
                }
                updateEditor(data);

            }
        });
        modelPanel.add(scaleSlider);

        JLabel scaleLabel = new JLabel("Scale");
        scaleLabel.setFont(new Font(FONT, Font.PLAIN, 9));
        scaleLabel.setBounds(10, 59, 30, 14);
        modelPanel.add(scaleLabel);

        scaleValuelabel = new JLabel("1.0");
        scaleValuelabel.setBounds(201, 59, 25, 14);
        modelPanel.add(scaleValuelabel);
    }

    private void createMenue() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menueFile = new JMenu("File");
        menuBar.add(menueFile);

        JMenuItem menueItemNew = new JMenuItem("New");
        menueFile.add(menueItemNew);

        JMenuItem menueItemSave = new JMenuItem("Save");
        menueItemSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File folder;
                try {
                    folder = new File(EntityEditor.class.getResource(SLASHED_MODEL_DIR).toURI());
                    File targetFolder = new File(folder, "M" + Integer.parseInt(idTextfield.getText()));
                    targetFolder.mkdirs();
                    if (data.getModelFile() != null && !data.getModelFile().equals(new File(targetFolder, MODEL_OBJ))) {
                        FileUtils.copyFile(data.getModelFile(), new File(targetFolder, MODEL_OBJ));
                    }
                    if (data.getTextureFile() != null && !data.getTextureFile().equals(new File(targetFolder, TEXTURE_PNG))) {
                        FileUtils.copyFile(data.getTextureFile(), new File(targetFolder, TEXTURE_PNG));
                    }
                    File nameFile = new File(targetFolder, FilenameUtils.removeExtension(data.getModelFile().getName()));
                    nameFile.createNewFile();
                    Map<String, Object> serialization = new HashMap<>();
                    serialization.put("numColumns", data.getNumColums());
                    serialization.put("reflectivity", data.getReflectivity());
                    serialization.put("shineDamper", data.getShineDamper());
                    serialization.put("scale", data.getScale());
                    Map<String, String> components = new HashMap<>();
                    for (String component : data.getComponentsAdded()) {
                        Component c = editor3dCore.getComponent(component);
                        if (c.isValidForSaving()) {
                            components.put(component, c.serialize());
                        }
                    }
                    serialization.put(COMPONENTS, components);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(new File(targetFolder, ENTITY_ENT), serialization);
                } catch (URISyntaxException | IOException e1) {
                    LOGGER.error("Could not write EntitySchema: ", e1);
                }
            }

        });
        menueFile.add(menueItemSave);

        JMenuItem menueItemLoad = new JMenuItem("Load");
        menueItemLoad.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                try {
                    File folder = new File(EntityEditor.class.getResource(SLASHED_MODEL_DIR).toURI());
                    chooser.setCurrentDirectory(folder);
                } catch (URISyntaxException e1) {
                    LOGGER.info(COULD_NOT_SET_CURRENT_DIRECTORY);
                }
                chooser.showOpenDialog(null);
                File chosen = chooser.getSelectedFile();
                if (chosen != null && chosen.getName().matches("M\\d{5}") && new File(chosen, ENTITY_ENT).exists()) {
                    editor3dCore.doRender(false);
                    data = new EditorData();
                    editor3dCore.setEditorData(data);
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JsonNode tree = mapper.readTree(new File(chosen, ENTITY_ENT));
                        data.setId(Integer.parseInt(chosen.getName().substring(1)));
                        data.setNumColums(tree.get("numColumns").getIntValue());
                        data.setReflectivity((float) tree.get("reflectivity").getDoubleValue());
                        data.setShineDamper((float) tree.get("shineDamper").getDoubleValue());
                        data.setScale((float) tree.get("scale").getDoubleValue());
                        if (new File(chosen, MODEL_OBJ).exists()) {
                            data.setModelFile(new File(chosen, MODEL_OBJ));
                            editor3dCore.createNewEntity();
                            editor3dCore.triggerLoadModel();
                        }
                        if (new File(chosen, TEXTURE_PNG).exists()) {
                            data.setTextureFile(new File(chosen, TEXTURE_PNG));
                            editor3dCore.triggerUpdateTexture();
                            updateTexturePreview(data.getTextureFile());
                        }
                        Iterator<String> componentNamesIterator = tree.get(COMPONENTS).getFieldNames();
                        while (componentNamesIterator.hasNext()) {
                            String name = componentNamesIterator.next();
                            editor3dCore.addComponent(name);
                            Component c = editor3dCore.getComponent(name);
                            c.deserialize(tree.get(COMPONENTS).get(name));
                            data.getComponentsAdded().add(name);
                        }
                    } catch (IOException e1) {
                        LOGGER.error("Could not read entity file in path " + chosen);

                    }
                    updateEditor(data);
                    fillComponentComboAndList();
                    editor3dCore.updateData();
                    editor3dCore.doRender(true);

                }
            }
        });
        menueFile.add(menueItemLoad);
    }

    /**
     * Once the Canvas is created its add notify method will call this method to start the LWJGL Display and game loop in another thread.
     */
    public void startLWJGL() {
        messageQueue = new LinkedBlockingQueue<String>();
        editor3dCore = new Editor3DCore(displayParent, WIDTH, HEIGHT, messageQueue);
        editor3dCore.setEditorData(data);
        gameThread = new Thread(editor3dCore);
        gameThread.start();
    }

    /**
     * Tell game loop to stop running, after which the LWJGL Display will be destoryed. The main thread will wait for the Display.destroy()
     * to complete
     */
    private void stopLWJGL() {
        try {
            editor3dCore.stop();
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    /**
     * Listener for damper and reflectivity textfield.
     * 
     * @author Josch Bosch
     */
    private class TextureDocument extends PlainDocument {

        private static final long serialVersionUID = 1L;

        private final JTextField textF;

        private final int type;

        public TextureDocument(JTextField text, int type) {

            this.textF = text;
            this.type = type;
        }

        @Override
        public void insertString(int off, String str, AttributeSet as) throws BadLocationException {
            for (int i = 0; i < str.length(); i++) {
                if (!(Character.isDigit(str.charAt(i)) || str.charAt(i) == '.')) {
                    return;
                }
            }
            super.insertString(off, str, as);
            verifyFloat();
            if (editor3dCore != null) {
                editor3dCore.updateData();
            }
        }

        private void verifyFloat() {
            boolean isvalid = true;
            String text = textF.getText();
            float value = Float.parseFloat(text);

            if (value < 0) {
                isvalid = false;
            }

            if (isvalid) {
                textF.setBackground(Color.WHITE);
            } else {
                textF.setBackground(Color.RED);
            }
            if (isvalid) {
                if (type == 0) {
                    data.setReflectivity(value);
                } else {
                    data.setShineDamper(value);
                }

            }
        }
    }

    private boolean verifyID() {
        boolean isvalid = true;
        String text = idTextfield.getText();
        int id = Integer.parseInt(text);
        try {
            File folder = new File(EntityEditor.class.getResource(SLASHED_MODEL_DIR).toURI());
            File folder2 = new File(folder, "M" + id);
            if (folder2.exists()) {
                isvalid = false;
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Could not load folder with id " + id);
        }
        if (id < EntityEditor.MINIMUM_ID) {
            isvalid = false;
        }

        if (isvalid) {
            idTextfield.setBackground(Color.WHITE);
        } else {
            idTextfield.setBackground(Color.RED);
        }
        if (isvalid) {
            data.setId(id);
        }
        return isvalid;
    }

    /**
     * Listener for id textfield.
     * 
     * @author Josch Bosch
     */
    private class IDDocument extends PlainDocument {

        private static final long serialVersionUID = 1L;

        @Override
        public void insertString(int off, String str, AttributeSet as) throws BadLocationException {
            if (idTextfield.getText().length() >= 5) {
                return;
            }

            for (int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return;
                }
            }
            super.insertString(off, str, as);
            verifyID();
            if (editor3dCore != null) {
                editor3dCore.updateData();
            }
        }

    }
}
