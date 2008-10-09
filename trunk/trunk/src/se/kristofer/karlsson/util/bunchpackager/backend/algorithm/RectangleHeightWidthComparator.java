package se.kristofer.karlsson.util.bunchpackager.backend.algorithm;

import java.util.Comparator;

public class RectangleHeightWidthComparator implements Comparator<Rectangle> {

	// Order first by largest height, second by largest width
	public int compare(Rectangle arg0, Rectangle arg1) {
		int d = arg1.getHeight() - arg0.getHeight();
		if (d != 0) return d;
		return arg1.getWidth() - arg0.getWidth();
	}

}
