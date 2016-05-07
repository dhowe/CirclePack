package circle;

import java.awt.Rectangle;

import processing.core.PApplet;
import processing.core.PGraphics;

public class CRectPacking extends CCirclePacking {

	int num = 200, colors[];

	public void init() {

		zoom = .5f;
		packer = new CPacker(testset(num), width, height);
		if (paused)
			advance();
	}
	
	private Rectangle[] testset(int num) {
		
		colors = new int[num];
		for (int i = 0; i < colors.length; i++) {
			float r = random(0, 150);
			colors[i] = color(225 - r, random(0, r), 100 + r);
		}

		Rectangle[] r = new Rectangle[num];
		for (int i = 0; i < r.length; i++) {
			int w = (int) (20 + Math.random() * 200);
			int h = (int) (20 + Math.random() * 200);

			if (Math.random() < .01) {
				w = 300;
				h = 600;
			}
			if (Math.random() < .02) {
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
			packer = new CPacker(testset(num), packer.width, packer.height);
			if (paused)
				advance();
			return;
		}
		super.keyPressed();
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { CRectPacking.class.getName() });
	}
}
