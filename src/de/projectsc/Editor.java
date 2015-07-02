/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.projectsc.editor.Editor3DCore;

/**
 * World editor.
 * 
 * @author Josch Bosch
 */
public class Editor extends JFrame {

    /**
     * UID.
     */
    private static final long serialVersionUID = 1L;

    private JFrame frame;

    private final JPanel panel1 = new JPanel();

    private final JPanel panel2 = new JPanel();

    private Canvas displayParent;

    private Thread gameThread;

    private Editor3DCore editor3dCore;

    private BlockingQueue<String> messageQueue;

    public Editor() {}

    /**
     * Once the Canvas is created its add notify method will call this method to start the LWJGL
     * Display and game loop in another thread.
     */
    public void startLWJGL() {
        messageQueue = new LinkedBlockingQueue<String>();
        editor3dCore = new Editor3DCore(displayParent, WIDTH, HEIGHT, messageQueue);
        gameThread = new Thread(editor3dCore);
        gameThread.start();
    }

    /**
     * Tell game loop to stop running, after which the LWJGL Display will be destoryed. The main
     * thread will wait for the Display.destroy() to complete
     */
    private void stopLWJGL() {
        try {
            editor3dCore.stop();
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    private void execute() {
        frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                frame.remove(displayParent);
                frame.dispose();
            }
        });
        frame.setBounds(100, 100, 1024, 768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
        frame.getContentPane().add(panel1);
        panel1.setLayout(null);

        JButton button = new JButton("Load Map");
        button.setBounds(0, 0, 100, 50);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                messageQueue.offer("Load Map");

            }
        });
        panel1.add(button);

        frame.getContentPane().add(panel2);
        panel2.setLayout(new BorderLayout(0, 0));

        displayParent = new Canvas() {

            /**
             * UID.
             */
            private static final long serialVersionUID = -6934397843291223284L;

            @Override
            public void addNotify() {
                super.addNotify();
                startLWJGL();
            }

            @Override
            public void removeNotify() {
                stopLWJGL();
                super.removeNotify();
            }
        };
        displayParent.setFocusable(true);
        displayParent.requestFocus();
        displayParent.setIgnoreRepaint(true);
        panel2.add(displayParent);
    }

    /**
     * Main.
     * 
     * @param args program args
     * */
    public static void main(String[] args) {
        Editor sit = new Editor();
        sit.initialize();
        sit.execute();
    }
}
