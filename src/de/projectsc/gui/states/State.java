package de.projectsc.gui.states;

public interface State {

	void initialize();

	void pause();

	void resume();

	void update();

	void render();
}
