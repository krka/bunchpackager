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
