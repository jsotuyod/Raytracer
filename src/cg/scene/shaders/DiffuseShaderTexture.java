package cg.scene.shaders;

import java.io.File;

import cg.math.Point2D;
import cg.raycasting.Collision;
import cg.scene.LightManager;
import cg.utils.Color;
import cg.utils.ImageBuffer;

public class DiffuseShaderTexture extends DiffuseShader {
	protected String textureFilePath;
	protected ImageBuffer textureImage;

	public DiffuseShaderTexture(String name, String type, String textureFilePath, LightManager lm) {
		super(name, type, null, lm);
		this.textureFilePath = textureFilePath;
		textureImage = new ImageBuffer(new File(textureFilePath));
	}

	public String getTextureFilePath() {
		return textureFilePath;
	}

	@Override
	public String toString() {
		return "*DIFFUSE TEXTURE SHADER: " + name + " Texture File Path: " + textureFilePath;
	}

	@Override
	public Color getPointColor(Collision collision) {

		// Get color samples from lights
		lm.addLightSamples(collision);

		Point2D uv = collision.getUV();
		Color color = textureImage.getTextureColor(uv.x, uv.y);

		return collision.getDiffuse(color);
	}
}
