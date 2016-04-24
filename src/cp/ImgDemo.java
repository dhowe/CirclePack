package cp;

import java.awt.Rectangle;

import processing.core.*;
import processing.event.MouseEvent;

public class ImgDemo extends PApplet {

	boolean paused = false;
	float minZoom = .1f, zoom = .1f;
	int animateMs = 1, ts = -1;

	Packer packer;
	IRect[] imgs;

	public void init() {

		imgs = PU.loadIRects("/Users/dhowe/Desktop/AdCrawl1");
		packer = new Packer(imgs, width, height);
		if (paused)
			advance();
	}

	public void draw() {

		background(255);
		fill(100);
		text("[p]ause, [r]eset, [c]lear, [g]enerate, space-bar to step, mouse-wheel to zoom", 10, 20);
		translate((1 - zoom) * width / 2, (1 - zoom) * height / 2);
		scale(zoom);

		// drawMer();
		drawPack();
		drawBounds();

		if (!paused && (millis() - ts >= animateMs)) {
			advance();
		}
	}

	public void drawPack() {
		stroke(200);

		for (int i = 0; i < packer.rec.length; i++) {
			IRect ir = (IRect) packer.rec[i];
			image(ir.image, ir.x, ir.y, imgs[i].width, imgs[i].height);
		}
	}

	private void drawMouseCoords() {

		fill(0);
		textSize(24);
		text(zoom == 1 ? (int) mouseX + "," + (int) mouseY : "?", 10, 30);
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

		for (int i = 0; r != null && i < r.length; i++) {
			noFill();
			rect(r[i].x, r[i].y, r[i].width, r[i].height);
			fill(0);
			text((char) (i + 65), r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
		popMatrix();
	}

	public void advance() {
		if (packer.steps > 0)
			System.out.println(packer.steps + ") " + (millis() - ts) + "ms  ("+packer.boundingDiameter+")");		// + packer.mer.length + ")");
		packer.step();
		ts = millis();
	}

	public void keyPressed() {

		if (key == ' ') {
			if (paused)
				advance();
			else
				paused = true;
		} else if (key == 'p')
			paused = !paused;
		else if (key == 'c') {
			noLoop();
			packer.reset();
			redraw();
			packer.reset();
			advance();
			loop();
		} else if (key == 'r') {
			packer.reset();
			paused = false;
		} else if (key == 'g') {
			packer = new Packer(imgs, width, height);
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
			float r = random(0, 150);
			colors[i] = color(225 - r, random(0, r), 100 + r);
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
		surface.setLocation(800, 0);
		init();
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { ImgDemo.class.getName() });
	}
}
