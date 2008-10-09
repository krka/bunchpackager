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
