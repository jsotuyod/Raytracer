package cg.scene.shaders;

import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.LightManager;
import cg.utils.Color;

public class MirrorShader extends Shader {
	protected Color colorRGB;
	protected LightManager lm;
	
	public MirrorShader(String name, String type, Color colorRGB, LightManager lm) {
		super(name, type);
		this.colorRGB = colorRGB;
		this.lm = lm;
	}

	public Color getColorRGB() {
		return colorRGB;
	}

	@Override
	public String toString() {
		return "*MIRROR SHADER: " + name + "(" + colorRGB + ")";
	}

	@Override
	public Color getPointColor(Collision collision) {
		Vector3D n = collision.normal;
		Vector3D d = collision.ray.d;
		
		float cos = Math.max(-(d.x * n.x + d.y * n.y + d.z * n.z), 0.0f);
        float dn = 2.0f * cos;
        
        Vector3D refDir = new Vector3D((dn * n.x) + d.x,
        		(dn * n.y) + d.y,
        		(dn * n.z) + d.z);
        
        Ray refRay = new Ray(collision.hitPoint, refDir, collision.ray.getDepthReflection() + 1, collision.ray.getDepthRefraction());

        // compute Fresnel term
        cos = 1.0f - cos;
        float cos2 = cos * cos;
        float cos5 = cos2 * cos2 * cos;
        
        Color c = new Color(1.0f,1.0f,1.0f);
        c.sub(colorRGB);
        return c.mul(cos5).add(colorRGB).mul(traceReflection(refRay));
	}
}
