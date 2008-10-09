package se.kristofer.karlsson.util.bunchpackager.gui;
import java.io.File;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

public class FileTreeNode extends DefaultMutableTreeNode {

    private File f;

    boolean foundChildren = false;
    
    public int hashCode() {
        if (f == null) return 0;
        return f.getAbsolutePath().hashCode();
    }
    
    public File getFile() {
        return f;
    }
    
    public FileTreeNode(File f) {
        super();
        this.f = f;
        this.allowsChildren = true;    
    }

    public FileTreeNode() {
        f = null;
        this.allowsChildren = true;    
    }

    public String toString() {
        if (f == null) return "root";
        String s = f.getName(); 
        if (s.equals("")) return f.getAbsolutePath();
        return s;
    }
    
    public String toPath() {
        if (f == null) return "";
        return f.getAbsolutePath();
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    public void findChildren() {
        foundChildren = true;
        
        File[] files = null;
        if (f == null) {
            files = File.listRoots();
        } else {
            files = f.listFiles();
        }
        if (files != null) {
	        for (int i = 0; i < files.length; i++) {
	            if (files[i].isDirectory()) {
		            FileTreeNode new_node = new FileTreeNode(files[i]); 
		            add(new_node);
	            }
	        }
        }
    }
    
    // Important overrides:
    public boolean getAllowsChildren() {
        return true;
    }
    
    public int getChildCount() {
        if (!foundChildren) findChildren();
        return super.getChildCount();
    }
    
    public Enumeration children() {
        if (!foundChildren) findChildren();
        return super.children(); 
    }
    
    
}
