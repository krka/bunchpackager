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

package se.kristofer.karlsson.util.bunchpackager.backend;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class BLResult {
    private Set<Placement> placedObjects = new HashSet<Placement>();
    
    public int width = 0;
    public int height = 0;

    public int area;

    public void placeObject(ImageObject io, int row, int col) {
        placedObjects.add(new Placement(io, col, row));
        width = Math.max(width, col + io.width);
        height = Math.max(height, row + io.height);
    }
    
    public Iterator iterator() {
        return placedObjects.iterator();
    }
}
