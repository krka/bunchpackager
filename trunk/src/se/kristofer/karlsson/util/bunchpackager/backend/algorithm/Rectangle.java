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
		return x + "," + y + "-" + (x + width - 1) + "," + (y - height - 1); 
	}
}
