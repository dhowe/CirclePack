package cp;

import java.awt.Rectangle;

public class Ellipse {
	
	public static final float SQRT2 = (float) Math.sqrt(2);

	public int x,y,width,height;

	public Ellipse() {
		this(0,0,0,0);
	}

	public Ellipse(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
	}
	
	public Ellipse(Rectangle r) {

    this.width = Math.round(r.width * SQRT2);
    this.height = Math.round(r.height * SQRT2);
    this.x = Math.round(r.x + r.width/2f);
    this.y = Math.round(r.y + r.width/2f);
	}

	public Ellipse(float x, float y, float width, float height) {
		
    this.x = Math.round(x);
    this.y = Math.round(y);
    this.width = Math.round(width);
    this.height = Math.round(height);
	}
  
	public float containsVal(float X, float Y) {
		float dx = X - x, rx = width/2f;
		float dy = Y - y, ry = height/2f;
		float left =  (dx*dx) / (rx*rx);
		float right = (dy*dy) / (ry*ry);
		return left+right;
	}

	public float area() {
		return (float) (Math.PI * width/2d * height/2d);
	}
	
	public boolean contains(float X, float Y) {
		if (width <= 0.0 || height <= 0.0) return false;
		float dx = X - x, rx = width/2f;
		float dy = Y - y, ry = height/2f;
		float left =  (dx*dx) / (rx*rx);
		float right = (dy*dy) / (ry*ry);
		return left + right <= 1;
	}

	public boolean contains(float X, float Y, float d) {
		if (width <= 0.0 || height <= 0.0) return false;
		float dx = X - x, rx = width/2f;
		float dy = Y - y, ry = height/2f;
		float left =  (dx*dx) / (rx*rx);
		float right = (dy*dy) / (ry*ry);
		return left + right <= d;
	}

}
