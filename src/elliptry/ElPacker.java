package elliptry;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;

import circle.CMer;
import circle.CPU;

public class ElPacker {

	int steps;
	CEllipse bounds;
	Rectangle[] mer, rec;
	float width, height;

	public ElPacker(Rectangle[] r, int cw, int ch) {
		this.rec = r;
		this.width = cw;
		this.height = ch;
		this.bounds = new CEllipse(Math.round(cw / 2f), Math.round(ch / 2f),0,0);
		sortByArea(r);
		for (int i = 0; i < rec.length; i++)
			rec[i].x = Integer.MAX_VALUE;
	}
	
	public boolean complete() {

		return steps >= rec.length;
	}
	
	public float percent() {

		return steps / (float) rec.length;
	}
	
	public int step() {	
		
		//System.out.println("STEP #"+steps);
		if (steps < rec.length) {

			Rectangle curr = rec[steps];
			if (steps > 0) {
				
				float minArea = Float.MAX_VALUE;
				float minCenterDist = Float.MAX_VALUE;
				
				for (int i = 0; i < mer.length; i++) {

					for (int j = 0; j < 4; j++) {
						
						int px = curr.x, py = curr.y;
						float area = testPlacement(curr, mer[i], j);
						//float area = dia < 0 ? Float.MAX_VALUE : (float) (Math.PI * (dia/2d) * (dia/2d) );
						
						if (area < minArea) {
							minArea = area;
							minCenterDist = centerPointDist(curr);
						}
						else if (area == minArea && centerPointDist(curr) < minCenterDist) {
							minArea = area;
							minCenterDist = centerPointDist(curr);
						}
						else {
							
							place(curr, px, py); // revert
						}
						//return ++steps;
					}
				}		
			}
			else {

				center(curr, bounds.x, bounds.y);
			}
			
			mer = computeMER();		
			++steps;
		}
		
		
		return steps;
	}

	float testPlacement(Rectangle curr, Rectangle mer, int type) {
		 
		int x = -1, mx = mer.x + Math.round( (width - bounds.width) / 2f);
		int y = -1, my = mer.y + Math.round( (height - bounds.height) / 2f);
		
		switch (type) {
		
			case 0:
				x = mx;
				y = my;			
				break;
			case 1:
				x = mx + (mer.width - curr.width);
				y = my;
				break;
			case 2:
				x = mx + (mer.width - curr.width);
				y = my + (mer.height - curr.height);
				break;
			case 3:
				x = mx;
				y = my + (mer.height - curr.height);
				break;
			default:
				throw new RuntimeException();
			}
		
		place(curr, x, y);
		
		return intersectsPack(curr) ? Float.MAX_VALUE : CPU.boundingEllipse(placed(), bounds.x, bounds.y).area();
	}
	
	// Dist from center point of rect to center point of pack
	float centerPointDist(Rectangle r) {
		
		int rx = r.x + Math.round(r.width / 2f);
		int ry = r.y + Math.round(r.height / 2f);
		return CPU.dist(rx, ry, bounds.x, bounds.y);
	}
	
	void sortByArea(Rectangle[] r) {
		java.util.Arrays.sort(r, new java.util.Comparator<Rectangle>() {
			public int compare(Rectangle b, Rectangle a) {
				return Float.compare(a.width * a.height, b.width * b.height);
			}
		});
	}
	
	void sortByArea(Shape[] r) {
		java.util.Arrays.sort(r, new java.util.Comparator<Shape>() {
			public int compare(Shape s1, Shape s2) {
				Rectangle a = s1.getBounds(), b = s2.getBounds();
				return Float.compare(a.width * a.height, b.width * b.height);
			}
		});
	}
	
	boolean intersectsPack(Rectangle curr) {
		Rectangle[] pack = placed();
		for (int i = 0; i < pack.length; i++) {
			if (curr != pack[i] && curr.intersects(pack[i]))
				return true;
		}
		return false;
	}

	void center(Rectangle r, int x, int y) {
		r.x = x - Math.round(r.width / 2f);
		r.y = y - Math.round(r.height / 2f);
	}

	void place(Rectangle r, int x, int y) {
		r.x = x;
		r.y = y;
	}

	Rectangle[] placed() {
		
		ArrayList<Rectangle> p = new ArrayList<Rectangle>();
		for (int i = 0; i < rec.length; i++) {
			if (rec[i].x != Integer.MAX_VALUE)
				p.add(rec[i]);
		}
		return p.toArray(new Rectangle[0]);
	}
	
	Rectangle[] computeMER() {
		
		bounds.width = bounds.height = Math.round(CPU.boundingDiameter(placed(), bounds.x, bounds.y));
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
	
	public void reset() {
		steps = 0;
		bounds.width = bounds.height = 0;
		for (int i = 0; i < rec.length; i++)
			rec[i].x = Integer.MAX_VALUE;
	}

}
