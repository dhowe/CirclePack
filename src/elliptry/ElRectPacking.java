package elliptry;

import java.awt.Rectangle;

import circle.CPU;
import processing.core.PApplet;
import processing.core.PGraphics;

public class ElRectPacking extends ElCirclePacking {

	int[] colors = new int[200];

	public void init() {
		zoom = .5f;
		paused = true;
		packer = new ElPacker(CPU.testset(this, colors), width, height/2);
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

	public void keyPressed() {
		if (key == 'g') {
			packer = new ElPacker(CPU.testset(this, colors), packer.bounds.width, packer.bounds.height);
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
