package cg.scene.shaders;

import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.utils.Color;

public class GlassShader extends Shader {
	private float eta;
	private float ieta;
	private Color colorRGB;
	private float absDistance;
	private float iAbsDistance;
	private Color absColor;
	private Color oAbsColor;

	public GlassShader(String name, String type, float eta, Color colorRGB,
			float absDistance, Color colorAbs) {
		super(name, type);
		this.absDistance = absDistance;
		iAbsDistance = 1.0f / absDistance;
		absColor = colorAbs;

		if (absColor != null) {
			oAbsColor = absColor.clone().opposite();
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
	public Color getPointColor(Collision collision) {

		Vector3D n = collision.normal;
		Vector3D d = collision.ray.d;

        float cos = Math.max(-(d.x * n.x + d.y * n.y + d.z * n.z), 0.0f);
        float neta = collision.isBehind ? eta : ieta;

        float dn = 2.0f * cos;
        Vector3D reflDir = new Vector3D((dn * n.x) + d.x,
        		(dn * n.y) + d.y,
        		(dn * n.z) + d.z);

        // refracted ray
        float arg = 1.0f - (neta * neta * (1.0f - (cos * cos)));
        boolean tir = arg < 0.0f;
        Vector3D refrDir = null;
        if (tir) {
        	refrDir = new Vector3D(0.0f, 0.0f , 0.0f);
        } else {
            float nK = (neta * cos) - (float) Math.sqrt(arg);
            refrDir = new Vector3D((neta * d.x) + (nK * n.x),
            		(neta * d.y) + (nK * n.y),
            		(neta * d.z) + (nK * n.z));
        }

        // compute Fresnel terms
        float cosTheta1 = n.x * reflDir.x + n.y * reflDir.y + n.z * reflDir.z;
        float cosTheta2 = -(n.x * refrDir.x + n.y * refrDir.y + n.z * refrDir.z);

        float pPara = (cosTheta1 - eta * cosTheta2) / (cosTheta1 + eta * cosTheta2);
        float pPerp = (eta * cosTheta1 - cosTheta2) / (eta * cosTheta1 + cosTheta2);
        float kr = 0.5f * (pPara * pPara + pPerp * pPerp);
        float kt = 1.0f - kr;

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
        Color ret = new Color();
        if (!tir) {
            ret.madd(kt, traceRefraction(new Ray(collision.hitPoint.translateNew(n, -0.1f), refrDir, collision.ray.depthReflection, collision.ray.depthRefraction + 1))).mul(colorRGB);
        }
        if (tir || !collision.isBehind) {
        	ret.madd(kr, traceReflection(new Ray(collision.hitPoint, reflDir, collision.ray.depthReflection + 1, collision.ray.depthRefraction))).mul(colorRGB);
        }
        return absorbtion != null ? ret.mul(absorbtion) : ret;
	}
}
