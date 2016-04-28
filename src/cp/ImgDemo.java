package cp;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

public class ImgDemo extends PApplet {

	final String OUTPUT = "/Users/dhowe/Desktop/rendered.png";
	
	int ts, animateMs;
	boolean paused = false;
	float zoom = .2f;

	Packer packer;
	IRect[] imgs;

	public void init() {

		imgs = PU.loadIRects("/Users/dhowe/Desktop/AdCrawl1", 150);
		packer = new Packer(imgs, width, height);
		if (paused)
			advance();
	}

	public void draw() {

		background(255);

		if (!packer.complete())
			drawNext(getGraphics());


		// drawMouseCoords();

		fill(100);
		text("[p]ause, [r]eset, [c]lear, space-bar to step, mouse-wheel to zoom", 10, 20);
		translate((1 - zoom) * width / 2, (1 - zoom) * height / 2);
		scale(zoom);

		//drawMer();
		drawPack(getGraphics());
		//drawBounds();

		if (!paused && (millis() - ts >= animateMs)) {
			advance();
		}
	}
	
	public void drawNext(PGraphics p) { /* no-op */ }

	public void drawPack(PGraphics p) {

		p.stroke(200);
		for (int i = 0; i < packer.rec.length; i++) {
			IRect ir = (IRect) packer.rec[i];
			p.image(ir.image, ir.x, ir.y, imgs[i].width, imgs[i].height);
		}
	}

	void drawMouseCoords() {

		fill(0);
		text(zoom == 1 ? Integer.toString(mouseX) + "," + Integer.toString(mouseY) : "z=" + zoom, 10, 30);
	}

	void drawBounds() {

		noFill();
		stroke(255, 0, 255);
		
		P5.drawEllipse(this, packer.bounds);
		
		stroke(0);
		strokeWeight(3);
		Rect rect = PU.alignedBoundingRect(packer.placed(), packer.bounds.x,packer.bounds.y);
		P5.drawRect(this, rect);
		
		float diam = PU.boundingCircle(packer.placed(), packer.bounds.x,packer.bounds.y);
		ellipse(Math.round(width / 2f), Math.round(height / 2f),diam, diam);
		
		
		strokeWeight(1);
		stroke(0, 155, 155);
		ellipse(Math.round(width / 2f), Math.round(height / 2f),5,5);
	}

	void drawMer() {

		pushMatrix();
		stroke(100);
		translate(packer.bounds.x - Math.round(packer.bounds.width / 2f),
				packer.bounds.y - Math.round(packer.bounds.height / 2f));
		Rect[] r = packer.mer;

		for (int i = 0; r != null && i < r.length; i++) {
			noFill();
			rect(r[i].x, r[i].y, r[i].width, r[i].height);
			fill(0);
			text(i, r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
			// text((char) (i + 65), r[i].x + r[i].width / 2, r[i].y + r[i].height /
			// 2);
		}
		popMatrix();
	}

	public void advance() {
		advance(true);
	}

	public void advance(boolean forward) {

		// if (!packer.complete()) System.out.println
		// (packer.steps + ") " + (millis() - ts)+"ms");
		if (forward)
			packer.step();
		else
			packer.back();
		ts = millis();
	}

	public void keyPressed() {
		//System.out.println(key+" "+keyCode);
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
			render(OUTPUT, packer.bounds.width, packer.bounds.height);
		}
		else if (keyCode == 38) {
			paused = true;
			advance(false);
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

		PGraphics p = createGraphics((int)w, (int)h);
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
		surface.setLocation(1200, 0);
		init();
	}

	public static void main(String[] args) {

		PApplet.main(new String[] { ImgDemo.class.getName() });
	}
}
