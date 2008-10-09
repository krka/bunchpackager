package se.kristofer.karlsson.util.bunchpackager.backend;
import java.util.Comparator;

public class IntegerComp implements Comparator {

    public int compare(Object arg0, Object arg1) {
        Integer p1 = (Integer) arg0;
        Integer p2 = (Integer) arg1;
        
        int v1 = p1.intValue();
        int v2 = p2.intValue();

        return v1 - v2;
    }

}
