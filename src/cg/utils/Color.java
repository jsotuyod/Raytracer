package cg.utils;


public class Color implements Cloneable {
	
	public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);
	
	private final static float I_MAX_VALUE = 1.0f / 255.0f;

	private float r, g, b;

	public Color() {
	}
	
	public Color(int rgb) {
        r = ((rgb >> 16) & 0xFF) * I_MAX_VALUE;
        g = ((rgb >> 8) & 0xFF) * I_MAX_VALUE;
        b = (rgb & 0xFF) * I_MAX_VALUE;
    }

	public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color toNonLinear() {
        r = RGBSpace.SRGB.gammaCorrect(r);
        g = RGBSpace.SRGB.gammaCorrect(g);
        b = RGBSpace.SRGB.gammaCorrect(b);
        return this;
    }

    public Color toLinear() {
        r = RGBSpace.SRGB.ungammaCorrect(r);
        g = RGBSpace.SRGB.ungammaCorrect(g);
        b = RGBSpace.SRGB.ungammaCorrect(b);
        return this;
    }

    @Override
	public Color clone() {
    	return new Color(this.r, this.g, this.b);
    }

    public int toRGB() {
        int ir = (int) (r * 255.0f + 0.5f);
        int ig = (int) (g * 255.0f + 0.5f);
        int ib = (int) (b * 255.0f + 0.5f);
        ir = clamp(ir, 0, 255);
        ig = clamp(ig, 0, 255);
        ib = clamp(ib, 0, 255);
        return (ir << 16) | (ig << 8) | ib;
    }
    
    private int clamp(int x, int min, int max) {
        if (x > max)
            return max;
        if (x > min)
            return x;
        return min;
    }

    public final Color mul(float s) {
        r *= s;
        g *= s;
        b *= s;
        return this;
    }

    public String toString() {
        return String.format("(%.3f, %.3f, %.3f)", r, g, b);
    }
 

    public boolean isBlack() {
        return r <= 0.0f && g <= 0.0f && b <= 0.0f;
    }

    public Color madd(float s, Color c) {
        r += (s * c.r);
        g += (s * c.g);
        b += (s * c.b);
        return this;
    }
    
    public Color add(Color c) {
        r += c.r;
        g += c.g;
        b += c.b;
        return this;
    }
    
    public Color mul(Color c) {
        r *= c.r;
        g *= c.g;
        b *= c.b;
        return this;
    }

	public void blend(Color c2, Color b) {
		this.r = (1.0f - b.r) * this.r + b.r * c2.r;
        this.g = (1.0f - b.g) * this.g + b.g * c2.g;
        this.b = (1.0f - b.b) * this.b + b.b * c2.b;
    }
	
	public static Color blend(Color c1, Color c2, float b) {
		float cb = 1.0f - b;
		
		return new Color( cb * c1.r + b * c2.r, cb * c1.g + b * c2.g, cb * c1.b + b * c2.b );
    }

	public void sub(Color c) {
		this.r -= c.r;
        this.g -= c.g;
        this.b -= c.b;
	}
	
	public Color opposite() {
        r = 1.0f - r;
        g = 1.0f - g;
        b = 1.0f - b;
        return this;
    }
	
	public Color exp() {
        r = (float) Math.exp(r);
        g = (float) Math.exp(g);
        b = (float) Math.exp(b);
        return this;
    }
}
