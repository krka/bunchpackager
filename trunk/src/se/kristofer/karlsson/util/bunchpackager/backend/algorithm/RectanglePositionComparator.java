package se.kristofer.karlsson.util.bunchpackager.backend.algorithm;

import java.util.Comparator;


public class RectanglePositionComparator implements Comparator<Rectangle> {

	public int compare(Rectangle arg0, Rectangle arg1) {
		int d = arg1.getY() - arg0.getY();
		if (d != 0) return d;
		return arg1.getX() - arg0.getX();
	}

}
