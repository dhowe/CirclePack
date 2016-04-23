package cp;

import java.awt.Rectangle;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

public class Demo extends PApplet {

	boolean paused = false;
	float minZoom = .1f, zoom = .5f;
	int num = 100, colors[];
	
	Packer packer;

	public void init() {

		colors = testSetColors(num);
		packer = new Packer(PU.testSetVariable(num), width, height);
		packer.step();
		frameRate(8);
	}

	public void draw() {
		
		background(255);

		translate((1-zoom)*width/2, (1-zoom)*height/2);
		scale(zoom);

		//drawMer();
		drawPack();
		drawBounds();
		
		if (!paused) packer.step();
	}

	void drawBounds() {
		
		noFill();
		stroke(255, 0, 255);
		float diam = packer.boundingDiameter;
		ellipse(width / 2, height / 2, diam, diam);
	}

	void drawMer() {
		
		pushMatrix();
		stroke(200);
		translate((width - packer.boundingDiameter) / 2, (height - packer.boundingDiameter) / 2);
		Rectangle[] r = packer.mer; 
		
		for (int i = 0; r!=null && i < r.length; i++) {
			noFill();
			rect(r[i].x, r[i].y, r[i].width, r[i].height);
			fill(0);
			text((char) (i + 65), r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
		popMatrix();
	}

	void drawPack() {
		stroke(200);
		Rectangle[] r = packer.rec;
		for (int i = 0; i < r.length; i++) {
			fill(colors[i]);
			//int x = (r[i].x == Integer.MAX_VALUE) ? 0 : r[i].x;
			rect(r[i].x, r[i].y, r[i].width, r[i].height);
			fill(0, 255, 255);
			text(i, r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
	}

	public void keyPressed() {
		
		if (key == ' ') {
			if (paused) packer.step();
			else paused = true;
		}
		else if (key == 'p') 
			paused = !paused;
		else if (key == 'c') {
			noLoop();
			packer.reset();
			redraw();
			packer.reset();
			packer.step();
			loop();
		}
		else if (key == 'r') {
			packer.reset();
			paused = false;
		}
		else if (key == 'g') {
			packer = new Packer(PU.testSetVariable(num), width, height);
			paused = false;
		}

	
	}

	/***********************************************************************************/
	
	public void mouseWheel(MouseEvent event) {
		@SuppressWarnings("deprecation")
		float e = event.getAmount();
		if (e == -1 && zoom < 1)
			zoom *= 1.4;
		else if (e == 1 && zoom > minZoom)
			zoom /= 1.4;
	}

	int[] testSetColors(int num) {
		int[] colors = new int[num];
		for (int i = 0; i < colors.length; i++) {
			float r = random(0,150);
			colors[i] = color(225-r, random(0,r), 100+r);
		}
		return colors;
	}

	
	public void render(String name, int w, int h) {

		PGraphics p = createGraphics(w, 4000);
		p.beginDraw();
		p.fill(255);
		// contents
		p.save(name);
	}

	public void settings() {
		size(2000, 1300);
	}

	public void setup() {
		surface.setLocation(1200, 0);
		init();
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { Demo.class.getName() });
	}
}
