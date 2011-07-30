package cg.scene.shaders;

import cg.raycasting.Collision;
import cg.scene.LightManager;
import cg.utils.Color;

public class DiffuseShader extends Shader {
	protected Color colorRGB;
	protected LightManager lm;

	public DiffuseShader(String name, String type, Color colorRGB, LightManager lm) {
		super(name, type);
		this.colorRGB = colorRGB;
		this.lm = lm;
	}

	@Override
	public String toString() {
		return "*DIFFUSE SHADER: " + name + "(" + colorRGB + ")";
	}

	@Override
	public Color getPointColor(Collision collision) {

		// Get color samples from lights
		lm.addLightSamples(collision);

		return collision.getDiffuse(colorRGB);
	}
}
