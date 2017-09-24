package jcp.util;

import java.util.ArrayList;
import java.util.List;

public class Geom {

	static final float SQRT2 = (float) Math.sqrt(2);

	static int[] AdSizes = { 728, 90, 300, 250, 300, 600, 320, 100, 336, 280 }; // 1940,
																																							// 88,

	// ///////////////////////////////////////////////////////////////

	static Pt[] lineCircleIntersects(Pt a1, Pt a2, Pt c, float r) {

		List<Pt> points = new ArrayList<Pt>();
		lineCircleIntersects(a1, a2, c, r, points);
		return points.toArray(new Pt[0]);
	}
	
	static void lineCircleIntersects(Pt a1, Pt a2, Pt c, float r, List<Pt> points) {

		float a = (a2.x - a1.x) * (a2.x - a1.x) + (a2.y - a1.y) * (a2.y - a1.y);
		float b = 2 * ((a2.x - a1.x) * (a1.x - c.x) + (a2.y - a1.y) * (a1.y - c.y));
		float cc = c.x * c.x + c.y * c.y + a1.x * a1.x + a1.y * a1.y - 2 * (c.x * a1.x + c.y * a1.y) - r * r;
		float d = b * b - 4 * a * cc;
		float e = (float) Math.sqrt(d);
		float u1 = (-b + e) / (2 * a);
		float u2 = (-b - e) / (2 * a);
		if (!((u1 < 0 || u1 > 1) && (u2 < 0 || u2 > 1))) {
			if (0 <= u2 && u2 <= 1)
				points.add(new Pt(lerp(a1.x, a2.x, u2), lerp(a1.y, a2.y, u2)));
			if (0 <= u1 && u1 <= 1)
				points.add(new Pt(lerp(a1.x, a2.x, u1), lerp(a1.y, a2.y, u1)));
		}
	}

	static final float lerp(float start, float stop, float amt) {
		return start + (stop - start) * amt;
	}

	//
	// static Ellipse boundingEllipse(Rect[] r, int cx, int cy) {
	// return boundingEllipse(r, cx, cy, 1);
	// }

	// static float boundingEllipseArea(Rect[] r, int cx, int cy) {
	// return boundingEllipseArea(r, cx, cy, 1);
	// }

	public static float boundingEllipseArea(Rect[] r, int cx, int cy, float ratio) {
		return boundingEllipse(r, cx, cy, ratio).area();
	}


	
	public static Ellipse boundingEllipse(Rect[] r, int cx, int cy, float ratio) {

		Rect rect = Geom.alignedBoundingRect(r, cx, cy);
		float diam = Geom.boundingCircle(r, cx, cy);
		Pt[] p = rect.intersectsCircle(cx, cy, diam / 2f);
		if (p.length == 8) {
			if (ratio > 1)
				rect = Rect.fromCorners(p[0], p[4]);
			else if (ratio < 1)
				rect = Rect.fromCorners(p[7], p[3]);
		}

		// ellipse.width = sqrt(rect.width^2 + ratio^2 * rect.height^2)
		// double ew = Math.sqrt((rect.width * rect.width) + (ratio * ratio) *
		// (rect.height * rect.height));
		double ew = Math.sqrt((rect.width * rect.width) + (ratio * ratio) * (rect.height * rect.height));
		double eh = ew / ratio;

		return new Ellipse(cx, cy, ew, eh);
	}

	public static Ellipse boundingEllipseLoose(Rect[] r, int cx, int cy, float ratio) {

		Rect rect = Geom.alignedBoundingRect(r, cx, cy);
		// ellipse.width = sqrt(rect.width^2 + ratio^2 * rect.height^2)
		double ew = Math.sqrt((rect.width * rect.width) + (ratio * ratio) * (rect.height * rect.height));
		double eh = ew / ratio;
		return new Ellipse(cx, cy, ew, eh);
	}

	public static Rect alignedBoundingRect(Rect[] r, int cx, int cy) {

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = -Integer.MAX_VALUE, maxY = -Integer.MAX_VALUE;

		for (int i = 0; i < r.length; i++) {
			minX = Math.min(minX, r[i].x);
			minY = Math.min(minY, r[i].y);
			maxX = Math.max(maxX, r[i].x + r[i].width);
			maxY = Math.max(maxY, r[i].y + r[i].height);
		}

		int w = Math.round(Math.max(Math.abs(minX - cx), Math.abs(maxX - cx)));
		int h = Math.round(Math.max(Math.abs(minY - cy), Math.abs(maxY - cy)));

		return new Rect(cx - w, cy - h, w * 2, h * 2);
	}

	public static Rect boundingRect(Rect[] r) {

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = -Integer.MAX_VALUE, maxY = -Integer.MAX_VALUE;

		for (int i = 0; i < r.length; i++) {
			minX = Math.min(minX, r[i].x);
			minY = Math.min(minY, r[i].y);
			maxX = Math.max(maxX, r[i].x + r[i].width);
			maxY = Math.max(maxY, r[i].y + r[i].height);
		}

		return new Rect(minX, minY, maxX - minX, maxY - minY);
	}

	//
	// static float boundingCircleDiameterNew(Rect[] r, int cx, int cy) {
	//
	// Ellipse be = boundingEllipse(r, cx, cy);
	// return Math.max(be.width, be.height);
	// }

	public static float boundingCircle(Rect[] r, int cx, int cy) { // not used

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

		return (float) Math.sqrt(sq(x2 - x1) + sq(y2 - y1));
	}

	static float sq(float f) {
		return (f * f);
	}

	public static Rect[] testSetVariable(int num) {
		Rect[] r = new Rect[num];
		for (int i = 0; i < r.length; i++) {
			int w = (int) (20 + Math.random() * 200);
			int h = (int) (20 + Math.random() * 200);

			if (Math.random() < .01) {
				w = 300;
				h = 600;
			}
			if (Math.random() < .02) {
				w = 728;
				h = 90;
			}
			r[i] = new Rect(Integer.MAX_VALUE, 0, w, h);
		}
		return r;
	}

	static Rect[] testSetFixed(int num) {

		Rect[] r = new Rect[num];
		for (int i = 0; i < r.length; i++) {
			int idx = (int) (Math.random() * AdSizes.length / 2) * 2;
			int w = AdSizes[idx];
			int h = AdSizes[idx + 1];
			// System.out.println(w+"x"+h);
			r[i] = new Rect(Integer.MAX_VALUE, 0, w, h);
		}
		return r;
	}

	static float maxEdge(Rect r) {

		return Math.max(r.width, r.height);
	}

}
