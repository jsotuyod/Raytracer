package cg.scene.shaders;

import cg.raycasting.Collision;
import cg.scene.LightManager;
import cg.utils.Color;

public class PhongShader extends Shader {

	protected Color colorRGB;
	protected Color colorRGBSpec;
	protected float specPower;
	protected int samples;
	protected LightManager lm;
	
	public PhongShader(String name, String type, Color colorRGB, 
			Color colorRGBSpec, float specPower, int samples, LightManager lm) {
		super(name, type);
		this.colorRGB = colorRGB;
		this.colorRGBSpec = colorRGBSpec;
		this.lm = lm;
		this.samples = samples;
		this.specPower = specPower;
	}
	
	public Color getColorRGB() {
		return colorRGB;
	}

	public Color getColorRGBSpec() {
		return colorRGBSpec;
	}

	public float getSpecPower() {
		return specPower;
	}

	public int getSamples() {
		return samples;
	}

	public LightManager getLm() {
		return lm;
	}

	@Override
	public Color getPointColor(Collision collision) {
		return null;
	}

	@Override
	public String toString() {
		return "*PHONG SHADER: " + name + " Color: " + colorRGB + " Spec: " + colorRGBSpec;
	}
}
