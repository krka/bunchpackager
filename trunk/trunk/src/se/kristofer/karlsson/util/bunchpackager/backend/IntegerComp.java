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

public class IntegerComp implements Comparator {

    public int compare(Object arg0, Object arg1) {
        Integer p1 = (Integer) arg0;
        Integer p2 = (Integer) arg1;
        
        int v1 = p1.intValue();
        int v2 = p2.intValue();

        return v1 - v2;
    }

}
