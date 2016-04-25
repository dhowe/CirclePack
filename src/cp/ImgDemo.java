package cp;

import java.awt.Rectangle;

import processing.core.*;
import processing.event.MouseEvent;

public class ImgDemo extends PApplet {

	int ts, animateMs = 10;
	boolean paused = true;
	float zoom = .2f;

	Packer packer;
	IRect[] imgs;

	public void init() {

		imgs = PU.loadIRects("/Users/dhowe/Desktop/AdCrawl1",50);
		packer = new Packer(imgs, width, height);
		if (paused) advance();
	}

	public void draw() {

		background(255);
		drawMouseCoords();
		fill(100);
		text("[p]ause, [r]eset, [c]lear, [g]enerate, space-bar to step, mouse-wheel to zoom", 10, 20);
		translate((1 - zoom) * width / 2, (1 - zoom) * height / 2);
		scale(zoom);

		drawMer();
		drawPack(getGraphics());
		drawBounds();

		if (!paused && (millis() - ts >= animateMs)) {
			advance();
		}
		

	}

	public void drawPack(PGraphics p) {

		p.stroke(200);
		for (int i = 0; i < packer.rec.length; i++) {
			IRect ir = (IRect) packer.rec[i];
			p.image(ir.image, ir.x, ir.y, imgs[i].width, imgs[i].height);
		}
	}

	void drawMouseCoords() {

		fill(0);
		//textSize(24);
		text(zoom == 1 ? Integer.toString(mouseX) + "," + Integer.toString(mouseY) : "?", 10, 30);
	}

	void drawBounds() {

		noFill();
		stroke(255, 0, 255);
//		float diam = packer.boundingW;
		ellipse(width / 2, height / 2, packer.boundingW, packer.boundingH);
		stroke(0, 155, 155);
		//diam = packer.boundingDiameter2;
		//ellipse(width / 2, height / 2, diam, diam);
	}

	void drawMer() {

		pushMatrix();
		stroke(100);
		translate((width - packer.boundingW) / 2, (height - packer.boundingH) / 2);
		Rectangle[] r = packer.mer;

		for (int i = 0; r != null && i < r.length; i++) {
			noFill();
			rect(r[i].x, r[i].y, r[i].width, r[i].height);
			fill(0);
			text(i, r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
			//text((char) (i + 65), r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
		popMatrix();
	}

	public void advance() {

		if (!packer.complete())
			System.out.println(packer.steps + ") " + (millis() - ts)+"ms");//  (" + packer.boundingW + ")"); 																																													// ")");
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
			if (paused)
				advance();
		} else if (key == 's') {
			paused = true;
			render("/Users/dhowe/Desktop/rendered.png", packer.boundingW, packer.boundingH);
		} 
	}

	public void mouseWheel(MouseEvent event) {

		float e = event.getCount();
		if (e < 0 && zoom < 2)
			zoom *= 1.2;
		else if (e > 0 && zoom > .05)
			zoom /= 1.2;
	}

	int[] testSetColors(int num) {
		int[] colors = new int[num];
		for (int i = 0; i < colors.length; i++) {
			float r = random(0, 150);
			colors[i] = color(225 - r, random(0, r), 100 + r);
		}
		return colors;
	}

	public void render(String name, float w, float h) {

		PGraphics p = createGraphics((int) (w * 1.2), (int) h);
		p.beginDraw();
		p.background(255);
		p.translate((p.width - width) / 2f, (p.height - height) / 2f);
		p.stroke(200);
		drawPack(p);
		p.endDraw();
		if (p.save(name))
			System.out.println("Wrote " + name);
		else
			System.err.println("Write failed for: " + name);
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
