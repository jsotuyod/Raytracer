package cg.raycasting;

import cg.math.Point3D;
import cg.math.Vector3D;

public class Ray {

	public Point3D p;
	public Vector3D d;
	public Vector3D id;
	public float travelledDistance;

	public int depthReflection;
	public int depthRefraction;

	public Ray(Point3D p, Vector3D v) {
		super();
		this.p = p;
		d = v.normalize();
		id = new Vector3D(1.0f / d.x, 1.0f / d.y, 1.0f / d.z);
		depthReflection = 0;
		depthRefraction = 0;
		travelledDistance = Float.POSITIVE_INFINITY;
	}

	public Ray (Point3D p, Vector3D v, int depthReflection, int depthRefraction) {
		super();
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
