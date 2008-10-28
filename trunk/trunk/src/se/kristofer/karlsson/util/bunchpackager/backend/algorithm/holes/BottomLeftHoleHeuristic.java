package se.kristofer.karlsson.util.bunchpackager.backend.algorithm.holes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.Rectangle;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.RectanglePlacement;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.RectanglePositionComparator;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.bottomleft.BottomLeftHeuristic;

public class BottomLeftHoleHeuristic extends BottomLeftHeuristic {

	private static int max;

	public BottomLeftHoleHeuristic(long maxRuntimeMillis) {
		super(maxRuntimeMillis);
	}

	public BottomLeftHoleHeuristic(long maxRuntimeMillis, Integer maxWidth, Integer maxHeight) {
		super(maxRuntimeMillis, maxWidth, maxHeight);
	}
	
	protected RectanglePlacement generatePlacement(List<Rectangle> rectangles, int width) {
		RectanglePlacement placement = new RectanglePlacement();

		TreeSet<Rectangle> holes = new TreeSet<Rectangle>(new RectanglePositionComparator());
		List<Rectangle> newHoles = new ArrayList<Rectangle>();
		holes.add(new Rectangle(null, width, Integer.MAX_VALUE, 0, 0));
		
		int rectangledPlaced = 0;
		for (Rectangle rectangle: rectangles) {
			rectangledPlaced++;
			//System.out.println("Working on: " + rectangle);
			Rectangle usedHole = null;
			
			for (Rectangle hole: holes) {
				if (rectangle.getWidth() <= hole.getWidth() && rectangle.getHeight() <= hole.getHeight()) {
					usedHole = hole;
					break;
				}
			}
			if (usedHole == null) {
				throw new RuntimeException("Could not find a valid hole");
			}
			Rectangle clone = rectangle.clone(usedHole.getX(), usedHole.getY());
			// place in hole
			placement.addRectangle(clone);
			System.out.println("Placing clone: " + clone);
			
			// remove and recreate all overlapping holes
			for (Iterator<Rectangle> it = holes.iterator(); it.hasNext();) {
				Rectangle hole = it.next();
				if (RectanglePlacement.overlaps(hole, clone)) {
					//System.out.println("Found overlapping hole!");
					it.remove();
				
					int diff;
					
					// add hole above
					diff = clone.getY() - hole.getY();
					if (diff > 0) {
						Rectangle newHole = new Rectangle(null, hole.getWidth(), diff, hole.getX(), hole.getY());
						newHoles.add(newHole);
						//System.out.println("adding above " + newHole);
					}
					
					// add hole below
					diff =  (hole.getY() + hole.getHeight()) - (clone.getY() + clone.getHeight());
					if (diff > 0) {
						Rectangle newHole = new Rectangle(null, hole.getWidth(), diff, hole.getX(), clone.getY() + clone.getHeight());
						newHoles.add(newHole);
						//System.out.println("adding below " + newHole);
					}
					
					// add hole to the left
					diff = clone.getX() - hole.getX();
					if (diff > 0) {
						Rectangle newHole = new Rectangle(null, diff, hole.getHeight(), hole.getX(), hole.getY());
						newHoles.add(newHole);
						//System.out.println("adding left " + newHole);
					}
					
					// add hole to the right
					diff = (hole.getX() + hole.getWidth()) - (clone.getX() + clone.getWidth());
					if (diff > 0) {
						Rectangle newHole = new Rectangle(null, diff, hole.getHeight(), clone.getX() + clone.getWidth(), hole.getY());
						newHoles.add(newHole);
						//System.out.println("adding right " + newHole);
					}
				}
			}
			holes.addAll(newHoles);
			newHoles.clear();
			int oldMax = max;
			max = Math.max(max, holes.size());
			if (oldMax < max) {
				System.out.println("size: " + max + ", " + rectangledPlaced);
			}
		}
		System.out.println(placement.getWidth() + "x" + placement.getHeight());
		return placement;
	}
}
