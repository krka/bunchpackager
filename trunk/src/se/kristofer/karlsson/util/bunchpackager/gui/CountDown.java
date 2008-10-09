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
