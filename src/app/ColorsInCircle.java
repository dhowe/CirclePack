package app;

import java.awt.Rectangle;

import circle.*;
import processing.core.PApplet;
import processing.core.PGraphics;

public class ColorsInCircle extends ImagesInCircle {

	int[] colors = new int[200];

	public void init() {
		zoom = .5f;
		//paused = true;
		packer = new CPacker(CPU.testset(this, colors), width, height);
		if (paused)
			advance();
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
			packer = new CPacker(CPU.testset(this, colors), width, height);
			if (paused)
				advance();
			return;
		}
		super.keyPressed();
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { ColorsInCircle.class.getName() });
	}
}
