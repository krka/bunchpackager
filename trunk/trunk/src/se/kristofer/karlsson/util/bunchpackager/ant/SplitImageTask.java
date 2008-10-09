package se.kristofer.karlsson.util.bunchpackager.ant;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class SplitImageTask extends Task {
	private Vector<FileSet> filesets = new Vector<FileSet>();
	private File outputDir;
	private int width;
	private int height;
	private int amountX;
	private int amountY;
	private String namingStyle;

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setAmountX(int amountX) {
		this.amountX = amountX;
	}

	public void setAmountY(int amountY) {
		this.amountY = amountY;
	}
	
	public void setOutputDir(File dir) {
		outputDir = dir;
	}

	public void setNamingStyle(String namingStyle) {
		this.namingStyle = namingStyle;
	}

	public void addFileset(FileSet fs) {
		filesets.add(fs);
	}
	
	public void execute() throws BuildException {
		if (namingStyle == null) {
			namingStyle = "grid";
		}

		if (namingStyle.equals("grid")) {
			
		} else if (namingStyle.equals("indexed")) {
			
		} else {
			throw new BuildException("Bad namingStyle: Valid styles are 'grid' and 'indexed'");
		}
		
		if (width <= 0 && amountX <= 0) {
			throw new BuildException("Requires a positive width or amountX");
		}
		if (height <= 0 && amountY <= 0) {
			throw new BuildException("Requires a positive height or amountY");
		}
		if (outputDir == null || !outputDir.isDirectory()) {
			throw new BuildException("Output dir must be a directory");
		}			
        try {
			for (FileSet fs : filesets) {
				File baseDir = fs.getDir(getProject());
				System.out.println("base: " + baseDir);
				
				DirectoryScanner ds = fs.getDirectoryScanner(getProject());
	            String[] includedFiles = ds.getIncludedFiles();
	            for (int i = 0; i < includedFiles.length; i++) {
	                String filename = includedFiles[i].replace('\\','/');
	                File sourceFile = new File(baseDir, filename);
	                File destFile = new File(outputDir, filename);
	                File destDir = destFile;

	                System.out.println("source: " + sourceFile);
	                System.out.println("dest: " + destFile);

	                destDir.mkdirs();

	                Image im = getImage(sourceFile);
	                int imageWidth = im.getWidth(null);
	                int imageHeight = im.getHeight(null);

	                int pieceWidth = width, pieceHeight = height;
	                if (width <= 0) {
	                	pieceWidth = (imageWidth + amountX - 1) / amountX;
	                }
	                if (height <= 0) {
	                	pieceHeight = (imageHeight + amountY - 1) / amountY;
	                }
	                if (pieceHeight <= 0 || pieceWidth <= 0) {
	                	throw new BuildException("Bad image with dimensions (" + imageWidth + "x" + imageHeight + "): " + sourceFile.getAbsolutePath());
	                }
	                int index = 0;
	                int row = 0;
	                for (int offsetY = 0; offsetY < imageHeight; offsetY += pieceHeight) {
		                int col = 0;
		                for (int offsetX = 0; offsetX < imageWidth; offsetX += pieceWidth) {
		                	BufferedImage outputImage = new BufferedImage(pieceWidth, pieceHeight, BufferedImage.TYPE_4BYTE_ABGR);
		                	Graphics g = outputImage.getGraphics();
		                	g.drawImage(im, -offsetX, -offsetY, null);
		                	
		                	String outputFileName;
		                	if (namingStyle.equals("grid")) {
		                		outputFileName = String.format("%04d", row) + "-" + String.format("%04d", col) + ".png";
		                	} else if (namingStyle.equals("indexed")) {
		                		outputFileName = String.format("%04d", index) + ".png";		                		
		                	} else {
		            			throw new BuildException("Bad namingStyle: Valid styles are 'grid' and 'indexed'");
		                	}
		                	File outputImageFile = new File(destDir, outputFileName);
		                	ImageIO.write(outputImage, "png", outputImageFile);

		                	col++;
		                	index++;
		                }
	                	row++;                	
	                }

	            }
			}
		} catch (IOException e) {
			throw new BuildException(e.getMessage());
		}
	}
	
    // utility methods
    private static Frame f = new Frame();
    private static Toolkit tk = Toolkit.getDefaultToolkit();
    
    public static Image getImage(File file) {
        return getImage(file.getAbsolutePath());
    }
    
    public static Image getImage(String filename) throws BuildException {
        MediaTracker mediaTracker = new MediaTracker(f);
        Image image = tk.createImage(filename);

        mediaTracker.addImage(image, 0);
        try {
             mediaTracker.waitForID(0);
        } catch (InterruptedException ie) {
        	throw new BuildException(ie.getMessage());
        }
        return image;
    }

}
