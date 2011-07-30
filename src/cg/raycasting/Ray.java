package cg.raycasting;

import cg.math.Point3D;
import cg.math.Vector3D;

public class Ray {

	public Point3D p;
	public Vector3D d;
	public Vector3D id;
	protected int depthReflection;
	protected int depthRefraction;
	public float travelledDistance;
	
	public Ray(Point3D p, Vector3D v) {
		super();
		this.p = p;
		this.d = v.normalize();
		this.id = new Vector3D(1.0f / d.x, 1.0f / d.y, 1.0f / d.z);
		this.depthReflection = 0;
		this.depthRefraction = 0;
		this.travelledDistance = Float.POSITIVE_INFINITY;
	}

	public Ray (Point3D p, Vector3D v, int depthReflection, int depthRefraction) {
		super();
		this.p = p;
		this.d = v.normalize();
		this.id = new Vector3D(1.0f / d.x, 1.0f / d.y, 1.0f / d.z);
		this.depthReflection = depthReflection;
		this.depthRefraction = depthRefraction;
		this.travelledDistance = Float.POSITIVE_INFINITY;
	}

	public int getDepthReflection() {
		return depthReflection;
	}

	public int getDepthRefraction() {
		return depthRefraction;
	}

	@Override
	public String toString() {
		return "p: " + p + " - d: " + d;
	}
}
