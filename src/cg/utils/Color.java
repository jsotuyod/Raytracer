package cg.utils;


public class Color implements Cloneable {

	public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);

	private final static float I_MAX_VALUE = 1.0f / 255.0f;

	private float r, g, b;

	public Color() {
	}

	public Color(final int rgb) {
        r = ((rgb >> 16) & 0xFF) * I_MAX_VALUE;
        g = ((rgb >> 8) & 0xFF) * I_MAX_VALUE;
        b = (rgb & 0xFF) * I_MAX_VALUE;
    }

	public Color(final float r, final float g, final float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public final Color toNonLinear() {
        r = RGBSpace.SRGB.gammaCorrect(r);
        g = RGBSpace.SRGB.gammaCorrect(g);
        b = RGBSpace.SRGB.gammaCorrect(b);
        return this;
    }

    public final Color toLinear() {
        r = RGBSpace.SRGB.ungammaCorrect(r);
        g = RGBSpace.SRGB.ungammaCorrect(g);
        b = RGBSpace.SRGB.ungammaCorrect(b);
        return this;
    }

    @Override
	public Color clone() {
    	return new Color(r, g, b);
    }

    public final int toRGB() {
        int ir = (int) (r * 255.0f + 0.5f);
        int ig = (int) (g * 255.0f + 0.5f);
        int ib = (int) (b * 255.0f + 0.5f);
        ir = clamp(ir, 0, 255);
        ig = clamp(ig, 0, 255);
        ib = clamp(ib, 0, 255);
        return (ir << 16) | (ig << 8) | ib;
    }

    private final int clamp(final int x, final int min, final int max) {
        if (x > max) {
			return max;
		}
        if (x > min) {
			return x;
		}
        return min;
    }

    public final Color mul(final float s) {
        r *= s;
        g *= s;
        b *= s;
        return this;
    }

    @Override
	public String toString() {
        return String.format("(%.3f, %.3f, %.3f)", r, g, b);
    }


    public final boolean isBlack() {
        return r <= 0.0f && g <= 0.0f && b <= 0.0f;
    }

    public final Color madd(final float s, final Color c) {
        r += (s * c.r);
        g += (s * c.g);
        b += (s * c.b);
        return this;
    }

    public final Color add(final Color c) {
        r += c.r;
        g += c.g;
        b += c.b;
        return this;
    }

    public final Color mul(final Color c) {
        r *= c.r;
        g *= c.g;
        b *= c.b;
        return this;
    }

	public final void sub(final Color c) {
		r -= c.r;
        g -= c.g;
        b -= c.b;
	}

	public final Color opposite() {
        r = 1.0f - r;
        g = 1.0f - g;
        b = 1.0f - b;
        return this;
    }

	public final Color exp() {
        r = (float) Math.exp(r);
        g = (float) Math.exp(g);
        b = (float) Math.exp(b);
        return this;
    }
}
