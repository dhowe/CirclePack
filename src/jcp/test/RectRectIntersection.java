package jcp.test;

import jcp.util.P5;
import jcp.util.Rect;
import processing.core.PApplet;

public class RectRectIntersection extends PApplet {

	Rect r1, r2, r3;

	public void setup() {
		r1 = new Rect(250, 250, 300, 100);
		r2 = new Rect(350, 200, 400, 300);
	}

	public void draw() {

		background(255);
		noFill();
		r1.x = mouseX;
		r1.y = mouseY;
		r3 = r1.intersection(r2);
		

		P5.drawRect(this, r1);
		P5.drawRect(this, r2);

		if (r3 != null) {
			fill(0);
			text(r3==null ? -1 : r3.area(),30,30);
			fill(200, 0, 0, 32);			
			P5.drawRect(this, r3);
		}
	}

	public void keyPressed() {
		setup();
	}

	public void settings() {
		size(800, 800);
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { RectRectIntersection.class.getName() });
	}
}
