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
