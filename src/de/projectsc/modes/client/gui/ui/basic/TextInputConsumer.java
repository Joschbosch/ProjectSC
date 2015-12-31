/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.basic;

/**
 * Consumer of a text input.
 * 
 * @author Josch Bosch
 */
public interface TextInputConsumer {

    /**
     * Consumed given text.
     * 
     * @param newLine to consume
     */
    void consumeText(String newLine);

}
