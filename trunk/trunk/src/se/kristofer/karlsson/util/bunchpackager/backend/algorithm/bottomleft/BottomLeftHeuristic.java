package se.kristofer.karlsson.util.bunchpackager.backend.algorithm.bottomleft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.Rectangle;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.RectangleHeightWidthComparator;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.RectanglePlacement;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.RectangleStripPacker;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.RectangleStripPackerListener;

public class BottomLeftHeuristic implements RectangleStripPacker, Runnable {

	private final static Comparator<Rectangle> comparator = new RectangleHeightWidthComparator();
	
	private RectanglePlacement best;
	private long maxRuntimeMillis;
	protected Integer maxWidth;
	protected Integer maxHeight;
	protected double ratio;

	private Random random;
	
	public BottomLeftHeuristic(long maxRuntimeMillis) {
		this(maxRuntimeMillis, null, null);
	}
	
	public BottomLeftHeuristic(long maxRuntimeMillis, Integer maxWidth, Integer maxHeight) {
		this.maxRuntimeMillis = maxRuntimeMillis;
		this.maxWidth = maxWidth;
		this.maxHeight = maxWidth;
		
		random = new Random();
	}

	
	protected boolean perform(int iteration) {
		boolean gotBetter = false;
		List<Rectangle> workingCopy = cloneAndOrder(rectangles, comparator);

		int width = totalWidth;
		while (width >= minWidth) {
			RectanglePlacement current = generatePlacement(workingCopy, width);
			if (current != null) {
				if ((best == null || current.getArea() < best.getArea()) &&
						(maxHeight == null || current.getHeight() <= maxWidth) &&
						(maxWidth == null || current.getWidth() <= maxWidth)) {
					best = current;
					gotBetter = true;
					if (!best.isValid()) {
						System.out.println("Best is not valid");
					}
				}
				width = current.getWidth() - 1;
			} else {
				break;
			}
		}
		return gotBetter;
	}

	protected RectanglePlacement generatePlacement(List<Rectangle> rectangles, int width) {
		RectanglePlacement current = new RectanglePlacement();

        TreeSet<Integer> tryRows = new TreeSet<Integer>();
        TreeSet<Integer> tryCols = new TreeSet<Integer>();

        tryCols.add(0);
        tryRows.add(0);
        
		for (Rectangle rectangle: rectangles) {
			boolean placed = false;
			for (Integer row: tryRows) {
				for (Integer col: tryCols) {
					if (col + rectangle.getWidth() > width) break;
					
					rectangle = rectangle.clone(col, row);
					if (current.canAdd(rectangle)) {
						current.addRectangle(rectangle);
						tryRows.add(row + rectangle.getHeight());
						tryCols.add(col + rectangle.getWidth());
						placed = true;
						break;
					}
				}
				if (placed) break;
			}
			if (!placed) {
				throw new RuntimeException("Unable to place rectangle anywhere, " + rectangle);
			}
		}
		
		return current;
	}

	private List<Rectangle> cloneAndOrder(Collection<Rectangle> rectangles, Comparator<Rectangle> comparator) {
		List<Rectangle> copy = new ArrayList<Rectangle>(rectangles);
		Collections.sort(copy, comparator);

		int n = copy.size();
		// Not sure what the appropiate number of swaps should be
		int numSwaps = n * 2;
		for (int i = 0; i < numSwaps; i++) {
			int p1 = random.nextInt(n);
			int p2 = random.nextInt(n);
			Collections.swap(copy, p1, p2);
		}
		
		return copy;
	}

	public void setPercentage(double percentage) {
		ratio = 1 + (percentage / 100);
	}
	
	/*
	 * Interface methods
	 */
	private boolean running;

	private Collection<Rectangle> rectangles;

	private int totalWidth;

	private int minWidth;

	private boolean stop;
	
	public void run() {
		notifyStart();

		int iteration = 0;
		long startTime = System.currentTimeMillis();
		while (!stop &&
				System.currentTimeMillis() - startTime < maxRuntimeMillis) {
			
			iteration++;
			boolean gotBetter = perform(iteration);
			if (gotBetter) {
				notifyNewResult(iteration);
			}
			
			// Break if it's already good enough
			if (goodEnough()) {
				break;
			}
		}
		notifyStop();
		running = false;
		synchronized (this) {
			notifyAll();
		}
	}
	
	

	private boolean goodEnough() {
		return best != null && best.getArea() <= best.getPieceAreaSum() * ratio;
	}

	public synchronized void start(RectanglePlacement current) {
		blockingStop();

		best = current;
		if (best != null && !best.isValid()) {
			best = null;
		}

		rectangles = current.getRectangles();
		totalWidth = 0;
		minWidth = 0;
		for (Rectangle rectangle: rectangles) {
			int width = rectangle.getWidth();
			totalWidth += width;
			minWidth = Math.max(minWidth, width);
		}

		if (maxWidth != null) {
			totalWidth = Math.min(maxWidth, totalWidth);
		}
		
		stop = false;
		running = true;
		new Thread(this).start();
	}
	
	public void blockingStop() {
		nonblockingStop();
		while (running) {
			synchronized(this) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	public void nonblockingStop() {
		stop = true;
	}
	
	public boolean isRunning() {
		return running;
		
	}
	
	public RectanglePlacement getPlacement() {
		return best;
	}

	private Collection<RectangleStripPackerListener> listeners = new ArrayList<RectangleStripPackerListener>();
	public void addListener(RectangleStripPackerListener listener) {
		listeners.add(listener);
	}
	
	private void notifyNewResult(int iteration) {
		for (RectangleStripPackerListener listener: listeners) {
			listener.onNewResult(best, iteration);
		}
	}

	private void notifyStart() {
		for (RectangleStripPackerListener listener: listeners) {
			listener.onStart();
		}
	}

	private void notifyStop() {
		for (RectangleStripPackerListener listener: listeners) {
			listener.onStop();
		}
	}
}
