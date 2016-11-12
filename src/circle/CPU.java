package circle;

import java.awt.Rectangle;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import jcp.util.*;
import processing.core.PApplet;
import elliptry.CEllipse;

public class CPU {

	static int[] AdSizes = { 728, 90, 300, 250, 300, 600, 320, 100, 336, 280,
	// 1940, 88,
	};

	public static Rectangle[] testset(PApplet p, int[] colors) {

		for (int i = 0; i < colors.length; i++) {
			float r = p.random(0, 150);
			colors[i] = p.color(225 - r, p.random(0, r), 100 + r);
		}

		Rectangle[] r = new Rectangle[colors.length];
		for (int i = 0; i < r.length; i++) {
			int w = (int) (20 + p.random(200));
			int h = (int) (20 + p.random(200));

			if (p.random(0,1) < .01) {
				w = 300;
				h = 600;
			}
			if (p.random(0,1) < .02) {
				w = 728;
				h = 90;
			}
			r[i] = new Rectangle(Integer.MAX_VALUE, 0, w, h);
		}
		
		return r;
	}
	
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
					if (files.size() >= max) break;
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


	
	public static CEllipse boundingEllipseNew(Rectangle[] r, int cx, int cy, float ratio) {

		float maxX= 0, maxY = 0;
		for (int i = 0; i < r.length; i++) {
			
			float dx1 = Math.abs(r[i].x - cx);			 														
			float dx2 = Math.abs((r[i].x + r[i].width) - cx);			 							
			
			float dy1 = Math.abs(r[i].y - cy);			 														
			float dy2 = Math.abs((r[i].y + r[i].height) - cy);			 							

			maxX = Math.max(maxX, Math.max(dx1, dx2));
			maxY = Math.max(maxY, Math.max(dy1, dy2));
		}
		if (maxX / maxY > ratio) // maxX decides
			maxY = maxX / ratio;
		else if (maxX / maxY < ratio) // maxY decides
			maxX = maxY / ratio;
		
		return new CEllipse(cx, cy, maxX * 2, maxY * 2);
	}
	
	public static CEllipse boundingEllipseOrig(Rectangle[] r, int cx, int cy) {

		float maxRadiusSoFar = 0;
		for (int i = 0; i < r.length; i++) {
			
			float d1 = dist(r[i].x, r[i].y, cx, cy); 														// upper-left
			float d2 = dist(r[i].x + r[i].width, r[i].y, cx, cy); 							// upper-right
			float d3 = dist(r[i].x, r[i].y + r[i].height, cx, cy); 							// lower-left
			float d4 = dist(r[i].x + r[i].width, r[i].y + r[i].height, cx, cy); // lower-right
			float maxOfCorners = Math.max(Math.max(d1, d2), Math.max(d3, d4));
			maxRadiusSoFar = Math.max(maxRadiusSoFar, maxOfCorners);
		}

		return new CEllipse(cx, cy, maxRadiusSoFar * 2, maxRadiusSoFar * 2);
	}
	
	public static CEllipse boundingEllipse(Rectangle[] r, int cx, int cy, float ratio) {

		Rect rect = alignedBoundingRect(r, cx, cy);
		float diam = boundingCircle(r, cx, cy);
		Pt[] p = rect.intersectsCircle(cx, cy, diam / 2f);
		if (p.length == 8) {
			if (ratio > 1)
				rect = Rect.fromCorners(p[0], p[4]);
			else if (ratio < 1)
				rect = Rect.fromCorners(p[7], p[3]);
		}
		else {
			//throw new RuntimeException("invalid state");
			//System.out.println(p.length + " intersection pts");
		}

		// ellipse.width = sqrt(rect.width^2 + ratio^2 * rect.height^2)
		// double ew = Math.sqrt((rect.width * rect.width) + (ratio * ratio) *
		// (rect.height * rect.height));
		double ew = Math.sqrt((rect.width * rect.width) + (ratio * ratio) * (rect.height * rect.height));
		double eh = ew / ratio;

		return new CEllipse(cx, cy, ew, eh);
	}
	
	public static Rect alignedBoundingRect(Rectangle[] r, int cx, int cy) {

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
	
	public static Rect boundingRect(Rectangle[] r) {

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, 
				maxX = -Integer.MAX_VALUE, maxY = -Integer.MAX_VALUE;

		for (int i = 0; i < r.length; i++) {
			minX = Math.min(minX, r[i].x);
			minY = Math.min(minY, r[i].y);
			maxX = Math.max(maxX, r[i].x + r[i].width);
			maxY = Math.max(maxY, r[i].y + r[i].height);
		}

		int w = Math.round(maxX - minX);
		int h = Math.round(maxY - minY);

		return new Rect(minX, minY, w, h);
	}
	
	public static float boundingCircle(Rectangle[] r, int cx, int cy) { // not used

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

	public static float dist(float x1, float y1, float x2, float y2) {

		return (float) Math.sqrt(sq(x2 - x1) + sq(y2 - y1));
	}

	static float sq(float f) {
		return (f * f);
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

			if (Math.random() < .01) {
				w = 300;
				h = 600;
			}
			if (Math.random() < .02) {
				w = 728;
				h = 90;
			}
			r[i] = new Rectangle(Integer.MAX_VALUE, 0, w, h);
		}
		return r;
	}

	static Rectangle[] testSetFixed(int num) {

		Rectangle[] r = new Rectangle[num];
		for (int i = 0; i < r.length; i++) {
			int idx = (int) (Math.random() * AdSizes.length / 2) * 2;
			int w = AdSizes[idx];
			int h = AdSizes[idx + 1];
			// System.out.println(w+"x"+h);
			r[i] = new Rectangle(Integer.MAX_VALUE, 0, w, h);
		}
		return r;
	}

	static float maxEdge(Rectangle r) {

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
			System.err.println("WARN: "+e.getMessage());
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
    }
    finally {
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

	public static CIRect[] loadIRects(String path) {
		
		return loadIRects(path, -1);
	}

	public static CIRect[] loadIRects(String path, int maxNum) {
		
		File[] ifs = CPU.imageFiles(path, maxNum);
		List<CIRect> pl = new ArrayList<CIRect>();
		
		for (int i = 0; i < ifs.length; i++) {
		
			CIRect pi = null;
			try {
				
				byte bytes[] = loadBytes(ifs[i]);
				if (bytes == null) throw new RuntimeException("NO BYTES FOR: "+ifs[i]);
				pi = new CIRect(new javax.swing.ImageIcon(bytes).getImage(), ifs[i].getName());
			} 
			catch (Exception e) {
				System.err.println("[WARN] " + e.getMessage());
			}
			if (pi != null) pl.add(pi);
		}
		
		return pl.toArray(new CIRect[pl.size()]);
	}

	
	// ///////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		
		// String[] files = imageNames("/Users/dhowe/Desktop/AdCrawl1");
		File[] files = imageFiles("/Users/dhowe/Desktop/AdCrawl1");
		System.out.println("Loaded "+files.length);

	}

}
