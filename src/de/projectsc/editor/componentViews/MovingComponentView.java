/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.editor.componentViews;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.projectsc.core.data.entities.Entity;
import de.projectsc.core.data.entities.components.physic.MovingComponent;

/**
 * Editor view for the {@link MovingComponent}.
 * 
 * @author Josch Bosch
 */
public class MovingComponentView extends JDialog {

    private static final long serialVersionUID = -504709352705363482L;

    public MovingComponentView(MovingComponent component, Entity entity) {
        setTitle("Moving Component");
        getContentPane().setLayout(null);

        JLabel lblMovementSpeed = new JLabel("Movement speed:");
        lblMovementSpeed.setBounds(0, 0, 434, 14);
        getContentPane().add(lblMovementSpeed);

        JSlider movementSlider = new JSlider();
        movementSlider.setMinorTickSpacing(1);
        movementSlider.setMajorTickSpacing(10);
        movementSlider.setMinimum(1);
        movementSlider.setBounds(0, 14, 377, 77);
        movementSlider.setValue(component.getMovementSpeed());

        JLabel movementLabel = new JLabel("1");
        movementLabel.setHorizontalAlignment(SwingConstants.CENTER);
        movementLabel.setBounds(387, 14, 47, 77);
        movementLabel.setText("" + component.getMovementSpeed());
        getContentPane().add(movementLabel);

        movementSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                component.setMovementSpeed(movementSlider.getValue());
                movementLabel.setText("" + movementSlider.getValue());
            }
        });
        getContentPane().add(movementSlider);

    }
}
