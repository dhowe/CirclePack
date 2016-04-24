package cp;

import java.awt.Rectangle;

import processing.core.PApplet;
import processing.core.PGraphics;

public class EllipticalArea extends PApplet {

	Rectangle[] r;
	
	public void settings() {
		size(1000,800);
	}

	public void setup() {
		r = new Rectangle[10];
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
		
		//int cx = width/2, cy = height/2;
		int cx = mouseX>100?mouseX:width/2, cy = mouseY>100?mouseY:height/2;
		float[] be = PU.boundingEllipse(r, cx, cy);
		
		ellipse(cx,cy,be[0],be[1]);
		fill(0);
		noStroke();
		ellipse(cx,cy,5,5);
	}
	
	public static void main(String[] args) {

		PApplet.main(new String[] { EllipticalArea.class.getName() });
	}
}
