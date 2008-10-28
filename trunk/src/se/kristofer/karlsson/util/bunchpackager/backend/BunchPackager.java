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

/**
 * Merges a collection of images onto a larger image, in such a way that the total
 * area is minimized. This problem is called 2d bin packing and is NP-complete.
 * This program solves it using an approximation described in this
 * article: http://www.merl.com/reports/docs/TR2003-05.pdf
 * 
 * Don't try to use the bruteforce-method. It's only there for testing,
 * and it's painfully slow for even quite small sets since it tries all permutations.
 * 
 * My tests indicate that this solution in many (small) cases
 * finds the optimal solution.
 * 
 * Note that the resulting image should be processed manually
 * for reduction of colors.
 * 
 * The implementation is the simplest possible with bad complexity.
 * However, for a small number of images, (< 50) it is not a problem.
 *  
 * @author Kristofer Karlsson
 */
package se.kristofer.karlsson.util.bunchpackager.backend;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.bottomleft.BottomLeftHeuristic;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.holes.BottomLeftHoleHeuristic;
import se.kristofer.karlsson.util.bunchpackager.backend.bunch.Bunch;
import se.kristofer.karlsson.util.bunchpackager.backend.bunch.BunchListener;

public class BunchPackager {

    // Time to run in seconds
    public static final long DEFAULT_RUNTIME = 5;
    
    public long runtime = DEFAULT_RUNTIME; 
        
    private PrintWriter printer = new PrintWriter(System.out, true);

	public BunchPackager() {
	}
   
    private File inputDir;
    private File outputDir;

    private Integer maxWidth;
    private Integer maxHeight;

    private String outputPath;

    private boolean forcedUpdate = false;

	private boolean running;

	private boolean stop;

	private double percentage;

    public void setPrinter(PrintWriter p) {
        printer = p;
    }
    
    public void setInputdir(File f) {
        inputDir = f;
    }
    
    public void setOutputdir(File f) {
        outputDir = f;

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        outputPath = outputDir.getAbsoluteFile() + File.separator;
    }
    
    public void setMaxwidth(Integer w) {
        maxWidth = w;
    }
    
    public void setMaxheight(Integer h) {
        maxHeight = h;
    }

    public void setRuntime(long seconds) {
        runtime = seconds;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setForcedupdate(boolean b) {
        forcedUpdate = b;
    }

    public void execute() {
        printer.println("Scanning for image directories...");
		
    	running = true;
        try {
			findBunchesRecursive(inputDir);
		} catch (IOException e) {
			e.printStackTrace(printer);
		}

    	printer.println("Done!");
    	running = false;
    	synchronized (this) {
    		notifyAll();
    	}
    }
    
    public void findBunchesRecursive(File dir) throws IOException {
        if (dir.getName().startsWith(".")) return;
        
        makeBunch(dir);
        
        File[] children = dir.listFiles();
        for (int i = 0; i < children.length; i++) {
        	if (stop) return;
            File d = children[i];
            if (d.isDirectory()) {
                findBunchesRecursive(d);
            }
        }
    }
    
    public void makeBunch(File bunchDir) throws IOException {
    	if (stop) return;
    	
        String bunchName = bunchDir.getName();
        String baseDir = bunchDir.getAbsolutePath();

        File textFile = new File(outputPath + bunchName + ".txt");
        File imageFile = new File(outputPath + bunchName + ".png");

        Bunch bunch = Bunch.makeBunch(bunchName, bunchDir, imageFile, textFile);
        if (bunch == null) {
        	return;
        }

        if (bunch.isPreExisting()) {
        	printer.println("Found existing bunch: " + bunchName + " in " + baseDir);
            if (bunch.isUpToDate()) {
            	printer.println("bunch is up to date.");
            } else {
            	printer.println("bunch is not up to date.");
            }
        } else {
        	printer.println("Created new bunch: " + bunchName + " from " + baseDir);
        }
        
        if (!bunch.isUpToDate() || forcedUpdate) {
        	BunchListener bunchListener = new BunchListener(bunch, printer, imageFile, textFile, !bunch.isUpToDate());
        	
        	BottomLeftHeuristic algorithm = new BottomLeftHeuristic(1000 * runtime, maxWidth, maxHeight);
        	algorithm.setPercentage(percentage);
        	algorithm.addListener(bunchListener);
        	algorithm.start(bunch.getPlacement());
        	while (true) {
        		if (stop) {
        			algorithm.blockingStop();
        		}
        		if (!algorithm.isRunning()) break;
        		try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
        	}
        } else {
        	printer.println("Skipping bunch");
        }
    }

    public void stop() {
    	stop = true;
    }
    
    public void waitForStop() {
    	stop();
    	while (running) {
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				throw new Error(e1);
			}
    	}
    }
    
    public static void main(String[] args) {
        if (args.length < 2) { 
            printHelp();
            System.exit(1);
        }
        BunchPackager packager = new BunchPackager();
        packager.setInputdir(new File(args[0]));
        packager.setOutputdir(new File(args[1]));
        
        if (args.length >= 3) {
            packager.setRuntime(Integer.parseInt(args[2]));
        }
        if (args.length >= 4) {
            packager.setForcedupdate(Boolean.parseBoolean(args[3]));
        }
        if (args.length >= 5) {
            packager.setMaxwidth(Integer.parseInt(args[4]));
        }
        if (args.length >= 6) {
            packager.setMaxheight(Integer.parseInt(args[5]));
        }
        if (args.length >= 7) {
            packager.setPercentage(Double.parseDouble(args[6]));
        }
        packager.execute();
    }

	public static void printHelp() {
		System.out.println("For GUI: java -jar bunchpackager.jar");
		System.out.println("For Commandline: java -jar bunchpackager.jar <BASE DIR> <OUT DIR> [RUNTIME] [FORCE UPDATE] [MAX WIDTH] [MAX HEIGHT] [GOOD ENOUGH]");
		System.out.println("BASE DIR: a directory containing directories of images.");
		System.out.println("OUT DIR: a directory where the results are placed");
		System.out.println("RUNTIME: number of seconds the program runs");
		System.out.println("FORCE UPDATE: true if the program should try to improve the current result, false if not.");
		System.out.println("GOOD ENOUGH: percentage of result acceptance. a value of \"5\" means that the algorithm stops when the output is 5% larger than the input.");
	}
}
