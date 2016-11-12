package app;

import java.io.*;
import java.util.*;

import jcp.Packer;
import jcp.util.*;
import processing.core.*;
import processing.event.MouseEvent;

public class ImagePacking extends PApplet {

	///////////////////////// Configuration ///////////////////////////////////
	
	String INPUT_DIR = USER_HOME + "/Desktop/AdCollage/759-ad-images"; // images here
	String OUTPUT_DIR = USER_HOME + "/Desktop";	// [s]ave to this dir
	int MAX_NUM_IMAGES = -1; // -1 for unlimited, files/dirs prefixed with '_' are ignored
		
	/////////////////////////////////////////////////////////////////////////
	
	static String USER_HOME = System.getProperty("user.home");
	
	int ts, animateMs;
	boolean paused = false;
	float panX, panY, zoom = .2f;
	String mousedOver = "";
	Packer packer;
	IRect[] imgs;

	
	public void settings() {
		size(2000, 1000); 						// size determines aspect ratio for packing
	}
	
	public void init() {
		
		imgs = Images.loadAsIRects(INPUT_DIR, MAX_NUM_IMAGES);
		if (imgs.length < 1) throw new RuntimeException("No images found");
		packer = new Packer(imgs, width, height);
		if (paused) advance();
	}

	static String[] getFolders(String path) {
		List<String> files = new ArrayList<String>();
		folders(files, new File(path));
		return files.toArray(new String[0]);
	}
	
	static public void folders(List<String> files, File file) {
			if (file.isDirectory()) {
				files.add(file.getPath());
				File[] children = file.listFiles();
		    for (File child : children) {
		    	folders(files, child);
		    }
			}
	}

	public void setup() {
		surface.setLocation(1200, 0);
		init();
	}

	public void draw() {

		background(255);

		if (!packer.complete())
			drawNext(getGraphics());

		drawInfo(true);
		
		panX = (1 - zoom) * width / 2;
		panY = (1 - zoom) * height / 2;
		translate(panX, panY);
		scale(zoom);

		//drawMer();
		drawPack(getGraphics());
		//drawBounds();
		drawMouseOvers();

		if (!paused && (millis() - ts >= animateMs)) {
			advance();
		}
	}

	private void drawMouseOvers() {
		mousedOver = "";
		float mx = (mouseX-panX) / zoom;
		float my = (mouseY-panY) / zoom;
		for (int i = 0; i < packer.rec.length; i++) {
			IRect ir = (IRect) packer.rec[i];
			if (ir.contains(mx, my)) {
				mousedOver = "image: "+ir.name;
			}
		}
	}

	private void drawInfo(boolean mouseOver) {

		// drawMouseCoords();
		fill(0);
		text("[p]ause, [r]eset, [c]lear, [s] to save, space-bar to step, mouse-wheel to zoom", 10, 20);
		int percent = (int)(packer.percent()*100);
		text("zoom: "+zoom, 10, 40);
		text("aspect: "+packer.ratio, 10, 60);
		text("images: "+packer.rec.length, 10, 80);
		text("status: "+(percent<100 ? percent+"%" : "done"), 10, 100);
		text(mousedOver, 10, 120);		
	}
	
	public void drawNext(PGraphics p) { /* no-op */ }

	public void drawPack(PGraphics p) {

		
		noStroke();
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
		Rect rect = Geom.alignedBoundingRect(packer.placed(), packer.bounds.x,packer.bounds.y);
		P5.drawRect(this, rect);
		
		float diam = Geom.boundingCircle(packer.placed(), packer.bounds.x,packer.bounds.y);
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
			// text((char) (i + 65), r[i].x + r[i].width / 2, r[i].y + r[i].height/2);
		}
		popMatrix();
	}

	public void advance() {
		advance(true);
	}

	public void advance(boolean forward) {

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
			render(OUTPUT_DIR, packer.bounds.width, packer.bounds.height);
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
		
		zoom = min(zoom,1);
	}

	public void render(String outDir, float w, float h) {
		
		System.out.println("Rendering to a "+w+"x"+h+" canvas");
		if (!outDir.endsWith("/")) outDir += "/";
		String name = outDir + "AdRender_"+System.currentTimeMillis() + ".png";
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

	public static void main(String[] args) {
		PApplet.main(new String[] { ImagePacking.class.getName() });
	}
}
