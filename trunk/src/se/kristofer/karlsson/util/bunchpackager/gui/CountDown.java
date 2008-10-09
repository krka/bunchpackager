package se.kristofer.karlsson.util.bunchpackager.gui;
import javax.swing.JProgressBar;

public class CountDown implements Runnable {
    
    private long startTime;
    private int runtime;
    
    private JProgressBar progressbar;
    private boolean keepRunning;
    
    public CountDown(JProgressBar progressbar) {
        this.progressbar = progressbar;
        
        progressbar.setMinimum(0);
    }

    public void setRuntime(int runtime) {
    	stop();
    	this.runtime = runtime;
        progressbar.setMaximum(10 * runtime);
        update();
    }

	private void update() {
		if (startTime == 0) {
			progressbar.setVisible(false);
	        progressbar.setString("");
	        progressbar.setValue(10 * runtime);
		} else {
			progressbar.setVisible(true);
			long timeSpent = System.currentTimeMillis() - startTime;
			int deciSecondsLeft = (int) (10 * runtime - (timeSpent / 100));
			deciSecondsLeft = Math.max(deciSecondsLeft, 0);
			progressbar.setValue(10 * runtime - deciSecondsLeft);
			progressbar.setString(timeString(deciSecondsLeft / 10));
			progressbar.repaint();
		}
	}

	public void start() {
    	startTime = System.currentTimeMillis();
    	update();
    	keepRunning = true;
    	new Thread(this).start();
	}
    
    public void stop() {
    	startTime = 0;
    	keepRunning = false;
    	update();
    }
        
    public void run() {
        while (keepRunning) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            update();
        }
    }
    
    public static String timeString(long s) {
        int seconds = (int) (s % 60);
        int minutes = (int) ((s / 60) % 60);
        int hours = (int) (s / 3600);
        
        return String.format("%1$02d:%2$02d:%3$02d",
                new Object[] {
            		Integer.valueOf(hours),
            		Integer.valueOf(minutes),
            		Integer.valueOf(seconds),
                }
        );
        
    }
}
