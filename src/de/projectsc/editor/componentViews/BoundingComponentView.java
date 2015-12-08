/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.editor.componentViews;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.data.entities.components.physic.BoundingComponent;
import de.projectsc.editor.EditorGraphicsCore;

/**
 * Editor view for the {@link BoundingComponent}.
 * 
 * @author Josch Bosch
 */
public class BoundingComponentView extends JDialog {

    protected static final Log LOGGER = LogFactory.getLog(BoundingComponentView.class);

    private static final long serialVersionUID = -504709352705363482L;

    private final JTextField offsetXText;

    private final JTextField offsetYText;

    private final JTextField offsetZText;

    private final BoundingComponent component;

    public BoundingComponentView(BoundingComponent component, EditorGraphicsCore core) {
        setTitle("Bounding Component");
        getContentPane().setLayout(null);
        this.component = component;
        JLabel scaleLabel = new JLabel("Scale");
        scaleLabel.setBounds(10, 90, 434, 14);
        getContentPane().add(scaleLabel);

        JSlider scaleSlider = new JSlider();
        scaleSlider.setValue(10);
        scaleSlider.setMajorTickSpacing(10);
        scaleSlider.setMinorTickSpacing(1);
        scaleSlider.setMaximum(1000);
        scaleSlider.setBounds(40, 55, 161, 23);
        scaleSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (component != null) {
                    component.setScale(scaleSlider.getValue() / 10.0f);
                }
            }
        });

        JLabel scaleValueLabel = new JLabel("1");
        scaleValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scaleValueLabel.setBounds(387, 102, 47, 77);
        getContentPane().add(scaleValueLabel);

        scaleSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                scaleValueLabel.setText("" + scaleSlider.getValue() / 10.0f);
            }
        });
        getContentPane().add(scaleSlider);

        JLabel modelPathLabel = new JLabel("");
        modelPathLabel.setBounds(10, 47, 46, 14);
        getContentPane().add(modelPathLabel);

        JButton boxModelButton = new JButton("Load box model");
        boxModelButton.setBounds(10, 13, 89, 23);
        boxModelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Model Files", "obj");
                try {
                    File folder = new File(BoundingComponentView.class.getResource("/" + CoreConstants.SCHEME_DIRECTORY_NAME).toURI());
                    chooser.setCurrentDirectory(folder);
                } catch (URISyntaxException e1) {
                    LOGGER.info("Loading box model failed", e1);
                }
                chooser.setFileFilter(filter);
                chooser.showOpenDialog(null);
                File chosen = chooser.getSelectedFile();
                if (chosen != null && chosen.exists()) {
                    modelPathLabel.setText(chosen.getAbsolutePath());
                    component.setBoxFile(chosen);
                    if (core != null) {
                        core.triggerLoadBoundingBox();
                    }
                }
            }
        });
        getContentPane().add(boxModelButton);

        JLabel lblOffset = new JLabel("Offset");
        lblOffset.setBounds(10, 178, 46, 14);
        getContentPane().add(lblOffset);

        offsetXText = new JTextField();
        offsetXText.setBounds(10, 203, 47, 20);
        getContentPane().add(offsetXText);
        offsetXText.setDocument(new OffsetDocument(0));
        offsetXText.setColumns(10);

        offsetYText = new JTextField();
        offsetYText.setColumns(10);
        offsetYText.setBounds(67, 203, 47, 20);

        offsetYText.setDocument(new OffsetDocument(1));
        getContentPane().add(offsetYText);

        offsetZText = new JTextField();
        offsetZText.setColumns(10);
        offsetZText.setBounds(124, 203, 47, 20);
        offsetZText.setDocument(new OffsetDocument(2));
        getContentPane().add(offsetZText);

    }

    /**
     * Listener for id textfield.
     * 
     * @author Josch Bosch
     */
    private class OffsetDocument extends PlainDocument {

        private static final long serialVersionUID = 1L;

        private final int index;

        OffsetDocument(int i) {
            this.index = i;
        }

        @Override
        public void insertString(int off, String str, AttributeSet as) throws BadLocationException {

            for (int i = 0; i < str.length(); i++) {
                if (!(Character.isDigit(str.charAt(i)) || str.charAt(i) == '.' || str.charAt(i) == '-')) {
                    return;
                }
            }
            try {
                Float.parseFloat(str);
            } catch (NumberFormatException e) {
                return;
            }
            super.insertString(off, str, as);
            if (component != null) {
                switch (index) {
                case 0:
                    component.getOffset().x = Float.parseFloat(offsetXText.getText());
                    break;
                case 1:
                    component.getOffset().y = Float.parseFloat(offsetYText.getText());
                    break;
                case 2:
                    component.getOffset().z = Float.parseFloat(offsetZText.getText());
                    break;
                default:
                    break;
                }
            }
        }

    }
}
