package cp;

import processing.core.PApplet;
import processing.core.PGraphics;

public class RectDemo extends ImgDemo {

	int num = 100, colors[];

	public void init() {

		zoom = .5f;
		packer = new Packer(testset(num), width, height / 2);
		if (paused) advance();
	}

	private Rect[] testset(int num) {

		colors = testSetColors(num);
		return PU.testSetVariable(num);
	}

	public void drawPack(PGraphics p) {
		drawNext(p);
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

		int idx = packer.steps % packer.rec.length;
		int cw = packer.rec[idx].width;
		int ch = packer.rec[idx].height;
		p.fill(colors[idx]);
		p.rect(100, height/2 - packer.bounds.height-100, cw, ch);
		//if (packer.mer != null)p.rect(width/2, packer.mer[0].y, cw, ch);
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

		PApplet.main(new String[] { RectDemo.class.getName() });
	}
}
