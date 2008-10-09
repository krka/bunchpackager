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

package se.kristofer.karlsson.util.bunchpackager.backend;

import java.util.Comparator;

public class HeightComparator implements Comparator {

    public int compare(Object arg0, Object arg1) {
        ImageObject o1 = (ImageObject) arg0; 
        ImageObject o2 = (ImageObject) arg1;
        
        // Sort by large height first
        if (o1.height > o2.height) return -1;
        if (o1.height < o2.height) return 1;

        // And large width next
        if (o1.width > o2.width) return -1;
        if (o1.width < o2.width) return 1;

        return o1.name.compareTo(o2.name);
    }

}
