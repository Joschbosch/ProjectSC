package de.projectsc.core;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GLContext;

public class Window {

	/**
	 * Stores the window handle.
	 */
	private final long id;

	/**
	 * Key callback for the window.
	 */
	private final GLFWKeyCallback keyCallback;

	/**
	 * Shows if vsync is enabled.
	 */
	private boolean vsync;

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
		this.vsync = vsync;
		/* Creating a temporary window for getting the available OpenGL version */
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		long temp = glfwCreateWindow(1, 1, "", NULL, NULL);
		glfwMakeContextCurrent(temp);
		GLContext.createFromCurrent();
		String version = glGetString(GL_VERSION);
		glfwDestroyWindow(temp);
		int major = Character.getNumericValue(version.charAt(0));
		int minor = Character.getNumericValue(version.charAt(2));
		/* Reset and set window hints */
		glfwDefaultWindowHints();
		if (major > 3 || (major == 3 && minor >= 2)) {
			/* Hints for OpenGL 3.2 core profile */
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		} else {
			/* Hints for legacy OpenGL 2.1 */
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		}
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		/* Create window with specified OpenGL context */
		id = glfwCreateWindow(width, height, title, NULL, NULL);
		if (id == NULL) {
			glfwTerminate();
			throw new RuntimeException("Failed to create the GLFW window!");
		}
		/* Center window on screen */
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(id, (GLFWvidmode.width(vidmode) - width) / 2,
				(GLFWvidmode.height(vidmode) - height) / 2);
		/* Create OpenGL context */
		glfwMakeContextCurrent(id);
		GLContext.createFromCurrent();
		/* Enable v-sync */
		if (vsync) {
			glfwSwapInterval(1);
		}
		/* Set key callback */
		keyCallback = new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action,
					int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
					glfwSetWindowShouldClose(window, GL_TRUE);
				}
			}
		};
		glfwSetKeyCallback(id, keyCallback);
	}

	/**
	 * Returns if the window is closing.
	 * 
	 * @return true if the window should close, else false
	 */
	public boolean isClosing() {
		return glfwWindowShouldClose(id) == GL_TRUE;
	}

	/**
	 * Sets the window title
	 * 
	 * @param title
	 *            New window title
	 */
	public void setTitle(CharSequence title) {
		glfwSetWindowTitle(id, title);
	}

	/**
	 * Updates the screen.
	 */
	public void update() {
		glfwSwapBuffers(id);
		glfwPollEvents();
	}

	/**
	 * Destroys the window an releases its callbacks.
	 */
	public void destroy() {
		glfwDestroyWindow(id);
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
}
