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

package se.kristofer.karlsson.util.bunchpackager.ant;

import java.io.File;

import org.apache.tools.ant.Task;

import se.kristofer.karlsson.util.bunchpackager.backend.BunchPackager;

public class BunchPackagerTask extends Task {
    private BunchPackager packager;

    public BunchPackagerTask() {
        packager = new BunchPackager();
    }

    public void setInputdir(File f) {
        packager.setInputdir(f);
    }
    
    public void setOutputdir(File f) {
        packager.setOutputdir(f);
    }
    
    public void setMaxwidth(int w) {
        packager.setMaxwidth(w);
    }
    
    public void setMaxheight(int h) {
        packager.setMaxheight(h);
    }

    public void setRuntime(long seconds) {
        packager.setRuntime(seconds);
    }

    public void setForcedupdate(boolean b) {
        packager.setForcedupdate(b);
    }

    public void execute() {
        packager.execute();
    }
}
