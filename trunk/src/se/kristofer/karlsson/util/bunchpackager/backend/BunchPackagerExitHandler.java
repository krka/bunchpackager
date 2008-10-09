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

import java.util.HashSet;
import java.util.Set;

public class BunchPackagerExitHandler extends Thread {

	private static BunchPackagerExitHandler instance;
	private Set<BunchPackager> listeners = new HashSet<BunchPackager>();
	
	private BunchPackagerExitHandler() {
		Runtime.getRuntime().addShutdownHook(this);
	}
	
	public static BunchPackagerExitHandler getInstance() {
		if (instance == null) {
			instance = new BunchPackagerExitHandler();
		}
		return instance;
	}

	public void addListener(BunchPackager packager) {
		listeners.add(packager);
	}

	public void removeListener(BunchPackager packager) {
		listeners.remove(packager);
	}
	
	public void run() {
		for (BunchPackager packager: listeners) {
			packager.stop();
		}
		for (BunchPackager packager: listeners) {
			packager.waitForStop();
		}
	}

}
