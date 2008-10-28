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

public class Rectangle {
	private String identifier;
	private int width;
	private int height;
	
	private int x;
	private int y;
	
	public Rectangle(String identifier, int width, int height) {
		this.identifier = identifier;
		this.width = width;
		this.height = height;
	}
	
	public Rectangle(String identifier, int width, int height, int x, int y) {
		this.identifier = identifier;
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Rectangle clone() {
		Rectangle rectangle = new Rectangle(identifier, width, height, x, y);
		return rectangle;
	}

	public Rectangle clone(int x, int y) {
		Rectangle rectangle = new Rectangle(identifier, width, height, x, y);
		return rectangle;
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public String toString() {
		return width + "x" + height + "([" + x + "," + y + "]-[" + (x + width - 1) + "," + (y + height - 1) + "])"; 
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + height;
		result = PRIME * result + width;
		result = PRIME * result + x;
		result = PRIME * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Rectangle other = (Rectangle) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
}
