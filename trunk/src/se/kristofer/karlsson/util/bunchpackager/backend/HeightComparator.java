package se.kristofer.karlsson.util.bunchpackager.backend;

import java.util.Comparator;

public class HeightComparator implements Comparator {

    public int compare(Object arg0, Object arg1) {
        ImageObject o1 = (ImageObject) arg0; 
        ImageObject o2 = (ImageObject) arg1;
        
        // Sort by large height first
        if (o1.height > o2.height) return -1;
        if (o1.height < o2.height) return 1;

        // And large width next
        if (o1.width > o2.width) return -1;
        if (o1.width < o2.width) return 1;

        return o1.name.compareTo(o2.name);
    }

}
