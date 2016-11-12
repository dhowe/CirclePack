package elliptry;

import java.awt.Rectangle;

import jcp.util.*;
import circle.CPU;
import processing.core.PApplet;
import processing.core.PGraphics;

public class ElRectPacking extends ElCirclePacking {

	int num = 200, colors[];

	public void init() {
		randomSeed(0);
		zoom = .5f;
		paused = true;
		packer = new ElPacker(testset(num), width, height);
		if (paused)
			advance();
	}
	
	public void draw() {

		background(255);

		drawInfo();
		
		panX = (1 - zoom) * width / 2;
		panY = (1 - zoom) * height / 2;
		translate(panX, panY);
		scale(zoom);

		drawMer();
		drawPack(getGraphics());
		
		Rect r = CPU.boundingRect(packer.placed());
		noFill();
		stroke(200,0,0);
		P5.drawRect(this, r);
	  float d = CPU.boundingCircle(packer.placed(),width/2,height/2);
		noFill();
		stroke(0,200,0);
		ellipse(width/2,height/2,d,d);
		
		drawBounds();
		//drawMouseOvers();

		if (!paused && (millis() - ts >= animateMs)) {
			advance();
		}
	}
	
	public static void drawEllipse(PApplet p, CEllipse e) {
		p.ellipse(e.x, e.y, e.width, e.height);
	}
	
	private Rectangle[] testset(int num) {
		randomSeed(0);
		colors = new int[num];
		for (int i = 0; i < colors.length; i++) {
			float r = random(0, 150);
			colors[i] = color(225 - r, random(0, r), 100 + r);
		}

		Rectangle[] r = new Rectangle[num];
		for (int i = 0; i < r.length; i++) {
			int w = (int) (20 + random(200));
			int h = (int) (20 + random(200));

			if (random(0,1) < .01) {
				w = 300;
				h = 600;
			}
			if (random(0,1) < .02) {
				w = 728;
				h = 90;
			}
			r[i] = new Rectangle(Integer.MAX_VALUE, 0, w, h);
		}
		return r;
	}
	
	
	public void drawPack(PGraphics p) {
		p.stroke(200);
		Rectangle[] r = packer.rec;
		for (int i = 0; i < r.length; i++) {
			p.fill(colors[i]);
			p.rect(r[i].x, r[i].y, r[i].width, r[i].height);
			// p.fill(0, 255, 255);
			// p.text(i, r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
	}

	public void drawNext(PGraphics p) {
		pushMatrix();
		scale(zoom);
		int idx = packer.steps % packer.rec.length;
		int cw = packer.rec[idx].width;
		int ch = packer.rec[idx].height;
		p.fill(colors[idx]);
		p.rect(400, 400, cw, ch);
		// if (packer.mer != null)p.rect(width/2, packer.mer[0].y, cw, ch);
		popMatrix();
	}

	public void keyPressed() {
		if (key == 'g') {
			packer = new ElPacker(testset(num), packer.bounds.width, packer.bounds.width);
			if (paused)
				advance();
			return;
		}
		super.keyPressed();
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { ElRectPacking.class.getName() });
	}
}
