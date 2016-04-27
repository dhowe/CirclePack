package cp;

import java.awt.Rectangle;

import processing.core.PApplet;

public class EllipticalArea extends PApplet {

	Rectangle[] r;

	public void settings() {
		size(1000, 800);
	}

	public void setup() {
		r = new Rectangle[15];
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
		
		Ellipse e1 = PU.boundingEllipse(r, cx, cy, ratio);
		float diam = PU.boundingCircle(r, cx, cy);
		Ellipse e2 = new Ellipse(cx,cy,diam,diam);
		float diam2 = PU.boundingCircleDiameterNew(r, cx, cy);
		Ellipse e3 = new Ellipse(cx,cy,diam2,diam2);
		Rectangle r1 = PU.alignedBoundingRect(r, cx, cy);

		noFill();
		stroke(0, 0, 200);
		
		drawEllipse(e1);
		drawEllipse(e2);
		//drawEllipse(e3);
		rect(r1.x, r1.y, r1.width, r1.height);

		fill(0);
		noStroke();
		ellipse(cx, cy, 5, 5);
	}

	public void drawEllipse(Ellipse e) {
		
		ellipse(e.x, e.y, e.width, e.height);
	}
	
	public void drawRect(Rectangle r) {
		
		rect(r.x, r.y, r.width, r.height);
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { EllipticalArea.class.getName() });
	}
}
