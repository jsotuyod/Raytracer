package cg.scene.shaders;

import cg.raycasting.Collision;
import cg.scene.LightManager;
import cg.utils.Color;

public class ConstantShader extends Shader {
	protected Color colorRGB;
	protected LightManager lm;
	
	public ConstantShader(String name, String type, Color colorRGB, LightManager lm) {
		super(name, type);
		this.colorRGB = colorRGB;
		this.lm = lm;
	}

	public Color getColorRGB( Collision collision ) {
		return this.colorRGB;
	}

	@Override
	public String toString() {
		return "*CONSTANT SHADER: " + name + "(" + colorRGB + ")";
	}

	@Override
	public Color getPointColor(Collision collision) {
		return this.colorRGB;
	}
}
