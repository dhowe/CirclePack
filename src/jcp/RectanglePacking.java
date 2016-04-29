package jcp;

import jcp.util.*;
import processing.core.PApplet;
import processing.core.PGraphics;

public class RectanglePacking extends ImagePacking {

	int num = 200, colors[];

	public void init() {

		zoom = .5f;
		paused = true;
		packer = new Packer(testset(num), width, height);
		if (paused)
			advance();
	}

	private Rect[] testset(int num) {

		colors = new int[num];
		for (int i = 0; i < colors.length; i++) {
			float r = random(0, 150);
			colors[i] = color(225 - r, random(0, r), 100 + r);
		}

		return Geom.testSetVariable(num);
	}

	public void drawPack(PGraphics p) {
		p.stroke(200);
		Rect[] r = packer.rec;
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

	public void merCorners(PGraphics p) {

		noStroke();
		fill(0);
		Rect[] r = packer.mer;
		for (int i = 0; i < r.length; i++) {
			Pt[] cr = packer.toCorners(r[i], true);
			for (int j = 0; j < cr.length; j++) {
				// System.out.println(cr[j]+","+cr[j+1]);
				ellipse(cr[i].x, cr[i].y, 3, 3);
			}
		}
	}

	public void keyPressed() {
		if (key == 'g') {
			packer = new Packer(testset(num), packer.width, packer.height);
			if (paused)
				advance();
			return;
		}
		super.keyPressed();
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { RectanglePacking.class.getName() });
	}
}
