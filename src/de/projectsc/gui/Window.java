package de.projectsc.gui;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Font;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

import de.projectsc.gui.states.State;

public class Window implements State {

	/**
	 * Stores the window handle.
	 */
	/**
	 * Key callback for the window.
	 */
	private final GLFWKeyCallback keyCallback;

	/**
	 * Shows if vsync is enabled.
	 */
	private boolean vsync;

	private GLFWErrorCallback errorCallback;

	private final long window;

	private final TrueTypeFont font;

	/**
	 * Creates a GLFW window and its OpenGL context with the specified width,
	 * height and title.
	 * 
	 * @param width
	 *            Width of the drawing area
	 * @param height
	 *            Height of the drawing area
	 * @param title
	 *            Title of the window
	 * @param vsync
	 *            Set to true, if you want v-sync
	 */
	public Window(int width, int height, CharSequence title, boolean vsync) {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden
												// after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action,
					int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, GL_TRUE); // We will detect
																// this in our
																// rendering
																// loop
			}
		});

		// Get the resolution of the primary monitor
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - width) / 2,
				(GLFWvidmode.height(vidmode) - height) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		// Make the window visible
		if (vsync) {
			glfwSwapInterval(1);
		}
		glfwShowWindow(window);
		GLContext.createFromCurrent();
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		font = new TrueTypeFont(awtFont, true);
	}

	/**
	 * Returns if the window is closing.
	 * 
	 * @return true if the window should close, else false
	 */
	public boolean isClosing() {
		return glfwWindowShouldClose(window) == GL_TRUE;
	}

	/**
	 * Sets the window title
	 * 
	 * @param title
	 *            New window title
	 */
	public void setTitle(CharSequence title) {
		glfwSetWindowTitle(window, title);
	}

	/**
	 * Updates the screen.
	 */
	@Override
	public void update() {
		glfwSwapBuffers(window);
		glfwPollEvents();
	}

	/**
	 * Destroys the window an releases its callbacks.
	 */
	public void destroy() {
		glfwDestroyWindow(window);
		keyCallback.release();
	}

	/**
	 * Setter for v-sync.
	 * 
	 * @param vsync
	 *            Set to true to enable v-sync
	 */
	public void setVSync(boolean vsync) {
		this.vsync = vsync;
		if (vsync) {
			glfwSwapInterval(1);
		} else {
			glfwSwapInterval(0);
		}
	}

	/**
	 * Check if v-sync is enabled.
	 * 
	 * @return true if v-sync is enabled
	 */
	public boolean isVSyncEnabled() {
		return this.vsync;
	}

	@Override
	public void render() {
		// Set the clear color
		if (glfwGetKey(window, GLFW_KEY_V) == 1
				|| glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == 1) {
			glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
			vsync = false;
		} else {
			glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
			vsync = true;
		}
		DoubleBuffer b1 = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer b2 = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window, b1, b2);
		Color.white.bind();
		font.drawString(
				100,
				50,
				"THE LIGHTWEIGHT JAVA GAMES LIBRARY" + b1.get(0) + "   "
						+ b2.get(0), Color.yellow);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
															// framebuffer
		glfwSwapBuffers(window); // swap the color buffers
		// invoked during this call.
		glfwPollEvents();
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
}
