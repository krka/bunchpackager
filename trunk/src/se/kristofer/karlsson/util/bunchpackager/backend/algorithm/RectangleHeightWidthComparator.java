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
