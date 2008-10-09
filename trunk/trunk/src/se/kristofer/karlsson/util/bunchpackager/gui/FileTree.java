/*
    Copyright (c) 2005, 2008 Kristofer Karlsson

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package se.kristofer.karlsson.util.bunchpackager.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class FileTree extends JPanel implements TreeSelectionListener, ActionListener {

    JTree tree;
    
    JTextField dir;
    
    File current = null;

    DefaultTreeModel model;

    private JButton refresh;
    
    public FileTree() {
        
        setLayout(new BorderLayout());

        FileTreeNode root = new FileTreeNode();
        
        //FileTreeNode root = FileTreeNode.getFileSystem(false); 
        model = new DefaultTreeModel(root);

        tree = new JTree(root);
        tree.setModel(model);
        tree.addTreeSelectionListener(this);        
        
        TreeSelectionModel tsm = new DefaultTreeSelectionModel();
        tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setSelectionModel(tsm);

        tree.setExpandsSelectedPaths(true);
        
        dir = new JTextField();
        dir.setEditable(true);
        dir.addActionListener(this);
        
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(tree);
        
        Dimension d = new Dimension(200, 400);
        scrollpane.setMinimumSize(d);
        scrollpane.setMaximumSize(d);
        scrollpane.setSize(d);
        
        tree.setMinimumSize(d);
        tree.setMaximumSize(d);
        tree.setSize(d);

        add(dir, BorderLayout.NORTH);
        add(scrollpane, BorderLayout.CENTER);

        refresh = new JButton("Refresh");
        refresh.addActionListener(this);
        JPanel p = new JPanel();
        p.add(refresh);
        
        add(p, BorderLayout.SOUTH);
        
        setCurrent(".");
    }

    public void reset() {
        String path = dir.getText();

        FileTreeNode root = (FileTreeNode) tree.getPathForRow(0).getPathComponent(0);
        root.removeAllChildren();
        root.findChildren();
        model.reload();
        setCurrent(path);
    }
    
    public Dimension getMinimumSize() {
      return new Dimension(20, 10);
    }

    public Dimension getPreferredSize() {
      return new Dimension(200, 200);
    }
    
    public void valueChanged(TreeSelectionEvent event) {
        TreePath p = event.getPath();
        FileTreeNode n = (FileTreeNode) p.getLastPathComponent();

        setCurrent(n.toPath());
    }

    public File getCurrent() {
        return current;
    }
    
    public void setCurrent(String path) {
        File f;
        try {
            f = new File(path).getCanonicalFile();
        } catch (IOException e1) {
            return;
        }
        Stack<File> s = new Stack<File>();
        
        while (f != null) {
            s.push(f);
            f = f.getParentFile();
        }
        
        FileTreeNode node = (FileTreeNode) tree.getPathForRow(0).getPathComponent(0);

        TreePath p = new TreePath(node); 

        traversal:
        while (!s.empty()) {
            f = (File) s.pop();

            if (!f.exists()) break;

            FileTreeNode new_node = null;
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                new_node = (FileTreeNode) e.nextElement();
                if (new_node.getFile().equals(f)) {
                    p = p.pathByAddingChild(new_node);
                    node = new_node;
                    continue traversal;
                }
            }
            break;
        }
        
        tree.setSelectionPath(p);
        tree.scrollPathToVisible(p);
        tree.expandPath(p);
        current = node.getFile();
        dir.setText(node.toPath());
    }
    
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == refresh) {
            reset();
            return;
        }
        
        // On enter press
        if (event.getID() == 1001) {
            setCurrent(dir.getText());
        }
    }
}
