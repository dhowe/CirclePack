package elliptry;

import java.awt.Rectangle;
import java.util.ArrayList;

import circle.*;
import processing.core.PApplet;
import processing.core.PGraphics;

public class SimpleMerTest extends PApplet
{
	int[] colors = new int[100];
	CEllipse bounds;
	Rectangle[] rec,mer;
	float ratio = 2;

	public void setup() {
		rec = CPU.testset(this, colors);
		bounds = new CEllipse(width/2, height/2,0,0);
		center(rec[0], width/2, height/2);
		mer = computeMER();

	}
	public void keyReleased() {
		center(rec[1], width/2+rec[0].width/2, height/2);
		mer = computeMER();
	}
	public void draw() {

		background(255);

		drawMer();
		drawPack(getGraphics());
		drawBounds();
	}
	
	////////////////////////////////////////////////////

	Rectangle[] computeMER() {
		
		CEllipse ellipse = CPU.boundingEllipse(placed(), bounds.x, bounds.y, ratio);
		bounds.width = ellipse.width;
		bounds.height = ellipse.height;
		
		//System.out.println("EllPacker.mer() :: "+bounds.width+","+bounds.width);
		float merOffsetX = bounds.x - Math.round(bounds.width / 2f);
		float merOffsetY = bounds.y - Math.round(bounds.height / 2f);
		
		//System.out.println("CirclePack.offsets() :: "+merOffsetX+","+merOffsetY);
		
		int[][] imer = CMer.rectsToMer(placed());
		for (int i = 0; i < imer.length; i++) { // shift by offset
			imer[i][1] -= merOffsetX;
			imer[i][0] -= merOffsetY;
		}
		//PU.out(imer);
		int rows = Math.round(bounds.height);
		int cols = Math.round(bounds.width);
		imer = CMer.MER(rows, cols, imer);
		
		Rectangle[] result = CMer.merToRects(imer);
		
		return result;//validateMer(result);		
	}

	Rectangle[] placed() {
		
		ArrayList<Rectangle> p = new ArrayList<Rectangle>();
		for (int i = 0; i < rec.length; i++) {
			if (rec[i].x != Integer.MAX_VALUE)
				p.add(rec[i]);
		}
		return p.toArray(new Rectangle[0]);
	}

	public void drawPack(PGraphics p) {
		p.stroke(200);
		Rectangle[] r = rec;
		for (int i = 0; i < r.length; i++) {
			p.fill(colors[i]);
			p.rect(r[i].x, r[i].y, r[i].width, r[i].height);
			// p.fill(0, 255, 255);
			// p.text(i, r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
	}
	
	void drawBounds() {

		noFill();
		stroke(255, 0, 255);
		CEllipse e = bounds;
		ellipse(e.x,e.y,e.width,e.height);
	}

	void drawMer() {

		pushMatrix();
		stroke(200);
		translate((width - bounds.width) / 2, (height - bounds.height) / 2);
		Rectangle[] r = mer;

		for (int i = 0; r != null && i < r.length; i++) {
			noFill();
			rect(r[i].x, r[i].y, r[i].width, r[i].height);
			fill(0);
			text((char) (i + 65), r[i].x + r[i].width / 2, r[i].y + r[i].height / 2);
		}
		popMatrix();
	}

	void center(Rectangle r, int x, int y) {
		r.x = x - Math.round(r.width / 2f);
		r.y = y - Math.round(r.height / 2f);
	}

	void place(Rectangle r, int x, int y) {
		r.x = x;
		r.y = y;
	}
	public void settings() {
		size(1000,800);
	}
	public static void main(String[] args) {

		PApplet.main(new String[] { SimpleMerTest.class.getName() });
	}
}
