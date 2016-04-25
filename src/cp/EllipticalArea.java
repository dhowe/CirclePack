package cp;

import java.awt.Rectangle;

import processing.core.PApplet;

public class EllipticalArea extends PApplet {

	Rectangle[] r;

	public void settings() {
		size(1000, 800);
	}

	public void setup() {
		r = new Rectangle[5];
		for (int i = 0; i < r.length; i++) {
			r[i] = new Rectangle();
			r[i].x = (int) random(300, 600);
			r[i].y = (int) random(200, 500);
			r[i].width = (int) random(50, 150);
			r[i].height = (int) random(50, 150);
		}
	}

	public void keyPressed() {
		if (key==' ') setup();
	}

	public void draw() {

		background(255);
		stroke(0);
		fill(0, 16);
		for (int i = 0; i < r.length; i++) {
			rect(r[i].x, r[i].y, r[i].width, r[i].height);
		}

		int cx = width / 2, cy = height / 2;
		float ratio = 2f;
		
		Ellipse be = boundingEllipse(r, cx, cy, ratio);
		Rectangle br = PU.alignedBoundingRect(r, cx, cy);

		noFill();
		stroke(0, 0, 200);
		ellipse(be.x, be.y, be.width, be.height);

		rect(br.x, br.y, br.width, br.height);

		fill(0);
		noStroke();
		ellipse(cx, cy, 5, 5);
	}

	static float SQRT2 = (float) Math.sqrt(2);
	
	static Ellipse boundingEllipse(Rectangle[] r, int cx, int cy, float ratio) {
		Rectangle br = PU.alignedBoundingRect(r, cx, cy);

		// float width' = sqrt(rect.width^2 + ratio^2 * rect.height^2)
		float ew = sqrt( (br.width*br.width) + (ratio*ratio) * (br.height*br.height) );
		float eh = ew / ratio;
		
		return new Ellipse(cx, cy, ew, eh);
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { EllipticalArea.class.getName() });
	}
}
