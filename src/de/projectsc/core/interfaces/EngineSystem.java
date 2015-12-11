/*
 * Copyright (C) 2015
 */

package de.projectsc.core.interfaces;

import de.projectsc.core.data.Event;

public interface EngineSystem {

    void processEvent(Event e);

    String getName();

}
