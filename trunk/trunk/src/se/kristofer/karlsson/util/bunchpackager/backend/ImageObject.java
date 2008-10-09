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
import java.awt.Image;

public class ImageObject {

    public String name;
    public Image data;
    
    public int width;
    public int height;

    public boolean duplicate;
    public int dup_offset_x; 
    public int dup_offset_y; 
    public ImageObject dup_image; 
    
    public ImageObject(String name, int w, int h, Image data) {
        this.name = name;
        width = w;
        height = h;
        this.data = data; 
    }
}
