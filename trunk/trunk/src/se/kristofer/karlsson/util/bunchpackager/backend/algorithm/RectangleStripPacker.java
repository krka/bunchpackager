package se.kristofer.karlsson.util.bunchpackager.backend.algorithm;



public interface RectangleStripPacker {
	void start(RectanglePlacement current);
	void nonblockingStop();
	void blockingStop();
	
	boolean isRunning();
	
	RectanglePlacement getPlacement();
	
	void addListener(RectangleStripPackerListener listener);
}
