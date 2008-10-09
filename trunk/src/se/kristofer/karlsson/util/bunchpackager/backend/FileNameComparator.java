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

import java.io.File;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameComparator implements Comparator<File> {
    static Pattern p = Pattern.compile("(\\D*)(\\d+)");
    
	public int compare(File f1, File f2) {
        String s1 = f1.getAbsolutePath();
        String s2 = f2.getAbsolutePath();

        Matcher m1 = p.matcher(s1);
        Matcher m2 = p.matcher(s2);
        
        int pos1 = 0;
        int pos2 = 0;
        
        while (m1.find() && m2.find()) {

            String str1 = m1.group(1);
            String str2 = m2.group(1);
            
            int c = str1.compareTo(str2);
            if (c != 0) return c;
                    
            c = new Integer(m1.group(2)).compareTo(new Integer(m2.group(2)));
            if (c != 0) return c;
            
            pos1 = m1.end();
            pos2 = m2.end();
        }

        if (pos1 != 0) s1 = s1.substring(pos1);
        if (pos2 != 0) s2 = s2.substring(pos2);
        
        return s1.compareTo(s2);
	}
}
