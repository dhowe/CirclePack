package cp;

import java.awt.Rectangle;

import processing.core.PApplet;

public class RectsDemo extends ImgDemo {

	int num = 500, colors[];

	public void init() {

		colors = testSetColors(num);
		packer = new Packer(PU.testSetVariable(num), width, height);
	}
	
	public void drawPack() {
		stroke(200);
		Rectangle[] r = packer.rec;
		for (int i = 0; i < r.length; i++) {
			fill(colors[i]);
			rect(r[i].x, r[i].y, r[i].width, r[i].height);
			//fill(0, 255, 255);
			//text(i, r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { RectsDemo.class.getName() });
	}
}
