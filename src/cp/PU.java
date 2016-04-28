package cp;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PU {

	static final float SQRT2 = (float) Math.sqrt(2);
	
	static int[] AdSizes = { 728, 90, 300, 250, 300, 600, 320, 100, 336, 280 }; // 1940,
																																							// 88,
	static boolean isImage(File file) {

		return isImage(file.getName());
	}

	static boolean isImage(Path path) {

		return isImage(path.toAbsolutePath().toString());
	}

	static boolean isImage(String name) {

		return name.matches(".+\\.(png|gif|jpe?g)$");
	}

	static File[] imageFiles(String dir, int maxNum) {
		List<File> files = new ArrayList<File>();
		imageFiles(files, FileSystems.getDefault().getPath(dir), maxNum);
		return files.toArray(new File[0]);
	}

	static void imageFiles(List<File> files, Path dir, int maxNum) {

		int max = maxNum < 0 ? Integer.MAX_VALUE : maxNum;

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path path : stream) {
				if (path.toFile().isDirectory()) {
					imageFiles(files, path, max);
				} else {
					if (files.size() >= max)
						break;
					File file = path.toFile();

					if (isImage(file))
						files.add(file);

					// else System.err.println("SKIP: "+file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	static Ellipse boundingEllipse(Rect[] r, int cx, int cy) {
		return boundingEllipse(r, cx, cy, 1);
	}

	static float boundingEllipseArea(Rect[] r, int cx, int cy) {
		return boundingEllipseArea(r, cx, cy, 1);
	}

	static float boundingEllipseArea(Rect[] r, int cx, int cy, float ratio) {
		return boundingEllipse(r, cx, cy, ratio).area();
	}

	static Ellipse boundingEllipse(Rect[] r, int cx, int cy, float ratio) {
		
		Rect rect = PU.alignedBoundingRect(r, cx, cy);
		float diam = PU.boundingCircle(r, cx, cy);
		Pt[] p = rect.intersectsCircle(cx, cy, diam/2f);
		if (p.length == 8) {
			if (ratio > 1)
				rect = Rect.fromCorners(p[0], p[4]);
			else if (ratio < 1)
				rect = Rect.fromCorners(p[7], p[3]);
		}

		// ellipse.width = sqrt(rect.width^2 + ratio^2 * rect.height^2)
		//double ew = Math.sqrt((rect.width * rect.width) + (ratio * ratio) * (rect.height * rect.height));
		double ew = Math.sqrt((rect.width * rect.width) + (ratio * ratio) * (rect.height * rect.height));
		double eh = ew / ratio;

		return new Ellipse(cx, cy, ew, eh);
	}

	static Ellipse boundingEllipseLoose(Rect[] r, int cx, int cy, float ratio) {
		
		Rect rect = PU.alignedBoundingRect(r, cx, cy);
		// ellipse.width = sqrt(rect.width^2 + ratio^2 * rect.height^2)
		double ew = Math.sqrt((rect.width * rect.width) + (ratio * ratio) * (rect.height * rect.height));
		double eh = ew / ratio;
		return new Ellipse(cx, cy, ew, eh);
	}

	static Rect alignedBoundingRect(Rect[] r, int cx, int cy) {

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

	static Rect boundingRect(Rect[] r) {

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = -Integer.MAX_VALUE, maxY = -Integer.MAX_VALUE;

		for (int i = 0; i < r.length; i++) {
			minX = Math.min(minX, r[i].x);
			minY = Math.min(minY, r[i].y);
			maxX = Math.max(maxX, r[i].x + r[i].width);
			maxY = Math.max(maxY, r[i].y + r[i].height);
		}

		return new Rect(minX, minY, maxX - minX, maxY - minY);
	}

	static float boundingCircleDiameterNew(Rect[] r, int cx, int cy) {

		Ellipse be = boundingEllipse(r, cx, cy);
		return Math.max(be.width, be.height);
	}

	static float boundingCircle(Rect[] r, int cx, int cy) { // not used

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

	static Rect[] testSetVariable(int num) {
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

	static public byte[] loadBytes(InputStream input) {
		try {
			BufferedInputStream bis = new BufferedInputStream(input);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			int c = bis.read();
			while (c != -1) {
				out.write(c);
				c = bis.read();
			}
			return out.toByteArray();

		} catch (Throwable e) {
			System.err.println("WARN: " + e.getMessage());
			// throw new RuntimeException("Couldn't load bytes from stream");
		}
		return null;
	}

	static public byte[] loadBytes(File file) {

		InputStream is = null;
		byte[] byteArr = null;
		try {
			is = new FileInputStream(file);
			byteArr = loadBytes(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
		return byteArr;
	}

	public static File[] imageFiles(String path) {
		return imageFiles(path, -1);
	}

	public static IRect[] loadIRects(String path) {

		return loadIRects(path, -1);
	}

	public static IRect[] loadIRects(String path, int maxNum) {

		File[] ifs = PU.imageFiles(path, maxNum);
		List<IRect> pl = new ArrayList<IRect>();

		for (int i = 0; i < ifs.length; i++) {

			IRect pi = null;
			try {

				byte bytes[] = loadBytes(ifs[i]);
				if (bytes == null)
					throw new RuntimeException("NO BYTES FOR: " + ifs[i]);
				pi = new IRect(new javax.swing.ImageIcon(bytes).getImage());
			} catch (Exception e) {
				System.err.println("[WARN] " + e.getMessage());
			}
			if (pi != null)
				pl.add(pi);
		}

		return pl.toArray(new IRect[pl.size()]);
	}

	// ///////////////////////////////////////////////////////////////////

	public static void main(String[] args) {

		// String[] files = imageNames("/Users/dhowe/Desktop/AdCrawl1");
		File[] files = imageFiles("/Users/dhowe/Desktop/AdCrawl1");
		System.out.println("Loaded " + files.length);

	}

}
