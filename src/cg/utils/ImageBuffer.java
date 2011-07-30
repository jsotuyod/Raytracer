package cg.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cg.exceptions.InvalidImagePathExeption;


public class ImageBuffer {
	
	protected int width, height;
	protected int[][] data;
	
	public ImageBuffer(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		
		this.data = new int[width][height];
	}
	
	public ImageBuffer(File f){
		try {
			BufferedImage bi = ImageIO.read(f);
			this.width = bi.getWidth();
			this.height = bi.getHeight();
			this.data = new int[this.width][this.height];
			this.setImageData(bi);
		} catch (IOException e) {
			System.err.println("Image: " + f.getAbsolutePath() + " does not exist!");
			throw new InvalidImagePathExeption();
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public void setRgbColor(int x, int y, int rgb) {
		
		data[x][y] = rgb;
	}
	
	public int getRgbColor(int x, int y) {
		
		return data[x][y];
	}

	public int[][] getImageData() {
		return this.data;
	}

	public void setImageData(BufferedImage bi) {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				data[i][j] = bi.getRGB(i, j);
			}
		}
	}
	
	public Color getTextureColor(float x, float y) {
		// retrieve a pixel color for a texture, using bicubic filtering
		x = x < 0 ? x - (int) x + 1 : x - (int) x;
        y = y < 0 ? y - (int) y + 1 : y - (int) y;
        float dx = x * (width - 1);
        float dy = y * (height - 1);
        int ix0 = (int) dx;
        int iy0 = (int) dy;
        int ix1 = (ix0 + 1) % width;
        int iy1 = (iy0 + 1) % height;
        float u = dx - ix0;
        float v = dy - iy0;
        u = u * u * (3.0f - (2.0f * u));
        v = v * v * (3.0f - (2.0f * v));
        float k00 = (1.0f - u) * (1.0f - v);
        Color c00 = new Color( this.getRgbColor(ix0, iy0) );
        float k01 = (1.0f - u) * v;
        Color c01 = new Color( this.getRgbColor(ix0, iy1) );
        float k10 = u * (1.0f - v);
        Color c10 = new Color( this.getRgbColor(ix1, iy0) );
        float k11 = u * v;
        Color c11 = new Color( this.getRgbColor(ix1, iy1) );
        Color c = new Color()
        			.madd(k00, c00)
        			.madd(k01, c01)
        			.madd(k10, c10)
        			.madd(k11, c11);
        return c;
	}
}
