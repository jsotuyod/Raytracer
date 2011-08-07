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
		return name;
	}

	public String getType() {
		return type;
	}

	public abstract Color getPointColor(Collision collision);

	protected final Color traceRefraction(final Ray refRay) {
		// limit path depth and disable caustic paths
		if (refRay.depthRefraction >= MAX_REFRACTION_DEPTH ) { //|| previous.getDiffuseDepth() > 0)
			return Color.BLACK;
		}
		final Collision col = Registry.getScene().castRay(refRay);
		return col != null ? col.object.getColor(col) : Color.BLACK;
	}

	protected final Color traceReflection(final Ray refRay) {
	     // limit path depth and disable caustic paths
       if (refRay.depthReflection >= MAX_REFLECTION_DEPTH ) { //|| previous.getDiffuseDepth() > 0)
           return Color.BLACK;
       }
       final Collision col = Registry.getScene().castRay(refRay);
       return col != null ? col.object.getColor(col) : Color.BLACK;
	}
}
