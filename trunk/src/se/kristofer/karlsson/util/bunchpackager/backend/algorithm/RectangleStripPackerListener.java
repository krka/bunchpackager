package se.kristofer.karlsson.util.bunchpackager.backend.algorithm;

public interface RectangleStripPackerListener {

	void onNewResult(RectanglePlacement best, int iteration);

	void onStart();

	void onStop();

}
