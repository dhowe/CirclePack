package cp;

import processing.core.PApplet;
import processing.core.PGraphics;

public class EllipseDemo extends ImgDemo {

	int num = 100, colors[];

	public void init() {

		colors = testSetColors(num);
		packer = new Packer(PU.testSetVariable(num), 2300, 1294);
		zoom = .5f;
	}
	
	public void drawPack(PGraphics p) {
		
		p.stroke(200);
		Rect[] r = packer.rec;
		for (int i = 0; i < r.length; i++) {
			p.fill(colors[i]);
			p.rect(r[i].x, r[i].y, r[i].width, r[i].height);
			//p.fill(0, 255, 255);
			//p.text(i, r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { EllipseDemo.class.getName() });
	}
}
