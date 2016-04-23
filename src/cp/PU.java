package cp;

import java.awt.Rectangle;

public class PU {

	static int[] AdSizes = { 728, 90, 300, 250, 300, 600, 320, 100, 336, 280,
	// 1940, 88,
	};

	static float boundingDiameter(Rectangle[] r, int cx, int cy) {

		float maxRadiusSoFar = 0;
		for (int i = 0; i < r.length; i++) {
			float d1 = dist(r[i].x, r[i].y, cx, cy); // upper-left
			float d2 = dist(r[i].x + r[i].width, r[i].y, cx, cy); // upper-right
			float d3 = dist(r[i].x, r[i].y + r[i].height, cx, cy); // lower-left
			float d4 = dist(r[i].x + r[i].width, r[i].y + r[i].height, cx, cy); // lower-right
			float maxOfCorners = Math.max(Math.max(d1, d2), Math.max(d3, d4));
			maxRadiusSoFar = Math.max(maxRadiusSoFar, maxOfCorners);
		}

		return maxRadiusSoFar * 2;
	}

	static float dist(float x1, float y1, float x2, float y2) {
		
		return (float) Math.sqrt( sq(x2 - x1) + sq(y2 - y1) );
	}

	static float sq(float f) {
		return (f * f);
	}

	static void center(float[] r, float x, float y) {
		r[0] = x - r[2] / 2f;
		r[1] = y - r[3] / 2f;
	}

	static void pos(float[] r, float x, float y) {
		r[0] = x;
		r[1] = y;
	}

	static void sortByMaxEdge(Rectangle[] r) {
		java.util.Arrays.sort(r, new java.util.Comparator<Rectangle>() {
			public int compare(Rectangle b, Rectangle a) {
				return Float.compare(Math.max(a.width, a.height), Math.max(b.width, b.height));
			}
		});
	}

	static Rectangle[] testSetVariable(int num) {
		Rectangle[] r = new Rectangle[num];
		for (int i = 0; i < r.length; i++) {
			int w = (int) (20 + Math.random() * 200);
			int h = (int) (20 + Math.random() * 200);
			
			if (Math.random() < .01) { w = 300; h = 600; }
			if (Math.random() < .02) { w = 728; h = 90; }
			r[i] = new Rectangle(Integer.MAX_VALUE, 0, w, h);
		}
		return r;
	}
	
	static Rectangle[] testSetFixed(int num) {

		Rectangle[] r = new Rectangle[num];
		for (int i = 0; i < r.length; i++) {
			int idx = (int) (Math.random() * AdSizes.length / 2) * 2;
			int w = AdSizes[idx] / 2;
			int h = AdSizes[idx + 1] / 2;
			//System.out.println(w+"x"+h);
			r[i] = new Rectangle(Integer.MAX_VALUE, 0, w, h);
		}
		return r;
	}

	static float maxEdge(Rectangle r) {

		return Math.max(r.width, r.height);
	}
}
