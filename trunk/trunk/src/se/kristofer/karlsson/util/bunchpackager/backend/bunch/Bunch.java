package se.kristofer.karlsson.util.bunchpackager.backend.bunch;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.Rectangle;
import se.kristofer.karlsson.util.bunchpackager.backend.algorithm.RectanglePlacement;

public class Bunch {

	private static final String n = "(\\d+)";
	private static final String w0 = "[ \t\n]*";
	private static final String w1 = "[ \t\n]+";
	private static final Pattern dataPattern = Pattern.compile("^" +
			w0 + n + w1 + n + // left, top
			w1 + n + w1 + n + // width, height
			w1 + n + w1 + n + // margin-left, margin-top
			w1 + n + w1 + n + // margin-right, margin-bottom
			w1 + "(.*)$" // bunch name
			);
	private static final Pattern oldDataPattern = Pattern.compile("^" +
			"(.*)" + // bunch name
			w1 + n + w1 + n + // left, top
			w1 + n + w1 + n + // width, height
			w0 + "$"
			); 
	
	private RectanglePlacement placement;
	private Map<String, Margins> imageMargins = new TreeMap<String, Margins>();
	private Map<String, BufferedImage> images = new TreeMap<String, BufferedImage>();
	private Map<String, SubImage> subImages = new TreeMap<String, SubImage>();
	
	private long timestamp;
	private String bunchName;
	private boolean upToDate;
	private boolean preExisting;
	
	private Bunch(String bunchName) {
		this.bunchName = bunchName;
	}
	
	public static Bunch makeBunch(String bunchName, File directory, File imageFile, File textFile) throws IOException {
		Bunch bunch = new Bunch(bunchName);
		File[] files = directory.listFiles();

		Map<String, Rectangle> rectMap = new HashMap<String, Rectangle>();
		
		int totalWidth = 0;
		for (File file: files) {
			Image image = getImage(file);
			if (image != null) {
				int width = image.getWidth(null);
				int height = image.getHeight(null);

				if (width > 0 && height > 0) {
					bunch.timestamp = Math.max(bunch.timestamp, file.lastModified());

					String name = file.getName();

					// Calculate margins
					BufferedImage bufferedImage = getBufferedClone(image);
					int marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;

					while (marginRight < width) {
						boolean done = false;
						for (int top = 0; top < height; top++) {
							int color = bufferedImage.getRGB(width - marginRight - 1, top);
							if (!isTransparent(color)) {
								done = true;
								break;
							}
						}
						if (done) break;
						marginRight++;
					}
					width -= marginRight;

					while (marginLeft < width) {
						boolean done = false;
						for (int top = 0; top < height; top++) {
							int color = bufferedImage.getRGB(marginLeft, top);
							if (!isTransparent(color)) {
								done = true;
								break;
							}
						}
						if (done) break;
						marginLeft++;
					}
					width -= marginLeft;

					while (marginBottom < height) {
						boolean done = false;
						for (int left = 0; left < width; left++) {
							int color = bufferedImage.getRGB(marginLeft + left, height - marginBottom - 1);
							if (!isTransparent(color)) {
								done = true;
								break;
							}
						}
						if (done) break;
						marginBottom++;
					}
					height -= marginBottom;

					while (marginTop < height) {
						boolean done = false;
						for (int left = 0; left < width; left++) {
							int color = bufferedImage.getRGB(marginLeft + left, marginTop);
							if (!isTransparent(color)) {
								done = true;
								break;
							}
						}
						if (done) break;
						marginTop++;
					}
					height -= marginTop;

	        		BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
	        		Graphics g = im.getGraphics();
	        		g.drawImage(image, -marginLeft, -marginTop, null);
					bunch.images.put(name, im);

					
					bunch.imageMargins.put(name, new Margins(marginLeft, marginTop, marginRight, marginBottom));
					
					Rectangle rectangle = new Rectangle(name, width, height, totalWidth, 0);
					totalWidth += width;
					rectMap.put(name, rectangle);
				}
			}
		}

		if (bunch.images.size() == 0) {
			return null;
		}

		bunch.findSubImages();
		bunch.placement = bunch.getPlacement(bunch.checkExistingPlacement(bunchName, imageFile, textFile));
		bunch.preExisting = bunch.placement != null;
		
		if (bunch.placement == null) {
			bunch.placement = bunch.getPlacement(rectMap);
		}
		if (bunch.placement == null) {
			return null;
		}
		return bunch;
	}
	
