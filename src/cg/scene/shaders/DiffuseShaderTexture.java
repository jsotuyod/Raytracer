package cg.scene.shaders;

import java.io.File;

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
		this.lm = lm;
		this.textureImage = new ImageBuffer(new File(textureFilePath));
	}

	public String getTextureFilePath() {
		return this.textureFilePath;
	}

	@Override
	public String toString() {
		return "*DIFFUSE TEXTURE SHADER: " + name + " Texture File Path: " + textureFilePath;
	}

	@Override
	public Color getColorRGB(Collision collision) {
		return textureImage.getTextureColor(collision.getUV().x, collision.getUV().y);
	}
}
