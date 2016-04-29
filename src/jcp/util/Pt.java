package jcp.util;

public class Pt {

	public int x, y, width, height;

	public Pt() {

		this(0, 0);
	}

	public Pt(int x, int y) {

		this.x = x;
		
		this.y = y;
	}

	public Pt(float x, float y) {

		this(Math.round(x), Math.round(y));
	}

	public Pt(double x, double y) {

		this(Math.round(x), Math.round(y));
	}

	@Override
	public String toString() {

		return "[" + x + ", " + y + "]";
	}

	public static void main(String[] args) {

		System.out.println(new Pt(12, 4));
	}

	public float dist(int x2, int y2) {

		 return Geom.dist(x, y, x2, y2);
	}

}
