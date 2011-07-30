package cg.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageBufferIO {

	public enum ImageFormat {
		PNG, BMP
	}
	
	public static void saveToFile(ImageBuffer buffer, File f, ImageFormat format) throws IOException {
		
		int width = buffer.getWidth();
		int height = buffer.getHeight();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for ( int i = 0; i < width; i++ ) {
			for ( int j = 0; j < height; j++ ) {
				img.setRGB(i, j, buffer.getRgbColor(i, j) );
			}
		}
		
		ImageIO.write(img, format.name().toLowerCase(), f);
	}
}
