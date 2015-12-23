package de.projectsc;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.impl.ComponentListItem;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.editor.EditorData;
import de.projectsc.editor.MapEditorGraphicsCore;

/**
 * Entity editor.
 *
 * @author Josch Bosch
 */
public class MapEditor extends JFrame {

    /**
     * Smallest id for models.
     */
    public static final int MINIMUM_ID = 10000;

    private static final String COULD_NOT_SET_CURRENT_DIRECTORY = "Could not set current directory.";

    private static final String COMPONENTS = "components";

    private static final Log LOGGER = LogFactory.getLog(MapEditor.class);

    private static final long serialVersionUID = 3313139728699706144L;

    private static final String SLASHED_MODEL_DIR = "/" + CoreConstants.SCHEME_DIRECTORY_NAME + "/";

    private JPanel contentPane;

    private Canvas displayParent;

    private Thread gameThread;

    private MapEditorGraphicsCore graphicsCore;

    private BlockingQueue<String> messageQueue;

    private final EditorData data;

    /**
     * Create the frame.
     */
    public MapEditor() {
        data = new EditorData();
        loadComponents();
        createContent();
        try {
            File folder = new File(MapEditor.class.getResource(SLASHED_MODEL_DIR).toURI());
            for (int i = 0; i < 10000; i++) {
                File f = new File(folder, CoreConstants.SCHEME_DIRECTORY_PREFIX + (MINIMUM_ID + i));
                if (!f.exists()) {
                    data.setId(MINIMUM_ID + i);
                    break;
                }
            }
        } catch (URISyntaxException e) {
            LOGGER.error("Could not read model data.");
        }
    }

    /**
     * Launch the application.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MapEditor frame = new MapEditor();
                frame.setVisible(true);
            }
        });
    }

    private void createContent() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 950);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                remove(displayParent);
                dispose();
            }
        });

        createMenue();
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BorderLayout(0, 0));
        previewPanel.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        previewPanel.setBounds(10, 87, 1024, 768);
        contentPane.add(previewPanel);

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
        previewPanel.add(displayParent);

        JButton btnRemove = new JButton("Remove");
        btnRemove.setBounds(120, 53, 89, 23);
        btnRemove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {}
        });
        contentPane.add(btnRemove);

        JRadioButton addButton = new JRadioButton("Add Entity");
        addButton.setBounds(6, 23, 109, 23);

        contentPane.add(addButton);

        JRadioButton selectButton = new JRadioButton("Select entity");
        selectButton.setBounds(120, 23, 109, 23);
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (addButton.isSelected()) {
                    graphicsCore.setMode("add");
                    selectButton.setSelected(false);
                }
            }
        });
        selectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectButton.isSelected()) {
                    addButton.setSelected(false);
                    graphicsCore.setMode("select");
                }
            }
        });
        contentPane.add(selectButton);
    }

    private void createMenue() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menueFile = new JMenu("File");
        menuBar.add(menueFile);

        JMenuItem menueItemNew = new JMenuItem("New");
        menueItemNew.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                graphicsCore.triggerCreateTerrain(1000, 100, "mud");
            }
        });
        menueFile.add(menueItemNew);

        JMenuItem menueItemSave = new JMenuItem("Save");
        menueItemSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                graphicsCore.performSave();
            }

        });
        menueFile.add(menueItemSave);

        JMenuItem menueItemLoad = new JMenuItem("Load");
        menueItemLoad.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                try {
                    File folder = new File(MapEditor.class.getResource(SLASHED_MODEL_DIR).toURI());
                    chooser.setCurrentDirectory(folder);
                } catch (URISyntaxException e1) {
                    LOGGER.info(COULD_NOT_SET_CURRENT_DIRECTORY);
                }
                chooser.showOpenDialog(null);
                File chosen = chooser.getSelectedFile();
                if (chosen != null) {

                    graphicsCore.doRender(true);

                }
            }
        });
        menueFile.add(menueItemLoad);
    }

    private void loadComponents() {
        for (ComponentListItem it : ComponentListItem.values()) {
            ComponentManager.registerComponent(it.getName(), it.getClazz());
        }
    }

    /**
     * Once the Canvas is created its add notify method will call this method to start the LWJGL / Display and game loop in another thread.
     */
    public void startLWJGL() {
        messageQueue = new LinkedBlockingQueue<String>();
        graphicsCore = new MapEditorGraphicsCore(displayParent, 1024, 768, messageQueue);
        gameThread = new Thread(graphicsCore);
        gameThread.start();
    }

    /**
     * Tell game loop to stop running, after which the LWJGL Display will be destoryed. The main thread will wait for the Display.destroy()
     * to complete
     */
    private void stopLWJGL() {
        try {
            graphicsCore.stop();
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

}
