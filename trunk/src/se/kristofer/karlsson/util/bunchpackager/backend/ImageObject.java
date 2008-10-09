package se.kristofer.karlsson.util.bunchpackager.backend;
import java.awt.Image;

public class ImageObject {

    public String name;
    public Image data;
    
    public int width;
    public int height;

    public boolean duplicate;
    public int dup_offset_x; 
    public int dup_offset_y; 
    public ImageObject dup_image; 
    
    public ImageObject(String name, int w, int h, Image data) {
        this.name = name;
        width = w;
        height = h;
        this.data = data; 
    }
}
