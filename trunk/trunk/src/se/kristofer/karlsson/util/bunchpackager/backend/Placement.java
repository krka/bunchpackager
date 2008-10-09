package se.kristofer.karlsson.util.bunchpackager.backend;

public class Placement {

    public ImageObject io;
    public int col;
    public int row;
    
    public Placement(ImageObject io, int x, int y) {
        this.io = io;
        this.col = x;
        this.row = y;
    }
}