	private RectanglePlacement getPlacement(Map<String, Rectangle> rectMap) {
		if (rectMap == null) {
			return null;
		}
        RectanglePlacement placement = new RectanglePlacement();
		for (Entry<String, BufferedImage> entry: images.entrySet()) {
			String name = entry.getKey();
			if (!subImages.containsKey(name)) {
				Rectangle rectangle = rectMap.get(name);
				if (rectangle == null) {
					return null;
				}
        		if (placement.canAdd(rectangle)) {
        			placement.addRectangle(rectangle);
        		} else {
        			return null;
        		}
			}
		}
		return placement;
	}

	private Map<String, Rectangle> checkExistingPlacement(String bunchName, File imageFile, File dataFile) throws IOException {
		if (!imageFile.exists() || !dataFile.exists()) {
			return null;
		}

		long outputTimestamp = Math.max(imageFile.lastModified(), dataFile.lastModified());
		
		Image sourceImage = getImage(imageFile);
        
        if (sourceImage == null) {
        	return null;
        }
        
        BufferedReader reader = new BufferedReader(new FileReader(dataFile));
        String line;

		Map<String, Rectangle> rectMap = new HashMap<String, Rectangle>();
		        
        while ((line = reader.readLine()) != null) {
        	Matcher m = dataPattern.matcher(line);
        	
        	int left = 0, top = 0, width = 0, height = 0;
        	int marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;
        	String name = null;
        	if (m.matches()) {
        		left = Integer.parseInt(m.group(1));
        		top = Integer.parseInt(m.group(2));
        		width = Integer.parseInt(m.group(3));
        		height = Integer.parseInt(m.group(4));
        		marginLeft = Integer.parseInt(m.group(5));
        		marginTop = Integer.parseInt(m.group(6));
        		marginRight = Integer.parseInt(m.group(7));
        		marginBottom = Integer.parseInt(m.group(8));
        		name = m.group(9);
        	} else {
        		m = oldDataPattern.matcher(line);
        		if (m.matches()) {
            		name = m.group(1);
            		left = Integer.parseInt(m.group(2));
            		top = Integer.parseInt(m.group(3));
            		width = Integer.parseInt(m.group(4));
            		height = Integer.parseInt(m.group(5));
        		}
        	}
        	if (name != null) {
        		Margins margins = imageMargins.get(name);
        		if (margins == null) {
        			return null;
        		}
        		
        		Margins newMargins = new Margins(marginLeft, marginTop, marginRight, marginBottom);
        		if (!margins.equals(newMargins)) {
        			return null;
        		}
        		
        		BufferedImage image = images.get(name);
        		if (image.getWidth() != width || image.getHeight() != height) {
        			return null;
        		}
        		
        		Rectangle rectangle = new Rectangle(name, width, height, left, top);
        		rectMap.put(name, rectangle);
        	}
        }
        
        for (Entry<String, BufferedImage> entry: images.entrySet()) {
        	String name = entry.getKey();
        	Rectangle rectangle = rectMap.get(name);
			if (rectangle == null) {
        		return null;
        	}
        	BufferedImage image = entry.getValue();
        	if (image.getWidth() != rectangle.getWidth() || image.getHeight() != rectangle.getHeight()) {
        		return null;
        	}
        }
		upToDate = outputTimestamp > timestamp;
		return rectMap;
	}


	
	private static boolean isTransparent(int color) {
		return ((color >> 24) & 0xFF) == 0;
	}
	
	public RectanglePlacement getPlacement() {
		return placement;
	}

	public void setPlacement(RectanglePlacement placement) {
		this.placement = placement;
	}
	
