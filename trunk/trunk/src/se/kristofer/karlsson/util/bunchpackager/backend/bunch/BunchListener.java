package se.kristofer.karlsson.util.bunchpackager.backend.bunch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.RectanglePlacement;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.RectangleStripPackerListener;

public class BunchListener implements RectangleStripPackerListener {
	private Bunch bunch;
	private File imageFile;
	private File dataFile;

	private PrintWriter printer;
	private boolean needsSave;
	private boolean hasStored;
	public BunchListener(Bunch bunch, PrintWriter printer, File imageFile, File dataFile, boolean needsSave) {
		this.bunch = bunch;
		this.imageFile = imageFile;
		this.dataFile = dataFile;
		this.printer = printer;
		this.needsSave = needsSave;
	}

	public void onNewResult(RectanglePlacement current, int iteration) {
		if (current == null) return;

		needsSave = true;
		printer.printf("New result: %dx%d - %.1f%% larger than optimum - %d wasted pixels - at iteration: %d\n",
				current.getWidth(),
				current.getHeight(),
				100.0 * current.getArea() / current.getPieceAreaSum() - 100,
				current.getArea() - current.getPieceAreaSum(),
				iteration);
		bunch.setPlacement(current);
	}

	public void onStart() {
		RectanglePlacement current = bunch.getPlacement();
		printer.printf("Processing bunch \"%s\": %dx%d - %.1f%% larger than optimum - %d wasted pixels.\n",
				bunch.getName(),
				current.getWidth(),
				current.getHeight(),
				100.0 * current.getArea() / current.getPieceAreaSum() - 100,
				current.getArea() - current.getPieceAreaSum());
				
	}

	public void onStop() {
		RectanglePlacement current = bunch.getPlacement();
		if (needsSave) {
			store(current);
		} else {
			printer.printf("No new result for \"%s\"\n", bunch.getName());
		}
	}

	private void store(RectanglePlacement current) {
		printer.printf("Saving bunch \"%s\": %dx%d - %.1f%% larger than optimum - %d wasted pixels\n",
				bunch.getName(),
				current.getWidth(),
				current.getHeight(),
				100.0 * current.getArea() / current.getPieceAreaSum() - 100,
				current.getArea() - current.getPieceAreaSum());
		try {
			if (imageFile != null && dataFile != null) {
				bunch.store(imageFile, dataFile);
				hasStored = true;
			}
		} catch (IOException e) {
			e.printStackTrace(printer);
		}
	}
	
	public boolean hasStored() {
		return hasStored;
	}
}
