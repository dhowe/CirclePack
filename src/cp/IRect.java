package cp;

import java.awt.*;

import processing.core.PImage;

public class IRect extends Rect {

	public PImage image;

	public IRect(Image i) {
		this(new PImage(i));
	}
	
	public IRect(PImage p) {
		this.image = p;
		this.x = Integer.MAX_VALUE;
		this.y = Integer.MAX_VALUE;
		this.width = p.width;
		this.height = p.height;
	}
}
