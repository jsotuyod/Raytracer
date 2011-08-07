package cg.scene.shaders;

import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.utils.Color;

public class MirrorShader extends Shader {
	private final Color colorRGB;
	private final Color oppositeColor;

	public MirrorShader(String name, String type, Color colorRGB) {
		super(name, type);
		this.colorRGB = colorRGB;

		oppositeColor = colorRGB.clone().opposite();
	}

	@Override
	public String toString() {
		return "*MIRROR SHADER: " + name + "(" + colorRGB + ")";
	}

	@Override
	public Color getPointColor(Collision collision) {
		final Vector3D n = collision.normal;
		final Vector3D d = collision.ray.d;

		final float cos = Math.max(-(d.x * n.x + d.y * n.y + d.z * n.z), 0.0f);
		final float dn = 2.0f * cos;

		final Vector3D refDir = new Vector3D((dn * n.x) + d.x,
        		(dn * n.y) + d.y,
        		(dn * n.z) + d.z);

		final Ray refRay = new Ray(collision.hitPoint, refDir, collision.ray.depthReflection + 1, collision.ray.depthRefraction);

        // compute Fresnel term
		final float ocos = 1.0f - cos;
        final float ocos2 = ocos * ocos;
        final float ocos5 = ocos2 * ocos2 * ocos;

        Color c = oppositeColor.clone();
        return c.mul(ocos5).add(colorRGB).mul(traceReflection(refRay));
	}
}
