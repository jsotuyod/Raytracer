package cg.scene;

public class ImageData {
	
	protected int resX, resY;
	protected int minAA, maxAA;
	protected int samples;
	protected float contrast;
	protected String filter;
	protected boolean jitter;
	
	public ImageData(float contrast, String filter, boolean jitter, int maxAA,
			int minAA, int resX, int resY, int samples) {
		super();
		this.contrast = contrast;
		this.filter = filter;
		this.jitter = jitter;
		this.maxAA = maxAA;
		this.minAA = minAA;
		this.resX = resX;
		this.resY = resY;
		this.samples = samples;
	}
	
	public int getResX() {
		return resX;
	}
	public int getResY() {
		return resY;
	}
	public int getMinAA() {
		return minAA;
	}
	public int getMaxAA() {
		return maxAA;
	}
	public int getSamples() {
		return samples;
	}
	public float getContrast() {
		return contrast;
	}
	public String getFilter() {
		return filter;
	}
	public boolean isJitter() {
		return jitter;
	}
	
	@Override
	public String toString() {
		return "IMAGEDATA *resX: " + resX + " *resY: " + resY + " *minAA: " + minAA + " *maxAA: " + maxAA 
				+ " *samples: " + samples + " *contrast: " + contrast + " *filter: " + filter + " *jitter: " + jitter ;
	}
}
