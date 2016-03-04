/*
 * Copyright (C) 2016 
 */

package de.projectsc.editor.map.componentConfigurations;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Listener for textfields.
 * 
 * @author Josch Bosch
 */
public class IntegerInputVerifier extends PlainDocument {

    private static final long serialVersionUID = 1L;

    private JTextField textfield;

    public IntegerInputVerifier(JTextField textfield) {
        this.textfield = textfield;
    }

    @Override
    public void insertString(int off, String str, AttributeSet as) throws BadLocationException {
        String old = textfield.getText();
        super.insertString(off, str, as);
        String newText = textfield.getText();

        try {
            Integer.parseInt(newText);
        } catch (NumberFormatException e) {
            return;
        }
        textfield.setText(old);
    }
}
