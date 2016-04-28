package cp.test;

import cp.*;
import cp.util.*;
import processing.core.PApplet;

public class EllipticalArea extends PApplet {

	Rect[] r;

	public void settings() {
		size(1000, 800);
	}

	public void setup() {
		r = new Rect[15];
		for (int i = 0; i < r.length; i++) {
			r[i] = new Rect();
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
		float ratio = .5f;
		
		Rect boundingRect = Geom.alignedBoundingRect(r, cx, cy);
		Ellipse tightEllipse = Geom.boundingEllipse(r, cx, cy, ratio);
		//Ellipse looseEllipse = PU.boundingEllipseLoose(r, cx, cy, ratio);
		float diam = Geom.boundingCircle(r, cx, cy);
		//Ellipse boundingCirc = new Ellipse(cx,cy,diam,diam);

		noFill();
		stroke(0, 0, 200);
		
		//drawEllipse(tightEllipse);
		//drawEllipse(looseEllipse);
		//drawCircle(boundingCirc);
		drawRect(boundingRect);

		fill(0);
		noStroke();
		ellipse(cx, cy, 5, 5);
		
		
		Pt[] p = boundingRect.intersectsCircle(cx, cy, diam/2f);
		for (int i = 0; i < p.length; i++) {
			text(i,p[i].x, p[i].y);
			ellipse(p[i].x, p[i].y, 5, 5);
		}
		
		Rect minRect;
		if (p.length == 8) {
			noFill();
			stroke(200,0,50);
			if (ratio>1)
				minRect = Rect.fromCorners(p[0],p[4]);
			else
				minRect = Rect.fromCorners(p[7],p[3]);
			//drawRect(minRect);
		}
	}

	private void drawCircle(Ellipse e) {
		ellipse(e.x, e.y, e.width, e.height);
	}

	public void drawEllipse(Ellipse e) {
		
		ellipse(e.x, e.y, e.width, e.height);
	}
	
	public void drawRect(Rect r) {
		
		rect(r.x, r.y, r.width, r.height);
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { EllipticalArea.class.getName() });
	}
}
