package cg.scene.shaders;

import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.utils.Color;
import cg.utils.Registry;

public abstract class Shader {
	
	private static final int MAX_REFLECTION_DEPTH = 3;
	private static final int MAX_REFRACTION_DEPTH = 3;
	
	protected String name;
	protected String type;

	public Shader(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}
	
	public abstract Color getPointColor(Collision collision);
	
	protected Color traceRefraction(Ray refRay) {
		// limit path depth and disable caustic paths
		if (refRay.getDepthRefraction() >= MAX_REFRACTION_DEPTH ) { //|| previous.getDiffuseDepth() > 0)
			return Color.BLACK;
		}
		Collision col = Registry.getScene().castRay(refRay);
		return col != null ? col.object.getColor(col) : Color.BLACK;
	}
	
	protected Color traceReflection(Ray refRay) {
	     // limit path depth and disable caustic paths
       if (refRay.getDepthReflection() >= MAX_REFLECTION_DEPTH ) { //|| previous.getDiffuseDepth() > 0)
           return Color.BLACK;
       }
       Collision col = Registry.getScene().castRay(refRay);
       return col != null ? col.object.getColor(col) : Color.BLACK;
	}
}
