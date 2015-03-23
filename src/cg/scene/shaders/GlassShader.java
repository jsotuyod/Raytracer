package cg.scene.shaders;

import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.utils.Color;

public class GlassShader extends Shader {
	private final float eta;
	private final float ieta;
	private final Color colorRGB;
	private final float absDistance;
	private final float iAbsDistance;
	private final Color absColor;
	private final Color oAbsColor;

	public GlassShader(String name, String type, float eta, Color colorRGB,
			float absDistance, Color colorAbs) {
		super(name, type);
		this.absDistance = absDistance;
		iAbsDistance = 1.0f / absDistance;
		absColor = colorAbs;

		if (absColor != null) {
			oAbsColor = absColor.clone().opposite();
		} else {
			oAbsColor = null;
		}

		this.colorRGB = colorRGB;
		this.eta = eta;
		ieta = 1.0f / eta;
	}

	@Override
	public String toString() {
		return "*GLASS SHADER: " + name + " Color: " + colorRGB + " AbsColor: " + absColor;
	}

	@Override
	public Color getPointColor(final Collision collision) {
		final float cos = Math.max(-(collision.ray.d.x * collision.normal.x + collision.ray.d.y * collision.normal.y + collision.ray.d.z * collision.normal.z), 0.0f);
		final float neta = collision.isBehind ? eta : ieta;

		final float dn = 2.0f * cos;
		final Vector3D reflDir = new Vector3D((dn * collision.normal.x) + collision.ray.d.x,
        		(dn * collision.normal.y) + collision.ray.d.y,
        		(dn * collision.normal.z) + collision.ray.d.z);

        // refracted ray
		final float arg = 1.0f - (neta * neta * (1.0f - (cos * cos)));
		final boolean tir = arg < 0.0f;
		final Vector3D refrDir;
        if (tir) {
        	refrDir = new Vector3D(0.0f, 0.0f , 0.0f);
        } else {
        	final float nK = (neta * cos) - (float) Math.sqrt(arg);
            refrDir = new Vector3D((neta * collision.ray.d.x) + (nK * collision.normal.x),
            		(neta * collision.ray.d.y) + (nK * collision.normal.y),
            		(neta * collision.ray.d.z) + (nK * collision.normal.z));
        }

        // compute Fresnel terms
        final float cosTheta1 = collision.normal.x * reflDir.x + collision.normal.y * reflDir.y + collision.normal.z * reflDir.z;
        final float cosTheta2 = -(collision.normal.x * refrDir.x + collision.normal.y * refrDir.y + collision.normal.z * refrDir.z);

        final float pPara = (cosTheta1 - eta * cosTheta2) / (cosTheta1 + eta * cosTheta2);
        final float pPerp = (eta * cosTheta1 - cosTheta2) / (eta * cosTheta1 + cosTheta2);
        final float kr = 0.5f * (pPara * pPara + pPerp * pPerp);
        final float kt = 1.0f - kr;

        Color absorbtion = null;
        if (collision.isBehind && absDistance > 0.0f) {
            // this ray is inside the object and leaving it
            // compute attenuation that occured along the ray
            absorbtion = oAbsColor.clone().mul(-collision.ray.travelledDistance * iAbsDistance).exp();
            if (absorbtion.isBlack()) {
                return Color.BLACK; // nothing goes through
            }
        }
        // refracted ray
        final Color ret = new Color();
        if (!tir) {
            ret.madd(kt, traceRefraction(new Ray(collision.hitPoint.translateNew(collision.normal, -0.1f), refrDir, collision.ray.depthReflection, collision.ray.depthRefraction + 1))).mul(colorRGB);
        }
        if (tir || !collision.isBehind) {
        	ret.madd(kr, traceReflection(new Ray(collision.hitPoint, reflDir, collision.ray.depthReflection + 1, collision.ray.depthRefraction))).mul(colorRGB);
        }
        return absorbtion != null ? ret.mul(absorbtion) : ret;
	}
}
