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
