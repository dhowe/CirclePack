package cp;

import processing.core.PApplet;

public class RectCircleIntersects extends PApplet {

	public void setup() {
		
		float radius = 200, w = 320, h = 320;
		
		Pt c = new Pt(width / 2, height / 2);
		
		Rect r = new Rect(250,250, w, h);
		//Rect r = new Rect(mouseX, mouseY, w, h);

		noFill();
		stroke(0);
		rect(r.x, r.y, r.width, r.height);		
		ellipse(c.x, c.y, radius * 2, radius * 2);

		
		fill(0);
		noStroke();
		Pt[] p = r.intersectsCircle(c.x, c.y, radius);
		for (int i = 0; i < p.length; i++) {
			ellipse(p[i].x, p[i].y, 5, 5);
		}
	}
		
	public void settings() {
		size(800, 800);
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { RectCircleIntersects.class.getName() });
	}
}
