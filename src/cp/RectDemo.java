package cp;

import java.awt.Rectangle;

import processing.core.PApplet;
import processing.core.PGraphics;

public class RectDemo extends ImgDemo {

	int num = 100, colors[];

	public void init() {

		zoom = 1f;
		packer = new Packer(testset(num), width, height);
		packer.ratio = 2;
		if (paused) advance();
	}
	
	private Rectangle[] testset(int num) {
		
		colors = testSetColors(num);
		return PU.testSetVariable(num);
	}

	public void drawPack(PGraphics p) {
		
		p.stroke(200);
		Rectangle[] r = packer.rec;
		for (int i = 0; i < r.length; i++) {
			p.fill(colors[i]);
			p.rect(r[i].x, r[i].y, r[i].width, r[i].height);
			//p.fill(0, 255, 255);
			//p.text(i, r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
	}
	
	public void merCorners(PGraphics p) {
		
		noStroke();
		fill(0);
		Rectangle[] r = packer.mer;
		for (int i = 0; i < r.length; i++) {
			 int[] cr = packer.toCorners(r[i], true);
			 for (int j = 0; j < cr.length; j+=2) {
				 //System.out.println(cr[j]+","+cr[j+1]);
				 ellipse(cr[j],cr[j+1],3,3);
			}
		}
	}

	public void keyPressed() {
		if (key == 'g') {
			packer = new Packer(testset(num), packer.width, packer.height);
			if (paused) advance();
			return;
		}
		super.keyPressed();
	}
	
	public static void main(String[] args) {

		PApplet.main(new String[] { RectDemo.class.getName() });
	}
}
