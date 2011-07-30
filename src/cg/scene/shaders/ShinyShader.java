package cg.scene.shaders;

import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.LightManager;
import cg.utils.Color;

public class ShinyShader extends Shader {
	protected Color colorRGB;
	protected LightManager lm;
	protected float refl ;
	
	public ShinyShader(String name, String type, Color colorRGB, float refl, LightManager lm) {
		super(name, type);
		this.colorRGB = colorRGB;
		this.lm = lm;
		this.refl = refl;
	}

	public Color getColorRGB() {
		return colorRGB;
	}

	@Override
	public String toString() {
		return "*SHINY SHADER: " + name + "(" + colorRGB + ")";
	}

	@Override
	public Color getPointColor(Collision collision) {
		Vector3D n = collision.normal;
		Ray ray = collision.ray;
		Vector3D rd = ray.d;
		
		float cos = Math.max(-(rd.x * n.x + rd.y * n.y + rd.z * n.z), 0.0f);
        float dn = 2.0f * cos;
        
        Vector3D refDir = new Vector3D((dn * n.x) + rd.x,
        		(dn * n.y) + rd.y,
        		(dn * n.z) + rd.z);
        
        Ray refRay = new Ray(collision.hitPoint, refDir, ray.getDepthReflection() + 1, ray.getDepthRefraction());
        
        Color d = getColorRGB();
        
        // Get color samples from lights
		this.lm.addLightSamples(collision);
        Color lr = collision.getDiffuse(d);
        
        Color ret = new Color(1.0f,1.0f,1.0f);
        
        cos = 1.0f - cos;
        float cos2 = cos * cos;
        float cos5 = cos2 * cos2 * cos;

        Color r = d.clone().mul(refl);
        
        ret.sub(r);
        
        return lr.add(ret.mul(cos5).add(r).mul(traceReflection(refRay)));
	}
}
