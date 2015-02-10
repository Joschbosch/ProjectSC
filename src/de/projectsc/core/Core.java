package de.projectsc.core;

public class Core {

	public static final int TARGET_FPS = 75;

	private static final float TARGET_UPS = 30;

	private boolean running;

	private Timer timer;

	private Window window;

	/**
	 * This should be called to initialize and start the game.
	 */
	public void start() {
		init();
		startGameLoop();
		dispose();
	}

	private void dispose() {

	}

	private void init() {
		window = new Window(640, 480, "", true);
		this.timer = new Timer();
		running = true;
	}

	public void startGameLoop() {
		float delta;
		float accumulator = 0f;
		float interval = 1f / TARGET_UPS;
		float alpha;
		while (running) {
			/* Check if game should close */
			if (window.isClosing()) {
				running = false;
			}
			/* Get delta time and update the accumulator */
			delta = timer.getDelta();
			accumulator += delta;
			/* Handle input */
			input();
			/* Update game and timer UPS if enough time has passed */
			while (accumulator >= interval) {
				update();
				timer.updateUPS();
				accumulator -= interval;
			}
			/* Calculate alpha value for interpolation */
			alpha = accumulator / interval;
			/* Render game and update timer FPS */
			render(alpha);
			timer.updateFPS();
			/* Update timer */
			timer.update();

			/* Update window to show the new screen */
			window.update();
			/* Synchronize if v-sync is disabled */
			if (!window.isVSyncEnabled()) {
				sync(TARGET_FPS);
			}
		}
	}

	private void sync(int targetFps) {

	}

	private void render(float alpha) {

	}

	private void update() {

	}

	private void input() {

	}
}
