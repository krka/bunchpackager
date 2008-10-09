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

package se.kristofer.karlsson.util.bunchpackager.backend.bunch;


public class SubImage {
	private String parent;
	private int xOffset;
	private int yOffset;
	
	public SubImage(String parent, int offset, int offset2) {
		super();
		this.parent = parent;
		xOffset = offset;
		yOffset = offset2;
	}
	
	public String getParent() {
		return parent;
	}
	public int getXOffset() {
		return xOffset;
	}
	public int getYOffset() {
		return yOffset;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((parent == null) ? 0 : parent.hashCode());
		result = PRIME * result + xOffset;
		result = PRIME * result + yOffset;
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
		final SubImage other = (SubImage) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (xOffset != other.xOffset)
			return false;
		if (yOffset != other.yOffset)
			return false;
		return true;
	}
	
	public String toString() {
		return parent + "(" + xOffset +"," + yOffset + ")";
	}
}
