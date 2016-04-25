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
		Ellipse be = PU.boundingEllipse(r, cx, cy, ratio);
		Ellipse be2 = boundingEllipse(r, cx, cy, ratio, false);
		Ellipse be3 = boundingEllipse(r, cx, cy, ratio, true);
	
		Rectangle br = PU.alignedBoundingRect(r, cx, cy);
		Rectangle br2 = bboundingRect(r, cx, cy, ratio);

		noFill();
		if (be.contains(mouseX, mouseY))
		  fill(200,0,0,4);
		stroke(200, 0, 0);
		ellipse(be.x, be.y, be.width, be.height);
		noFill();

		stroke(0, 200, 0);
		ellipse(be2.x, be2.y, be2.width, be2.height);
		stroke(0, 0, 200);
		ellipse(be3.x, be3.y, be3.width, be3.height);

		rect(br2.x, br2.y, br2.width, br2.height);
		rect(br.x, br.y, br.width, br.height);

		fill(0);
		noStroke();
		ellipse(cx, cy, 5, 5);
	}

	static float SQRT2 = (float) Math.sqrt(2);

	// x^2/a^2 + y^2/b^2 = 1 
	// x^2/2a^2 + y^2/a^2 = 1
	
	static Ellipse boundingEllipse(Rectangle[] r, int cx, int cy, float ratio, boolean b) {
		Rectangle br = PU.alignedBoundingRect(r, cx, cy);

		float rx = br.width * SQRT2;
		float ry = br.height * SQRT2;
		if (b) {
			if (rx > ry * ratio) {
				//System.out.println("adjust Y");
				ry = Math.round(rx / ratio);
			} else if (rx < ry * ratio) {
				//System.out.println("adjust X");
				rx = Math.round(ry * ratio);
			}
		}
		//float ew = Rw/SQRT2;
		//float eh = Rh/SQRT2;
		return new Ellipse(cx, cy, rx, ry);
	}

	static Rectangle bboundingRect(Rectangle[] r, int cx, int cy, float ratio) {
		Rectangle br = PU.alignedBoundingRect(r, cx, cy);
		float rx = br.width;
		float ry = br.height;
		if (rx > ry * ratio) {
			ry = Math.round(rx / ratio);
		} else if (rx < ry * ratio) {
			rx = Math.round(ry * ratio);
		}
		return new Rectangle((int) (cx - rx / 2f), (int) (cy - ry / 2f), (int) rx, (int) ry);

		// int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE,
		// maxX = -Integer.MAX_VALUE, maxY = -Integer.MAX_VALUE;
		//
		// for (int i = 0; i < r.length; i++) {
		// minX = Math.min(minX, r[i].x);
		// minY = Math.min(minY, r[i].y);
		// maxX = Math.max(maxX, r[i].x + r[i].width);
		// maxY = Math.max(maxY, r[i].y + r[i].height);
		// }
		//
		// int rx = Math.round( Math.max( Math.abs(cx-minX), Math.abs(maxX-cx) ) );
		// int ry = Math.round( Math.max( Math.abs(cy-minY), Math.abs(maxY-cy) ) );
		//
		// if (rx > ry * ratio) {
		// ry = Math.round(rx / ratio);
		// }
		// else if (rx < ry * ratio) {
		// rx = Math.round(ry * ratio);
		// }
		//
		// float scale = 1;
		//

		// return new Ellipse(cx, cy, rx, ry);
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { EllipticalArea.class.getName() });
	}
}
