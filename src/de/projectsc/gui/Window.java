/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.concurrent.BlockingQueue;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import de.projectsc.gui.states.State;

/**
 * Class for showing the GUI.
 * 
 * @author Josch Bosch
 */
public class Window {

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

    private final GLFWErrorCallback errorCallback;

    private final long window;

    private final int width;

    private final int height;

    private final GLFWMouseButtonCallback mouseCallback;

    private final GLFWScrollCallback scrollCallback;

    /**
     * Creates a GLFW window and its OpenGL context with the specified width, height and title.
     * 
     * @param width Width of the drawing area
     * @param height Height of the drawing area
     * @param title Title of the window
     * @param vsync Set to true, if you want v-sync
     */
    public Window(int width, int height, CharSequence title, boolean vsync, final BlockingQueue<InputData> inputQueue) {

        this.width = width;
        this.height = height;
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        errorCallback = errorCallbackPrint(System.err);
        glfwSetErrorCallback(errorCallback);

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL11.GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are
                                  // already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden
                                                // after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed,
        // repeated or released.
        keyCallback = new GLFWKeyCallback() {

            @Override
            public void invoke(long internalWindow, int key, int scancode, int action,
                int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(internalWindow, GL_TRUE); // We will detect
                } else {
                    DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
                    DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
                    glfwGetCursorPos(internalWindow, x, y);
                    inputQueue.offer(new InputData(InputData.TYPE_KEY, key, action, mods, new double[] { x.get(), y.get() }));
                }
            }
        };
        glfwSetKeyCallback(window, keyCallback);
        mouseCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long internalWindow, int button, int action, int mods) {
                DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(internalWindow, x, y);
                inputQueue.offer(new InputData(InputData.TYPE_MOUSE_KEY, button, action, mods, new double[] { x.get(), y.get() }));
            }
        };
        glfwSetMouseButtonCallback(window, mouseCallback);
        scrollCallback = new GLFWScrollCallback() {

            @Override
            public void invoke(long internalWindow, double xoffset, double yoffset) {
                DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(internalWindow, x, y);
                inputQueue.offer(new InputData(InputData.TYPE_MOUSE_SCROLL, (int) xoffset, (int) yoffset, 0, new double[] { x.get(),
                    y.get() }));

            }
        };
        glfwSetScrollCallback(window, scrollCallback);
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
        // Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
        // font = new TrueTypeFont(awtFont, true);
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
     * Sets the window title.
     * 
     * @param title New window title
     */
    public void setTitle(CharSequence title) {
        glfwSetWindowTitle(window, title);
    }

    /**
     * Updates the screen.
     */
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
     * @param setVsync Set to true to enable v-sync
     */
    public void setVSync(boolean setVsync) {
        this.vsync = setVsync;
        if (setVsync) {
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

    /**
     * Render the current state.
     * 
     * @param state to be rendered
     */
    public void render(State state) {
        state.render();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
