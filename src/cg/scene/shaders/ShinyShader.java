package cg.scene.shaders;

import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.LightManager;
import cg.utils.Color;

public class ShinyShader extends Shader {
	private final Color colorRGB;
	private final Color reflectionColor;
	private final LightManager lm;

	public ShinyShader(String name, String type, Color colorRGB, float refl, LightManager lm) {
		super(name, type);
		this.colorRGB = colorRGB;
		this.lm = lm;
		reflectionColor = colorRGB.clone().mul(refl);
	}

	@Override
	public String toString() {
		return "*SHINY SHADER: " + name + "(" + colorRGB + ")";
	}

	@Override
	public Color getPointColor(Collision collision) {
		final float cos = Math.max(-(collision.ray.d.x * collision.normal.x + collision.ray.d.y * collision.normal.y + collision.ray.d.z * collision.normal.z), 0.0f);
        final float dn = 2.0f * cos;

        final Vector3D refDir = new Vector3D((dn * collision.normal.x) + collision.ray.d.x,
        		(dn * collision.normal.y) + collision.ray.d.y,
        		(dn * collision.normal.z) + collision.ray.d.z);

        final Ray refRay = new Ray(collision.hitPoint, refDir, collision.ray.depthReflection + 1, collision.ray.depthRefraction);

        // Get color samples from lights
		lm.addLightSamples(collision);
        final Color lr = collision.getDiffuse(colorRGB);

        final Color ret = new Color(1.0f,1.0f,1.0f);

        final float ocos = 1.0f - cos;
        final float ocos2 = ocos * ocos;
        final float ocos5 = ocos2 * ocos2 * ocos;

        ret.sub(reflectionColor);

        return lr.add(ret.mul(ocos5).add(reflectionColor).mul(traceReflection(refRay)));
	}
}
