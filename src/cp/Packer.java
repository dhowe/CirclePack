package cp;

import java.util.ArrayList;

import cp.util.*;

public class Packer {

	Ellipse bounds;
	Rect[] mer, rec;
	float ratio, width, height;
	int steps, maxOverlapArea = 300;

	public Packer(Rect[] r, float cw, float ch) {

		this.rec = r;
		this.width = cw;
		this.height = ch;
		this.ratio = cw / ch;
		System.out.println(cw + "x" + ch + " ratio: " + ratio);
		this.bounds = new Ellipse(Math.round(cw / 2f), Math.round(ch / 2f), cw, ch);
		// sortByMaxEdge(r);
		sortByArea(r);
	}
	
	Rect[] computeMER() {

		Rect[] sofar = placed();
		bounds = Geom.boundingEllipse(sofar, bounds.x, bounds.y, ratio);

		// translate packed rects from bounds.x/bounds.y to 0,0
		int[][] imer = Mer.rectsToMer(sofar, 
				-bounds.x + Math.round(bounds.width / 2f),
				-bounds.y + Math.round(bounds.height / 2f));

		imer = Mer.MER(Math.round(bounds.height), Math.round(bounds.width), imer);

		Rect[] result = Mer.merToRects(imer);
		
    /*for (int i = 0; i < result.length; i++)
			System.out.println("M"+i+") "+result[i].x+","+result[i].y+" "+result[i].width+"x"+result[i].height);
		System.out.println();*/
		return validateMer(result);
	}
	
	public int step() {

		// System.out.println("STEP #"+steps);
		if (steps < rec.length) {

			Rect curr = rec[steps];
			
			int bi=-1, bj=-1;
			
			if (steps > 0) {

				float bestEllipseArea = Float.MAX_VALUE;
				float bestCenterDist = Float.MAX_VALUE;

				boolean dbug = false;
				for (int i = mer.length-1; i>=0; i--) {
					
					dbug = (false && i == 0);
					if(dbug)System.out.println("MER#"+i);
					
					for (int j = 0; j < 4; j++) {

						int px = curr.x, py = curr.y;
						float ellipseArea = testPlacement(curr, mer[i], j);
						
						if(dbug)System.out.println("  "+j+" = "+(ellipseArea==Float.MAX_VALUE ? "FAIL" : ellipseArea));
						
						if (ellipseArea < bestEllipseArea) {

							bi = i; bj = j;
							bestEllipseArea = ellipseArea;
							bestCenterDist = centerPointDist(curr);

						} else if (ellipseArea == bestEllipseArea && centerPointDist(curr) < bestCenterDist) {

							bi = i; bj = j;
							bestEllipseArea = ellipseArea;
							bestCenterDist = centerPointDist(curr);

						} else {

							place(curr, px, py); // revert
						}
					}
					if(dbug)System.out.println();
				}
			} else {

				center(curr, bounds.x, bounds.y);
			}

			mer = computeMER();
			
			//System.out.println(steps+") placed: "+curr.x+","+curr.y+" -> "+PU.boundingEllipseArea(placed(), bounds.x, bounds.y, ratio)+(steps>0?" in MER#"+bi+"."+bj:""));
			
			++steps;
		}

		return steps;
	}
	
	public void back() {		
		rec[--steps].x = Integer.MAX_VALUE;
		mer = computeMER();
	}


	float testPlacement(Rect curr, Rect mer, int type) {

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

		float totalArea = Geom.boundingEllipseArea(placed(), bounds.x, bounds.y, ratio); 

		return intersectsPack(curr) ? Float.MAX_VALUE : totalArea;
	}

	// Dist from center point of rect to center point of pack
	float centerPointDist(Rect r) {

		Pt center = r.center();
		return center.dist(bounds.x, bounds.y);
	}

	void sortByArea(Rect[] r) {
		java.util.Arrays.sort(r, new java.util.Comparator<Rect>() {
			public int compare(Rect b, Rect a) {
				return Float.compare(a.width * a.height, b.width * b.height);
			}
		});
	}

	static void sortByMaxEdge(Rect[] r) {
		java.util.Arrays.sort(r, new java.util.Comparator<Rect>() {
			public int compare(Rect b, Rect a) {
				return Float.compare(Math.max(a.width, a.height), Math.max(b.width, b.height));
			}
		});
	}

	boolean intersectsPack(Rect curr) {
		Rect[] pack = placed();
		for (int i = 0; i < pack.length; i++) {
			if (curr != pack[i]) {
				float intersectionArea = curr.intersection(pack[i]).area();
				
				// TODO: this should be relative to the size
				float slop = Math.min(curr.area(),pack[i].area()) / 50f;
				if (intersectionArea > slop) {
					
					//System.out.println("PACK-INTERSECT R#"+i+" area="+intersectionArea+" "+curr+" "+pack[i]);
					return true;
				}
			}
		}
		return false;
	}

	void center(Rect r, int x, int y) {
		r.x = x - Math.round(r.width / 2f);
		r.y = y - Math.round(r.height / 2f);
	}

	void place(Rect r, int x, int y) {
		r.x = x;
		r.y = y;
	}

	Rect[] placed() {

		ArrayList<Rect> p = new ArrayList<Rect>();
		for (int i = 0; i < rec.length; i++) {
			if (rec[i].x != Integer.MAX_VALUE)
				p.add(rec[i]);
		}
		return p.toArray(new Rect[0]);
	}

	/*
	 * For each rectangle in the MER, check that all four corners are not outside
	 * the boundingCircle. If any are, split them into two... TODO: check
	 * orientation for split
	 */
	Rect[] validateMer(Rect[] mer) {

		ArrayList<Rect> rl = new ArrayList<Rect>();

		for (int i = 0; i < mer.length; i++) {

			// System.out.println(i+") "+mer[i]);
			Pt[] cr = toCorners(mer[i], true); // TODO: REMOVE TRANSFORMS?

			boolean inside = false;
			for (int j = 0; j < cr.length; j++) {
				// System.out.println("Checking corner #"+(j/2)+" of MER#"+i+": "+cr[j]+","+cr[j
				// + 1]);
				if (bounds.contains(cr[j].x, cr[j].y)) {
					// System.out.println("Corner #"+(j/2)+" of MER#"+i+" inside ellipse");
					inside = true;
					break;
				}
			}
			rl.add(mer[i]);

			if (!inside) { // all four corners are outside our bounds

				if (i != 0) continue; // Only the first ?
				//System.out.println("*** MER#" + i + " SPLITTING...");
				mer[i].width /= 2;
				rl.add(new Rect(mer[i].x + mer[i].width, mer[i].y, mer[i].width, mer[i].height));
			}
		}

		return rl.toArray(new Rect[0]);
	}

	/* convert rect{x,y,w,h,} to 4 corner points */
	public Pt[] toCorners(Rect bb, boolean tf) {

		return !tf ? bb.toCorners() : bb.toCorners(
				Math.round((width - bounds.width) / 2f),
				Math.round((width - bounds.width) / 2f));
	}

	public boolean complete() {

		return steps >= rec.length;
	}
	
	public float percent() {

		return steps / (float)rec.length;
	}


	public void reset() {
		steps = 0;
		bounds.width = bounds.height = 0;
		for (int i = 0; i < rec.length; i++) {
			rec[i].x = Integer.MAX_VALUE;
		}
	}


}
