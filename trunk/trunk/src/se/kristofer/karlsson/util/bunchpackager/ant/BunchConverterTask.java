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

import se.kristofer.karlsson.util.bunchpackager.backend.BunchConverter;

public class BunchConverterTask extends Task {

    private BunchConverter bc;

    public BunchConverterTask() {
        bc = new BunchConverter();
    }
    
    public void setImage(File f) {
        bc.setImage(f);
    }
    
    public void setText(File f) {
        bc.setText(f);
    }
    
    public void setOutputdir(File f) {
        bc.setOutputdir(f);
    }
    
    public void execute() {
        bc.execute();
    }
}
