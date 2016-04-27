package cp;

import java.util.ArrayList;

public class Rect {

	public int x, y, width, height;

	public Rect() {

		this(0, 0);
	}

	public Rect(int width, int height) {

		this(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
	}

	public Rect(int x, int y, int width, int height) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Rect(Pt pt, int width, int height) {

		this(pt.x, pt.y, width, height);
	}
	
	public Rect(Pt pt, float width, float height) {

		this(pt.x, pt.y, Math.round(width), Math.round(height));
	}

	public Rect(float x, float y, float width, float height) {

		this(Math.round(x), Math.round(y), Math.round(width), Math.round(height));
	}

	public Rect(double x, double y, double width, double height) {

		this(Math.round(x), Math.round(y), Math.round(width), Math.round(height));
	}

	public Pt[] toCorners() {
		return toCorners(0, 0);
	}
	
	// corners after translate(tx,ty);
	public Pt[] toCorners(int tx, int ty) {

		int tlX = x + tx, tlY = y + ty;
		int trX = tlX + width, trY = tlY;
		int blX = tlX, blY = tlY + height;
		int brX = tlX + width, brY = tlY + height;

		return new Pt[] { 
				new Pt(tlX, tlY), new Pt(trX, trY), 
				new Pt(brX, brY), new Pt(blX, blY) 
		};
	}
	
	public Pt[] intersectsCircle(float cx, float cy, float radius)
	{
		return intersectsCircle(new Pt(cx,cy), radius);
	}
	
	public Pt[] intersectsCircle(Pt c, float radius) {
		
		ArrayList<Pt> points = new ArrayList<Pt>();
		Pt[] corners = this.toCorners();
		for (int i = 0; i < corners.length; i++) {
			//System.out.println(corners[i]+" <-> "+corners[i%corners.length]);
			PU.lineCircleIntersects(corners[i], corners[(i+1)%corners.length], c, radius, points);
		}
		return points.toArray(new Pt[0]);
	}

	public float area() {

		return width * height;
	}

	public boolean contains(float X, float Y) {

		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if either dimension is zero, tests below must return false...
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		h += y;
		// overflow || intersect
		return ((w < x || w > X) && (h < y || h > Y));
	}

	public Rect intersection(Rect r) { // not used

		int tx1 = this.x;
		int ty1 = this.y;
		int rx1 = r.x;
		int ry1 = r.y;
		long tx2 = tx1;
		tx2 += this.width;
		long ty2 = ty1;
		ty2 += this.height;
		long rx2 = rx1;
		rx2 += r.width;
		long ry2 = ry1;
		ry2 += r.height;
		if (tx1 < rx1)
			tx1 = rx1;
		if (ty1 < ry1)
			ty1 = ry1;
		if (tx2 > rx2)
			tx2 = rx2;
		if (ty2 > ry2)
			ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		// tx2,ty2 will never overflow (they will never be
		// larger than the smallest of the two source w,h)
		// they might underflow, though...
		if (tx2 < Integer.MIN_VALUE)
			tx2 = Integer.MIN_VALUE;
		if (ty2 < Integer.MIN_VALUE)
			ty2 = Integer.MIN_VALUE;
		
		return new Rect(tx1, ty1, (int) tx2, (int) ty2);
	}

	public boolean intersects(Rect r) {

		int tw = this.width;
		int th = this.height;
		int rw = r.width;
		int rh = r.height;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = this.x;
		int ty = this.y;
		int rx = r.x;
		int ry = r.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		// overflow || intersect
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
	}

	public static void main(String[] args) {

		System.out.println(new Rect(0, 0, 12, 4).area() / Math.PI); // ~12
	}

}
