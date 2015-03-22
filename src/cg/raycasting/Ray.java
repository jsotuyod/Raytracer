package cg.raycasting;

import cg.math.Point3D;
import cg.math.Vector3D;

public class Ray {

	public final Point3D p;
	public final Vector3D d;
	public final Vector3D id;
	public float travelledDistance;

	public final int depthReflection;
	public final int depthRefraction;

	public Ray(final Point3D p, final Vector3D v) {
		this.p = p;
		d = v.normalize();
		id = new Vector3D(1.0f / d.x, 1.0f / d.y, 1.0f / d.z);
		depthReflection = 0;
		depthRefraction = 0;
		travelledDistance = Float.POSITIVE_INFINITY;
	}

	public Ray(final Point3D p, final Vector3D v, final int depthReflection, final int depthRefraction) {
		this.p = p;
		d = v.normalize();
		id = new Vector3D(1.0f / d.x, 1.0f / d.y, 1.0f / d.z);
		this.depthReflection = depthReflection;
		this.depthRefraction = depthRefraction;
		travelledDistance = Float.POSITIVE_INFINITY;
	}

	@Override
	public String toString() {
		return "p: " + p + " - d: " + d;
	}
}
