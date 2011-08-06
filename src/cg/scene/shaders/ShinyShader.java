package cg.scene.shaders;

import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.LightManager;
import cg.utils.Color;

public class ShinyShader extends Shader {
	private final Color colorRGB;
	private final LightManager lm;
	private final float refl ;

	public ShinyShader(String name, String type, Color colorRGB, float refl, LightManager lm) {
		super(name, type);
		this.colorRGB = colorRGB;
		this.lm = lm;
		this.refl = refl;
	}

	@Override
	public String toString() {
		return "*SHINY SHADER: " + name + "(" + colorRGB + ")";
	}

	@Override
	public Color getPointColor(Collision collision) {
		final Vector3D n = collision.normal;
		final Ray ray = collision.ray;
		final Vector3D rd = ray.d;

		final float cos = Math.max(-(rd.x * n.x + rd.y * n.y + rd.z * n.z), 0.0f);
        final float dn = 2.0f * cos;

        final Vector3D refDir = new Vector3D((dn * n.x) + rd.x,
        		(dn * n.y) + rd.y,
        		(dn * n.z) + rd.z);

        final Ray refRay = new Ray(collision.hitPoint, refDir, ray.depthReflection + 1, ray.depthRefraction);

        // Get color samples from lights
		lm.addLightSamples(collision);
        final Color lr = collision.getDiffuse(colorRGB);

        final Color ret = new Color(1.0f,1.0f,1.0f);

        final float ocos = 1.0f - cos;
        final float ocos2 = ocos * ocos;
        final float ocos5 = ocos2 * ocos2 * ocos;

        final Color r = colorRGB.clone().mul(refl);

        ret.sub(r);

        return lr.add(ret.mul(ocos5).add(r).mul(traceReflection(refRay)));
	}
}
