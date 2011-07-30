package cg.scene;

import cg.math.Vector3D;
import cg.utils.Color;

public class ColorSample {

	protected Vector3D shadowRayDir;
	protected Color diffuse;
	protected Color specular;
	
	public ColorSample(Color diffuse, Color specular, Vector3D v) {
		super();
		this.diffuse = diffuse;
		this.specular = specular;
		this.shadowRayDir = v;
	}

	public Color getDiffuse() {
		return diffuse;
	}

	public Color getSpecular() {
		return specular;
	}
	
	public float dotProduct(Vector3D v) {
		return shadowRayDir.x * v.x + shadowRayDir.y * v.y + shadowRayDir.z * v.z;
	}
}