	public void store(File imageFile, File dataFile) throws IOException {
		PrintWriter pw = null;
		try {
			int width = placement.getWidth();
			int height = placement.getHeight();
			
			BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = outputImage.getGraphics();

			pw = new PrintWriter(dataFile);
			
			Map<String, Rectangle> rectMap = new HashMap<String, Rectangle>();
			for (Rectangle rectangle: placement.getRectangles()) {
				rectMap.put(rectangle.getIdentifier(), rectangle);
			}
			
			for (Entry<String, BufferedImage> entry: images.entrySet()) {
				String name = entry.getKey();
				BufferedImage image = entry.getValue();
				int left = 0, top = 0, w = image.getWidth(), h = image.getHeight();
				{

					String parent = name;
					while (true) {
						SubImage subImage = subImages.get(parent);
						if (subImage == null) {
							break;
						}
						parent = subImage.getParent();
						left += subImage.getXOffset();
						top += subImage.getYOffset();
					}
					Rectangle rectangle = rectMap.get(parent);
					if (rectangle == null) {
						throw new RuntimeException("No rectangle found for " + parent);
					}
					left += rectangle.getX();
					top += rectangle.getY();
				}

			    if (left + w > width) {
			    	throw new RuntimeException("Rectangle placed outside of image");
			    }
			    if (top + h > height) {
			    	throw new RuntimeException("Rectangle placed outside of image");
			    }

			    
			    // For debugging only
			    /*
			    g.setColor(Color.black);
			    g.drawRect(left, top, w - 1, h - 1);
			    g.drawString(name, left + 2, top + 12);
			    */
			    
			    if (image == null) {
		        	throw new RuntimeException("No image found for rectangle: " + name);
			    }
				g.drawImage(image, left, top, null);

			    printBunch(pw, name, left, top, w, h);
			}
			ImageIO.write(outputImage, "png", imageFile);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private void printBunch(PrintWriter pw, String name, int left, int top, int w, int h) {
		Margins margins = imageMargins.get(name);
		pw.printf("%d %d %d %d %d %d %d %d %s\n",
				left, top, w, h,
				margins.getLeft(), margins.getTop(), margins.getRight(), margins.getBottom(),
				name
				);
	}
	
    public static Image getImage(File file) {
    	try {
			return ImageIO.read(file);
		} catch (IOException e) {
			return null;
		}
    }

    public static BufferedImage getBufferedClone(Image image) {
        int width = image.getWidth(null);
		int height = image.getHeight(null);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		bufferedImage.getWidth(null);
		bufferedImage.getHeight(null);
		
        Graphics g = bufferedImage.getGraphics();
        
        g.drawImage(image, 0, 0, null);
        return bufferedImage;
    }
    
	public long getLastModified() {
		return timestamp;
	}

	public String getName() {
		return bunchName; 
	}
	
	private void findSubImages() {
		Map<String, int[]> imageDataMap = new HashMap<String, int[]>();

		// fill upp dynamic buffer
		Map<String, int[]> dynBuffer = new HashMap<String, int[]>();
		for (Entry<String, BufferedImage> entry: images.entrySet()) {
			String name = entry.getKey();
			BufferedImage image = entry.getValue();
			int w = image.getWidth();
			int h = image.getHeight();
			
	        int[] imageData = image.getRGB(0, 0, w, h, null, 0, w);
	        int[] buf = new int[w*h];

	        for (int x = 1; x < w; x++) {
	        	int col = getPixel(imageData, x, 0, w) + getPixel(buf, x - 1, 0, w);
	        	setPixel(buf, x, 0, w, col);
	        }
	        for (int y = 1; y < h; y++) {
	        	int col = getPixel(imageData, 0, y, w) + getPixel(buf, 0, y - 1, w);
	        	setPixel(buf, 0, y, w, col);
	        }
			for (int x = 1; x < w; x++) {
				for (int y = 1; y < h; y++) {
					int col = getPixel(imageData, x, y, w) + getPixel(buf, x - 1, y, w) + getPixel(buf, x, y - 1, w);
					setPixel(buf, x, y, w, col);
				}
			}
			
			/*
	        for (int i = 0; i < Math.min(w, h); i++) {
	        	System.out.println(name + ": " + i + " = " + getBuf(buf, i, i, w));
	        }
	        */
	        imageDataMap.put(name, imageData);
			dynBuffer.put(name, buf);
		}
		
		subImages.clear();
		for (Entry<String, BufferedImage> entry1: images.entrySet()) {
			String child = entry1.getKey();
			BufferedImage childImage = entry1.getValue();

			int[] childBuf = dynBuffer.get(child);
			int[] childImageData = imageDataMap.get(child);
			
			for (Entry<String, BufferedImage> entry2: images.entrySet()) {
				String parent = entry2.getKey();

				// identity works fine here, since both strings come from the same source
				if (child != parent) {
					BufferedImage parentImage = entry2.getValue();
					
					int[] parentBuf = dynBuffer.get(parent);
					int[] parentImageData = imageDataMap.get(parent);
					
					Point p = getSubImagePosition(
							child, childBuf, childImage.getWidth(), childImage.getHeight(), childImageData,
							parent, parentBuf, parentImage.getWidth(), parentImage.getHeight(), parentImageData);
					if (p != null) {
						int x = (int) p.getX();
						int y = (int) p.getY();
						
						// Ensure no nested parents
						SubImage parentParent = subImages.get(parent);
						
						if (parentParent != null) {
							// identity works fine here, since both strings come from the same source
							if (parentParent.getParent() == child) {
								// Abort! We can't be our own parent
								break;
							}

							x += parentParent.getXOffset();
							y += parentParent.getYOffset();
							parent = parentParent.getParent();
						}
						subImages.put(child, new SubImage(parent, x, y));
						//System.out.println(child + " is inside " + parent + " at " + x + ", " + y);
						break;
					}
				}
			}
		}
	}
	
	private int bufArea(int[] buf, int x1, int y1, int x2, int y2, int width, int height) {
		int a = getPixel(buf, x2, y2, width);
		int b = getPixel(buf, x1, y2, width);
		int c = getPixel(buf, x2, y1, width);
		int d = getPixel(buf, x1, y1, width);
		
		return a - b - c + d;
	}
	
    private Point getSubImagePosition(
    		String child, int[] childBuf, int childW, int childH, int[] childRGB,
    		String parent, int[] parentBuf, int parentW, int parentH, int[] parentRGB) {
    	if (childW > parentW) {
    		return null;
    	}
    	if (childH > parentH) {
    		return null;
    	}
    	
    	int childTotal = bufArea(childBuf, -1, -1, childW - 1, childH - 1, childW, childH);
    	//System.out.println("child: " + child + " = " + childTotal);

        int heightDiff = parentH - childH;
    	int widthDiff = parentW - childW;
		for (int y = 0; y <= heightDiff; y++) {
			for (int x = 0; x <= widthDiff; x++) {
				int parentArea = bufArea(parentBuf, x - 1, y - 1, x + childW - 1, y + childH - 1, parentW, parentH);
				if (parentArea == childTotal) {
					// This could be a false hit, verify with proper image compare
					boolean match = true;
					for (int y2 = 0; match && y2 < childH; y2++) {
						for (int x2 = 0; x2 < childW; x2++) {
							if (childRGB[x2 + y2*childW] != parentRGB[x + x2 + (y + y2)*parentW]) {
								match = false;
								break;
							}
						}
					}
	                if (match) {
						return new Point(x, y);
					}
				}
			}
		}
    	return null;
	}


	private void setPixel(int[] buf, int x, int y, int w, int argb) {
    	buf[x + y * w] = argb;
	}

	private int getPixel(int[] buf, int x, int y, int w) {
		if (x < 0 || y < 0) return 0;
		return buf[x + y * w];
	}

	public boolean isPreExisting() {
		return preExisting;
	}

	public boolean isUpToDate() {
		return upToDate;
	}
}
