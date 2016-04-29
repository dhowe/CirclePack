package jcp.util;

import java.awt.Image;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class Images {

	public static final String GIF_ONLY = "[^_].+\\.gif$";
	public static final String ALL_TYPES = "[^_].+\\.(png|gif|jpe?g)$";
	public static String IMAGE_FILTER_RE = ALL_TYPES;

	public static byte[] loadBytes(InputStream input) {
		
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

	public static byte[] loadBytes(File file) {

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
		return loadIRects(path, new String[0], maxNum);
	}

	public static IRect[] loadIRects(String path, String[] folders, int maxNum) {
		return loadIRects(path, folders, -1, maxNum);
	}

	public static IRect[] loadIRects(String path, String[] folders, int maxPerFolder, int maxNum) {

		File[] ifs = imageFiles(path, folders, maxPerFolder, maxNum);
		List<IRect> pl = new ArrayList<IRect>();

		for (int i = 0; i < ifs.length; i++)
			createIRect(ifs[i], pl);

		return pl.toArray(new IRect[pl.size()]);
	}

	private static void createIRect(File file, List<IRect> result) {

		IRect pi = null;
		try {

			byte bytes[] = loadBytes(file);
			if (bytes == null)
				throw new RuntimeException("NO BYTES FOR: " + file);

			pi = new IRect(new javax.swing.ImageIcon(bytes).getImage(), file.getName());

		} catch (Exception e) {

			System.err.println("[WARN] " + e.getMessage());
		}

		if (pi == null) return;

		result.add(pi);
	}

	public static boolean isAnimated(String file) {
		return isAnimated(new File(file));
	}
		
	public static boolean isAnimated(File file) {
		
		if (!file.getName().toLowerCase().endsWith(".gif"))
			return false;
			
		ImageReader is = ImageIO.getImageReadersBySuffix("GIF").next();  
    ImageInputStream iis = null;
    try {
        iis = ImageIO.createImageInputStream(file);
        is.setInput(iis);  
        return is.getNumImages(true) > 1 ? true : false;
        
    } catch (IOException e) {
    	
       System.err.println(e.getMessage());
    }
    finally {
    	try {
				if (iis!=null) iis.close();
			} catch (IOException e) { }
    }
    
    return false;
	}
	
	public static boolean isImage(File file) {

		return isImage(file.getName(), IMAGE_FILTER_RE);
	}

	public static boolean isImage(Path path, String regex) {

		return isImage(path.toAbsolutePath().toString(), regex);
	}

	public static boolean isImage(String name, String regex) {

		return name.matches(regex);
	}
	
	public static File[] imageFiles(String dir, int maxNum) {

		return imageFiles(dir, -1, maxNum);
	}

	public static File[] imageFiles(String dir, int maxPerFolder, int maxNum) {

		return imageFiles(dir, new String[0], maxPerFolder, maxNum);
	}

	public static File[] imageFiles(String dir, String[] folders, int maxPerFolder, int maxNum) {

		List<File> files = new ArrayList<File>();
		// If we have folders divide equally between them...
		if (folders.length > 0 && maxPerFolder < 0 && maxNum < Integer.MAX_VALUE) {

			maxPerFolder = (int) Math.ceil(maxNum / (float) folders.length);
			System.out.println("Found " + folders.length + " folderNames for total=" + maxNum + ". Setting maxPerFolder="
					+ maxPerFolder);
		}
		imageFiles(files, FileSystems.getDefault().getPath(dir), Arrays.asList(folders), maxPerFolder, maxNum);
		return files.toArray(new File[0]);
	}

	public static void imageFiles(List<File> files, Path dir, List<String> subfolders, int maxPerFolder, int maxTotal) {

		// System.out.println("CALL: imageFiles: "+dir);
		int count = 0, max = maxTotal < 0 ? Integer.MAX_VALUE : maxTotal;
		int maxPer = maxPerFolder < 0 ? Integer.MAX_VALUE : maxPerFolder;

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {

			for (Path path : stream) {

				File file = path.toFile();

				if (path.toFile().isDirectory()) {

					if (subfolders.contains(file.getName()))
						imageFiles(files, path, subfolders, maxPerFolder, max);

				} else {

					if (files.size() >= max)
						break;

					if (isImage(file) && count++ < maxPer) {
						//System.out.println("  adding(" + count + "/" + files.size() + ") -> " + file);
						files.add(file);
					}

					// else System.err.println("SKIP: "+file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Image loadImage(String path) {
		byte bytes[] = loadBytes(new File(path));
		if (bytes == null)
			throw new RuntimeException("NO BYTES FOR: " + path);
		return new javax.swing.ImageIcon(bytes).getImage();
	}
	
	public static void main(String[] args) {

		// String[] files = imageNames("/Users/dhowe/Desktop/AdCollage/AdCrawl1");
		String fname = "/Users/dhowe/Desktop/AdCollage/testAni.gif";
		//Image img = loadImage(fname);
		System.out.println(isAnimated(new File(fname)));
		//System.out.println("Loaded " + img);
	}
}
