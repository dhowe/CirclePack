package cp;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Packer {

	int steps;
	Ellipse bounds;
	Rectangle[] mer, rec;
	float ratio,width,height;

	public Packer(Rectangle[] r, int cw, int ch) {
		
		this.rec = r;
		this.width = cw;
		this.height = ch;
		this.ratio = cw/(float)ch;
		this.bounds = new Ellipse(Math.round(cw / 2f), Math.round(ch / 2f), cw, ch);
		sortByMaxEdge(r);
		//sortByArea(r);
	}

	public int step() {

		// System.out.println("STEP #"+steps);
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
						
						} else if (dia == bestMaxCornerDist && centerPointDist(curr) < bestMaxCenterDist) {
							
							bestMaxCornerDist = dia;
							bestMaxCenterDist = centerPointDist(curr);
							
						} else {

							place(curr, px, py); // revert
						}
					}
				}
			} else {

				center(curr, bounds.x, bounds.y);
			}

			mer = computeMER();
			// System.out.println(steps+") placed: "+curr.x+","+curr.y);
			++steps;
		}

		return steps;
	}

	float testPlacement(Rectangle curr, Rectangle mer, int type) {

		int x = -1, mx = mer.x + Math.round((width - bounds.width) / 2f);
		int y = -1, my = mer.y + Math.round((height - bounds.height) / 2f);

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

		//float bc = PU.boundingCircle(placed(), bounds.x, bounds.y);
		//if (bc != bc2) System.out.println("FAIL "+bc+" "+bc2);
		return intersectsPack(curr) ? Float.MAX_VALUE : bounds.area();
	}

	// Dist from center point of rect to center point of pack
	float centerPointDist(Rectangle r) {

		int rx = r.x + Math.round(r.width / 2f);
		int ry = r.y + Math.round(r.height / 2f);
		return PU.dist(rx, ry, bounds.x, bounds.y);
	}

	void sortByArea(Rectangle[] r) {
		java.util.Arrays.sort(r, new java.util.Comparator<Rectangle>() {
			public int compare(Rectangle b, Rectangle a) {
				return Float.compare(a.width * a.height, b.width * b.height);
			}
		});
	}
	
	static void sortByMaxEdge(Rectangle[] r) {
		java.util.Arrays.sort(r, new java.util.Comparator<Rectangle>() {
			public int compare(Rectangle b, Rectangle a) {
				return Float.compare(Math.max(a.width, a.height), Math.max(b.width, b.height));
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

		Rectangle[] sofar = placed();
		Ellipse be = PU.boundingEllipse(sofar, bounds.x, bounds.y);
		bounds.width = be.width;
		bounds.height = be.height;
		
		int[][] imer = Mer.rectsToMer(sofar);
		
		int merOffsetX = bounds.x - Math.round(bounds.width / 2f),
				merOffsetY = bounds.y - Math.round(bounds.height / 2f);
		
		for (int i = 0; i < imer.length; i++) { // shift by offset
			imer[i][1] -= merOffsetX;
			imer[i][0] -= merOffsetY;
		}

		int rbw = Math.round(bounds.width);
		int rbh = Math.round(bounds.height);
		imer = Mer.MER(rbh, rbw, imer);

		Rectangle[] result = Mer.merToRects(imer);

		return validateMer(result);
	}

	// convert rect{x,y,w,h,} to 4 corner points
	public int[] toCorners(Rectangle b, boolean tf) {

		int bx = b.x + Math.round((tf ? (width - bounds.width) / 2f : 0));
		int by = b.y + Math.round((tf ? (height - bounds.height) / 2f : 0));

		int tlX = bx, tlY = by;
		int trX = bx + b.width, trY = by;
		int brX = bx + b.width, brY = by + b.height;
		int blX = bx, blY = by + b.height;

		return new int[] { tlX, tlY, trX, trY, brX, brY, blX, blY };
	}

	/*
	 * For each rectangle in the MER, check that all four corners are not outside
	 * the boundingCircle. If any are, split them into two...
	 */
	Rectangle[] validateMer(Rectangle[] mer) {

		ArrayList<Rectangle> rl = new ArrayList<Rectangle>();

		for (int i = 0; i < mer.length; i++) {
			
			//System.out.println(i+") "+mer[i]);

			int[] cr = toCorners(mer[i], true); // TODO: REMOVE TRANSFORMS
			
			boolean inside = false;
			for (int j = 0; j < cr.length; j += 2) {
				//System.out.println("Checking corner #"+(j/2)+" of MER#"+i+": "+cr[j]+","+cr[j + 1]+" "+bounds.containsVal(cr[j], cr[j + 1]));

				if (bounds.contains(cr[j], cr[j + 1])) {
					//System.out.println("Corner #"+(j/2)+" of MER#"+i+" inside ellipse");
					inside = true;
					break;
				}
			}
			rl.add(mer[i]);
			
			if (!inside) {  // all four corners are outside our bounds
				
				if (i != 0)System.out.println("*** MER#" + i + " SPLITTING... (no points inside bounds)");
				mer[i].width /= 2;
				rl.add(new Rectangle(mer[i].x + mer[i].width, mer[i].y, mer[i].width, mer[i].height));
			}
		}

		return rl.toArray(new Rectangle[0]);
	}

	public boolean complete() {
		return steps >= rec.length;
	}

	public void reset() {
		steps = 0;
		bounds.width = bounds.height = 0;
		for (int i = 0; i < rec.length; i++) {
			rec[i].x = Integer.MAX_VALUE;
		}
	}

}
