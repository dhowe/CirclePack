package circle;

import java.awt.Rectangle;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

public class CCirclePacking extends PApplet {

	String OUTPUT_DIR = "/Users/dhowe/Desktop/AdCollage/renders";

	boolean paused = false;
	float minZoom = .1f, zoom = .1f;
	int animateMs = 1, ts = -1;

	CPacker packer;
	CIRect[] imgs;
	String mousedOver="";
	float panX, panY;

	public void init() {

		imgs = CPU.loadIRects("/Users/dhowe/Desktop/AdCollage/Groups/Sexy");
		packer = new CPacker(imgs, width, height);
		if (paused)
			advance();
	}

	public void draw() {

		background(255);

		drawInfo();
		
		panX = (1 - zoom) * width / 2;
		panY = (1 - zoom) * height / 2;
		translate(panX, panY);
		scale(zoom);

		// drawMer();
		drawPack(getGraphics());
		drawBounds();
		drawMouseOvers();

		if (!paused && (millis() - ts >= animateMs)) {
			advance();
		}
	}

	private void drawInfo() {

		// drawMouseCoords();
		fill(0);
		text("[p]ause, [r]eset, [c]lear, [s]ave, space-bar to step, mouse-wheel to zoom", 10, 20);
		int percent = (int)(packer.percent()*100);
		text("zoom: "+zoom, 10, 40);
		text("images: "+packer.rec.length, 10, 60);
		text("status: "+(percent<100 ? percent+"%" : "done"), 10, 80);
		text(mousedOver, 10, 100);
	}
	
	public void drawPack(PGraphics p) {
		p.stroke(200);
		for (int i = 0; i < packer.rec.length; i++) {
			CIRect ir = (CIRect) packer.rec[i];
			p.image(ir.image, ir.x, ir.y, imgs[i].width, imgs[i].height);
		}
	}
	
	private void drawMouseOvers() {
		
		mousedOver = "";
		float mx = (mouseX-panX) / zoom;
		float my = (mouseY-panY) / zoom;
		for (int i = 0; i < packer.rec.length; i++) {
			CIRect ir = (CIRect) packer.rec[i];
			if (ir.contains(mx, my)) {
				mousedOver = "image: "+ir.name;
			}
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
//		if (packer.steps > 0)
//			System.out.println(packer.steps + ") " + (millis() - ts) + "ms  (" + packer.boundingDiameter + ")"); // +
//																																																						// packer.mer.length																																																						// +
																																																						// ")");
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
			packer = new CPacker(imgs, width, height);
			paused = false;
		} else if (key == 's') {
			paused = true;
			render(OUTPUT_DIR, packer.boundingDiameter, packer.boundingDiameter);
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

	public void render(String outDir, float w, float h) {

		System.out.println("Rendering to a " + w + "x" + h + " canvas");
		if (!outDir.endsWith("/"))
			outDir += "/";
		String name = outDir + "AdRender_" + System.currentTimeMillis() + ".png";
		PGraphics p = createGraphics((int) w, (int) h);
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

		PApplet.main(new String[] { CCirclePacking.class.getName() });
	}
}
