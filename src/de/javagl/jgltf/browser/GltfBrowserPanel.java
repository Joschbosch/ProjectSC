/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.browser;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import de.javagl.jgltf.browser.Resolver.ResolvedEntity;
import de.javagl.jgltf.impl.GlTF;
import de.javagl.jgltf.model.GltfData;

/**
 * A panel for browsing through the {@link GlTF} of a {@link GltfData}
 * and displaying information about the glTF entities. 
 */
class GltfBrowserPanel extends JPanel
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(GltfBrowserPanel.class.getName());

    /**
     *  Serial UID
     */
    private static final long serialVersionUID = 8959452050508861357L;

    /**
     * The tree that displays the glTF structure
     */
    private final JTree tree;
    
    /**
     * The history of tree paths that have been selected
     */
    private final Deque<TreePath> selectionPathHistory;

    /**
     * The factory for the info components of selected elements
     */
    private final InfoComponentFactory infoComponentFactory;
    
    /**
     * The {@link Resolver} that is used for resolving glTF
     * entities that are clicked in the tree
     */
    private final Resolver resolver;
    
    /**
     * The Action for going back in the selection history
     * 
     * @see #backToPreviousSelection()
     */
    private final Action backAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -515243029591873126L;

        // Initialization
        {
            putValue(NAME, "Back");
            putValue(SHORT_DESCRIPTION, "Go back to previous selection");
            putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
            setEnabled(false);
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            backToPreviousSelection();
        }
    };
    
    /**
     * A container for the panels that show information about the
     * entities that are selected in the tree
     */
    private final JPanel infoPanelContainer;
    
    /**
     * Creates a new browser panel for the given {@link GltfData}
     * 
     * @param gltfData The {@link GltfData}
     */
    GltfBrowserPanel(GltfData gltfData)
    {
        super(new BorderLayout());
        this.selectionPathHistory = new LinkedList<TreePath>();
        this.infoComponentFactory = new InfoComponentFactory(gltfData);
        this.resolver = new Resolver(gltfData.getGltf());
        
        add(createControlPanel(), BorderLayout.NORTH);
        
        JSplitPane mainSplitPane = new JSplitPane();
        add(mainSplitPane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                mainSplitPane.setDividerLocation(0.3);
            }
        });
        /*
        // XXX Test
        int XXX;
        JScrollPane sp = new JScrollPane(GltfTree.create(gltfData.getGltf()));
        sp.setPreferredSize(new Dimension(500,500));
        add(sp, BorderLayout.EAST);
        */
        
        tree = ObjectTrees.create("glTF", gltfData.getGltf());
        tree.addTreeSelectionListener(e -> treeSelectionChanged());
        
        JPopupMenu popupMenu = new JPopupMenu();
        MouseListener popupMenuMouseListener = new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (SwingUtilities.isRightMouseButton(e))
                {
                    TreePath treePath = 
                        tree.getPathForLocation(e.getX(), e.getY());
                    tree.setSelectionPath(treePath);
                    preparePopupMenu(popupMenu, treePath);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        tree.addMouseListener(popupMenuMouseListener);        
        
        mainSplitPane.setLeftComponent(new JScrollPane(tree));
        
        infoPanelContainer = new JPanel(new GridLayout(1,1));
        mainSplitPane.setRightComponent(infoPanelContainer);
        
    }
    
    /**
     * Create the control panel
     * 
     * @return The control panel
     */
    private JPanel createControlPanel()
    {
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton(backAction);
        controlPanel.add(backButton);
        return controlPanel;
    }
    
    /**
     * Go back to the previous selection in the tree
     */
    private void backToPreviousSelection()
    {
        if (!selectionPathHistory.isEmpty())
        {
            TreePath treePath = selectionPathHistory.removeLast();
            tree.setSelectionPath(treePath);
            tree.expandPath(treePath);
            scrollToTop(tree, getParentPath(treePath));
            
        }
        if (selectionPathHistory.isEmpty())
        {
            backAction.setEnabled(false);
        }
    }
    
    /**
     * Will be called whenever the tree selection changed, and update
     * the info panel based on the selected entity
     */
    private void treeSelectionChanged()
    {
        infoPanelContainer.removeAll();
        TreePath selectionPath = tree.getSelectionPath();
        JComponent infoComponent = 
            infoComponentFactory.createInfoComponent(selectionPath);
        if (infoComponent != null)
        {
            infoPanelContainer.add(infoComponent);
        }
        infoPanelContainer.validate();
        infoPanelContainer.repaint();
    }
    
    
    

    /**
     * Prepare the given popup menu to contain the items that are 
     * appropriate for the given selection path
     *  
     * @param popupMenu The popup menu
     * @param treePath The selection path
     */
    private void preparePopupMenu(
        JPopupMenu popupMenu, TreePath treePath)
    {
        popupMenu.removeAll();
        Object nodeEntryValue = ObjectTrees.getNodeEntryValue(treePath);
        if (nodeEntryValue == null)
        {
            return;
        }
        String pathString = ObjectTrees.createPathString(treePath);
        ResolvedEntity resolvedEntity = 
            resolver.resolve(pathString, nodeEntryValue);
        if (resolvedEntity == null)
        {
            return;
        }
        if (resolvedEntity.getMessage() != null)
        {
            popupMenu.add(createMessageMenuItem(resolvedEntity.getMessage()));
        }
        else
        {
            popupMenu.add(createTreeNodeSelectionMenuItem(
                resolvedEntity.getKey(), resolvedEntity.getValue()));
        }
    }
    
    
    /**
     * Create a disabled menu item with the given text
     *  
     * @param text The text
     * @return The menu item
     */
    private static JMenuItem createMessageMenuItem(String text)
    {
        JMenuItem item = new JMenuItem(text);
        item.setEnabled(false);
        return item;
    }


    /**
     * Create a menu item for selecting the node in the tree that contains 
     * the given value.
     * <br>
     * The node is determined as the first of the nodes returned by 
     * {@link ObjectTrees#findNodesWithNodeEntryValue}.
     * If there is no tree node that contains the resulting value, then
     * an "error message" menu item will be created and returned. 
     *  
     * @param key The key
     * @param value The value
     * @return The menu item
     */
    private JMenuItem createTreeNodeSelectionMenuItem(
        Object key, Object value)
    {
        List<DefaultMutableTreeNode> nodes = 
            ObjectTrees.findNodesWithNodeEntryValue(tree.getModel(), value);
        if (nodes.isEmpty())
        {
            return createMessageMenuItem("Not found: "+key);
        }
        if (nodes.size() > 1)
        {
            logger.warning("Found " + nodes.size() + " nodes for " + key);
        }
        DefaultMutableTreeNode node = nodes.get(0);
        JMenuItem item = new JMenuItem("Select "+key);
        item.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                TreePath oldTreePath = tree.getSelectionPath();
                if (oldTreePath != null)
                {
                    selectionPathHistory.add(oldTreePath);
                    backAction.setEnabled(true);
                }
                
                TreePath treePath = ObjectTrees.createPath(node);
                tree.setSelectionPath(treePath);
                tree.expandPath(treePath);
                scrollToTop(tree, treePath);
            }
        });
        return item;
    }
    
    
 
    
    
    /**
     * Returns the tree path of the parent of the given tree path. 
     * 
     * @param treePath The tree path
     * @return The parent tree path
     */
    private static TreePath getParentPath(TreePath treePath)
    {
        Object[] array = treePath.getPath();
        if (array.length == 0)
        {
            return treePath;
        }
        return new TreePath(Arrays.copyOf(array,  array.length-1));
    }
 
    /**
     * Scroll the given tree so that the given tree path is at the upper
     * border of the containing scroll pane
     * 
     * @param tree The tree
     * @param treePath The tree path
     */
    private static void scrollToTop(JTree tree, TreePath treePath)
    {
        Rectangle bounds = tree.getPathBounds(treePath);
        if (bounds == null)
        {
            return;
        }
        Container parent = tree.getParent();
        if (parent instanceof JViewport)
        {
            JViewport viewport = (JViewport)parent;
            Rectangle viewRect = viewport.getViewRect();
            Rectangle rectangle = new Rectangle(
                0, bounds.y, viewRect.width, viewRect.height);
            tree.scrollRectToVisible(rectangle);
        }
    }
    
    
}