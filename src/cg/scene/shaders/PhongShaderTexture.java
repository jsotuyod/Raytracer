package cg.scene.shaders;

import cg.raycasting.Collision;
import cg.scene.LightManager;
import cg.utils.Color;

public class PhongShaderTexture extends Shader {

	protected String textureFilePath;
	protected Color colorRGBSpec;
	protected float specPower;
	protected int samples;
	protected LightManager lm;

	public PhongShaderTexture(String name, String type, String textureFilePath, 
			Color colorRGBSpec, float specPower, int samples, LightManager lm) {
		super(name, type);
		this.textureFilePath = textureFilePath;
		this.colorRGBSpec = colorRGBSpec;
		this.lm = lm;
		this.samples = samples;
		this.specPower = specPower;
	}

	public String getTextureFilePath() {
		return textureFilePath;
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
		return "*PHONG SHADER: " + name + " File Path: " + textureFilePath + " Spec: " + colorRGBSpec;
	}
}
