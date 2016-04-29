package circle;

import java.awt.*;

import processing.core.PImage;

public class CIRect extends Rectangle {

	public PImage image;
	public String name;

	public CIRect(Image i, String name) {
		this(new PImage(i), name);
	}
	
	public CIRect(PImage p, String name) {
		this.image = p;
		this.name = name;
		this.x = Integer.MAX_VALUE;
		this.y = Integer.MAX_VALUE;
		this.width = p.width;
		this.height = p.height;
	}
}
