package cg.scene;

import cg.math.Vector3D;
import cg.utils.Color;

public class ColorSample {

	private final Vector3D shadowRayDir;
	public final Color diffuse;
	public final Color specular;

	public ColorSample(Color diffuse, Color specular, Vector3D v) {
		super();
		this.diffuse = diffuse;
		this.specular = specular;
		shadowRayDir = v;
	}

	public final float dotProduct(final Vector3D v) {
		return shadowRayDir.x * v.x + shadowRayDir.y * v.y + shadowRayDir.z * v.z;
	}
}
