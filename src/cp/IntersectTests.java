package cp;

import processing.core.PApplet;

public class IntersectTests extends PApplet {

	public void setup() {
		
		float radius = 200, w = 320, h = 320;
		
		Pt a = new Pt(250, 250);
		Pt b = new Pt(360, 250);
		Pt c = new Pt(width / 2, height / 2);
		
		Rect r = new Rect(a.x, a.y, w, h);

		noFill();
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

		PApplet.main(new String[] { IntersectTests.class.getName() });
	}
}
