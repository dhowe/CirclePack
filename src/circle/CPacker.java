package circle;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;

public class CPacker {

	int steps, cx, cy, width, height;
	float boundingDiameter;
	Rectangle[] mer, rec;

	public CPacker(Rectangle[] r, int cw, int ch) {
		this.rec = r;
		this.cx = Math.round(cw / 2f);
		this.cy = Math.round(ch / 2f);
		this.width = cw;
		this.height = ch;
		sortByArea(r);
		alignVertically();
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
				
				float bestMaxCornerDist = Float.MAX_VALUE;
				float bestMaxCenterDist = Float.MAX_VALUE;
				
				for (int i = 0; i < mer.length; i++) {

					for (int j = 0; j < 4; j++) {
						
						int px = curr.x, py = curr.y;
						float dia = testPlacement(curr, mer[i], j);
						
						if (dia < bestMaxCornerDist) {
							bestMaxCornerDist = dia;
							bestMaxCenterDist = centerPointDist(curr);
						}
						else if (dia == bestMaxCornerDist && centerPointDist(curr) < bestMaxCenterDist) {
							bestMaxCornerDist = dia;
							bestMaxCenterDist = centerPointDist(curr);
						}
						else {
							
							place(curr, px, py); // revert
						}
					}
				}		
			}
			else {

				center(curr, cx, cy);
			}
			
			mer = computeMER();			
			//System.out.println(steps+") placed: "+curr.x+","+curr.y);
			++steps;
		}
		
		
		return steps;
	}

	float testPlacement(Rectangle curr, Rectangle mer, int type) {
		 
		int x = -1, mx = mer.x + Math.round( (width - boundingDiameter) / 2f);
		int y = -1, my = mer.y + Math.round( (height - boundingDiameter) / 2f);
		
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
		
		return intersectsPack(curr) ? Float.MAX_VALUE : CPU.boundingDiameter(placed(), cx, cy);
	}
	
	// Dist from center point of rect to center point of pack
	float centerPointDist(Rectangle r) {
		
		int rx = r.x + Math.round(r.width / 2f);
		int ry = r.y + Math.round(r.height / 2f);
		return CPU.dist(rx, ry, cx, cy);
	}

	void alignVertically() {

		for (int i = 0; i < rec.length; i++) {
			rec[i].x = Integer.MAX_VALUE; 
			rec[i].y = i== 0 ? 0 : rec[i - 1].y + rec[i - 1].height;
		}
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
		
		boundingDiameter = CPU.boundingDiameter(placed(), cx, cy);
		
		//System.out.println("CirclePack.mer() :: "+boundingDiameter+","+boundingDiameter);
		float merOffsetX = cx - Math.round(boundingDiameter / 2f);
		float merOffsetY = cy - Math.round(boundingDiameter / 2f);
		
		//System.out.println("CirclePack.offsets() :: "+merOffsetX+","+merOffsetY);
		
		int[][] imer = CMer.rectsToMer(placed());
		for (int i = 0; i < imer.length; i++) { // shift by offset
			imer[i][1] -= merOffsetX;
			imer[i][0] -= merOffsetY;
		}
		//PU.out(imer);
		int rbd = Math.round(boundingDiameter);
		imer = CMer.MER(rbd, rbd, imer);
		
		Rectangle[] result = CMer.merToRects(imer);
		
		return validateMer(result);		
	}

	// convert rect{x,y,w,h,} to 4 corner points
	public int[] toCorners(Rectangle b, boolean tf) {
		
		int bx = Math.round( b.x + (tf ? (width - boundingDiameter) / 2f : 0));
		int by = Math.round( b.y + (tf ? (height - boundingDiameter) / 2f : 0));

		int tlX = bx, tlY = by;
		int brX = bx + b.width, brY = by + b.height;
		int trX = bx + b.width, trY = by;
		int blX = bx, blY = by + b.height;

		return new int[] { tlX, tlY, trX, trY, brX, brY, blX, blY };
	}
	
	/*
	 * For each rectangle in the MER, check that all four 
	 * corners are not outside the boundingCircle.
	 * If any are, split them into two...
	 */
	Rectangle[] validateMer(Rectangle[] r) {
		
		ArrayList<Rectangle> rl = new ArrayList<Rectangle>();
		
		for (int i = 0; i < r.length; i++) {
			
			int[] cr = toCorners(r[i], true);
			boolean inside = false;
			for (int j = 0; j < cr.length; j+=2) {
				//System.out.println(PU.dist(cr[j], cr[j+1], cx, cy)+" <= "+ boundingDiameter/2);
				if ( CPU.dist(cr[j], cr[j+1], cx, cy) <= boundingDiameter/2) {
						inside = true;
						break;
				}
			}
			rl.add(r[i]);
			if (!inside) {
				if (i != 0)System.out.println("*** MER#"+i+" SPLITTING...");
				r[i].width /= 2;
				rl.add(new Rectangle(r[i].x + r[i].width, r[i].y, r[i].width,r[i].height));
			}
		}	
		
		return rl.toArray(new Rectangle[0]);
	}

	public void reset() {
		steps = 0;
		boundingDiameter = 0;
		alignVertically();
	}

}
