package jcp.util;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.*;

public class JAIUtil {
	
	// TODO: move to createMap()
	static Color[][] mapArray = new Color[500][500]; // The array of colors,
																						// representing the map.
																						// This is limiting the map
																						// to 500x500 right now.
																						// I intend to replace this.
	
	public static RenderedImage createMap(RenderedImage baseImage, int hTiles, int vTiles) {

		// Calculate h + w of chunks we are finding the average color for
		int pixelWidth = baseImage.getWidth() / hTiles;
		int pixelHeight = baseImage.getHeight() / vTiles;

		TiledImage newImage = new TiledImage(baseImage, false);

		// Now, cycle through all of the map tile areas we are creating.
		for (int smallY = 0; smallY < vTiles; smallY++) {
			for (int smallX = 0; smallX < hTiles; smallX++) {

				// Calculate the starting pixels for the block.
				int yDim = smallY * pixelHeight;
				int xDim = smallX * pixelWidth;

				try {

					ROIShape myROI = new ROIShape(new Rectangle(xDim, yDim, pixelWidth, pixelHeight));

					// Set the parameters for doing the JAI "mean" calculation.
					ParameterBlock pb = new ParameterBlock();
					pb.addSource(baseImage);
					pb.add(myROI);
					pb.add(1);
					pb.add(1);

					// Find the mean value of the area.
					RenderedImage meanImage = JAI.create("mean", pb, null);
					double[] mean = (double[]) meanImage.getProperty("mean"); 
					
					// This assumes we are using color JPEGs, which have 3 bands of
					// data.. Red, Green, and Blue.
					Color c = new Color((int) mean[0], (int) mean[1], (int) mean[2]);

					// Put that color into the relevant pixels on the new image.
					// There might be a JAI way of doing this, using ROIs.
					for (int x = 0; x < pixelWidth; x++) {
						for (int y = 0; y < pixelHeight; y++) {
							newImage.setSample(xDim + x, yDim + y, 0, c.getRed());
							newImage.setSample(xDim + x, yDim + y, 1, c.getGreen());
							newImage.setSample(xDim + x, yDim + y, 2, c.getBlue());
						}
					}

					// Assign the color to the map array.
					mapArray[(xDim / pixelWidth)][(yDim / pixelHeight)] = c;
					
				} catch (Exception e) {
				}
			}
		}
		
		return newImage; 
	}
	
	public static void main(String[] args) {
		
		// IMPLEMENT ME
	}
}