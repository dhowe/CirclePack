package cp;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import processing.core.PApplet;

public class EllipticalArea extends PApplet {

	Rectangle[] r;
	
	public void settings() {
		size(1000,800);
	}

	public void setup() {
		r = new Rectangle[5];
		for (int i = 0; i < r.length; i++) {
			r[i] = new Rectangle();
			r[i].x = (int) random(200,600);
			r[i].y = (int) random(200,400);
			r[i].width = (int) random(50,300);
			r[i].height = (int) random(50,400);
		}
	}
	
	public void keyPressed() {
		setup();
	}
	
	public void draw() {
		
		background(255);
		stroke(0);
		fill(0,16);
		for (int i = 0; i < r.length; i++) {
			rect(r[i].x,r[i].y,r[i].width,r[i].height);
		}
		
		int cx = width/2, cy = height/2;
		Ellipse be = PU.boundingEllipse(r, cx, cy);
		//Ellipse2D be2 = new Ellipse2D.Float(be.x,be.y,be.width,be.height);
		Rectangle br = PU.alignedBoundingRect(r, cx, cy);
		
		noFill();
		if (be.contains(mouseX, mouseY))
			fill(200,0,0,64);
		stroke(200,200,0);
		ellipse(be.x,be.y,be.width,be.height);
		rect(br.x,br.y,br.width,br.height);
		fill(0);
		noStroke();
		ellipse(cx,cy,5,5);
	}
	
	public static void main(String[] args) {

		PApplet.main(new String[] { EllipticalArea.class.getName() });
	}
}
