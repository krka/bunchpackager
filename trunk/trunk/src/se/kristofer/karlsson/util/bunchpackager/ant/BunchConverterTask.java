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
