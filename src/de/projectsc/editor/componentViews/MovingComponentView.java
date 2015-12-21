/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.editor.componentViews;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.projectsc.core.component.impl.physic.VelocityComponent;
import de.projectsc.core.events.movement.ChangeMovementParameterEvent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.editor.ComponentView;

/**
 * Editor view for the {@link VelocityComponent}.
 * 
 * @author Josch Bosch
 */
public class MovingComponentView extends ComponentView {

    private static final long serialVersionUID = -504709352705363482L;

    private VelocityComponent component;

    private final JSlider movementSlider;

    private final JLabel movementLabel;

    private final JSlider accelerationSlider;

    private final JLabel accelerationLabel;

    public MovingComponentView() {
        setTitle("Velocity Component");
        getContentPane().setLayout(null);

        JLabel lblMovementSpeed = new JLabel("Maximum movement speed:");
        lblMovementSpeed.setBounds(0, 0, 434, 14);
        getContentPane().add(lblMovementSpeed);

        movementSlider = new JSlider();
        movementSlider.setMinorTickSpacing(1);
        movementSlider.setMajorTickSpacing(10);
        movementSlider.setMinimum(1);
        movementSlider.setMaximum(10000);
        movementSlider.setBounds(0, 14, 377, 77);

        movementLabel = new JLabel("1");
        movementLabel.setHorizontalAlignment(SwingConstants.CENTER);
        movementLabel.setBounds(387, 14, 47, 77);
        getContentPane().add(movementLabel);

        movementSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                movementLabel.setText("" + movementSlider.getValue() / 100f);
                ChangeMovementParameterEvent ev = new ChangeMovementParameterEvent(entity);
                ev.setMaximumSpeed(movementSlider.getValue() / 100f);
                EventManager.fireEvent(ev);
            }
        });
        getContentPane().add(movementSlider);
        JLabel lblAcceleration = new JLabel("Acceleration:");
        lblAcceleration.setBounds(0, 100, 434, 14);
        getContentPane().add(lblAcceleration);

        accelerationSlider = new JSlider();
        accelerationSlider.setMinorTickSpacing(1);
        accelerationSlider.setMajorTickSpacing(10);
        accelerationSlider.setMinimum(1);
        accelerationSlider.setMaximum(10000);
        accelerationSlider.setBounds(0, 114, 377, 77);

        accelerationLabel = new JLabel("1");
        accelerationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        accelerationLabel.setBounds(387, 114, 47, 77);
        getContentPane().add(accelerationLabel);

        accelerationSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                accelerationLabel.setText("" + accelerationSlider.getValue() / 100f);
                ChangeMovementParameterEvent ev = new ChangeMovementParameterEvent(entity);
                ev.setAcceleration(accelerationSlider.getValue() / 100f);
                EventManager.fireEvent(ev);
            }
        });
        getContentPane().add(accelerationSlider);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        this.component = (VelocityComponent) getAssociatedComponent();
        movementSlider.setValue((int) component.getMaximumSpeed() * 100);
        movementLabel.setText("" + component.getMaximumSpeed());
        accelerationSlider.setValue((int) component.getAcceleration() * 100);
        accelerationLabel.setText("" + component.getAcceleration());

    }

    @Override
    protected Component getAssociatedComponent() {
        return EntityManager.getComponent(entity, VelocityComponent.class);
    }

    @Override
    protected int getInitialHeight() {
        return 600;
    }

    @Override
    protected int getInitialWidth() {
        return 800;
    }
}
