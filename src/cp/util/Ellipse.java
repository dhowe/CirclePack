package cp.util;


public class Ellipse {

	public static final float SQRT2 = (float) Math.sqrt(2);

	public int x, y, width, height;

	public Ellipse() {

		this(0, 0, 0, 0);
	}

	public Ellipse(int x, int y, int width, int height) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/*
	 * public Ellipse(Rectangle r) { this.width = Math.round(r.width * SQRT2);
	 * this.height = Math.round(r.height * SQRT2); this.x = Math.round(r.x +
	 * r.width/2f); this.y = Math.round(r.y + r.width/2f); }
	 */

	public Ellipse(float x, float y, float width, float height) {

		this(Math.round(x), Math.round(y), Math.round(width), Math.round(height));
	}

	public Ellipse(double x, double y, double width, double height) {

		this(Math.round(x), Math.round(y), Math.round(width), Math.round(height));
	}
	
	@Override
	public String toString() {

		return "[" + x + ", " + y + ", " + width + ", " + height + "]";
	}
	
	public float area() {

		return (float) (Math.PI * (width / 2d) * (height / 2d));
	}

	public boolean contains(float X, float Y) {

		if (width <= 0.0 || height <= 0.0)
			return false;

		double dx = X - x, rx = width / 2f;
		double dy = Y - y, ry = height / 2f;
		double left = (dx * dx) / (rx * rx);
		double right = (dy * dy) / (ry * ry);

		return left + right <= 1;
	}

	public static void main(String[] args) {

		System.out.println(new Ellipse(0, 0, 12, 4).area() / Math.PI); // ~12
	}

}
