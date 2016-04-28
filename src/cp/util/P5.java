package cp.util;

import processing.core.PApplet;

public abstract class P5 {

	public static void drawEllipse(PApplet p, Ellipse e) {
		p.ellipse(e.x, e.y, e.width, e.height);
	}

	public static void drawRect(PApplet p, Rect r) {
		p.rect(r.x, r.y, r.width, r.height);

	}

	public static void drawPoint(PApplet p, Pt pt) {
		p.ellipse(pt.x, pt.y, 3, 3);
	}
}
