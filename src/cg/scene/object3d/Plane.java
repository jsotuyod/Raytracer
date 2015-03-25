package cg.scene.object3d;

import cg.exceptions.NoShaderException;
import cg.math.Point2D;
import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.Object3D;
import cg.scene.shaders.Shader;
import cg.utils.Color;

public class Plane implements Object3D {

	private final Shader shader;
	private final Vector3D n;
	private final Vector3D uAxis;
	private final Vector3D vAxis;
	private final Point3D p;
	private final float D;

	public Plane(final Point3D p1, final Point3D p2, final Point3D p3, final Shader shader) {
		this(p3, new Vector3D( p2, p1 ).crossProduct(new Vector3D(p3, p2)), shader);
	}

	public Plane(final Point3D p, final Vector3D n, final Shader shader) {
		if (shader == null) {
			throw new NoShaderException();
		}

		this.p = p.clone();
		this.n = n.clone().normalize();
		this.shader = shader;

		// prevent uAxis from being colineal with normal
		final Vector3D uAxis = new Vector3D(0.0f, 1.0f, 1.0f + this.n.z);

		vAxis = uAxis.crossProduct(this.n).normalize();
		this.uAxis = this.n.crossProduct(vAxis);

		// Precompute D factor of the general equation.
		D = this.n.dotProduct(this.p);
	}

	@Override
	public Color getColor(final Collision collision) {
		return shader.getPointColor(collision);
	}

	@Override
	public Point3D getHitPoint(final Ray r) {
		final float aux = n.x * r.d.x + n.y * r.d.y + n.z * r.d.z;

		if (aux == 0.0f) {
			return null;	// Parallel to the plane (or on it), it's a miss
		}

		final float t = (D - n.x * r.p.x - n.y * r.p.y - n.z * r.p.z) / aux;

		if (t < 0.0f || t > r.travelledDistance) {
			// Hit was behind camera, or further than a previously found hit, ignore it!
			return null;
		}

		r.travelledDistance = t;
		return r.p.translateNew(r.d, t);
	}

	@Override
	public String toString() {
		return "PLANE *n: " + n + " *p: " + p + " *D: " + D;
	}

	@Override
	public Point3D getPos() {
		return p;
	}

	@Override
	public Vector3D getNormal(final Point3D p) {
		return n;
	}

	@Override
	public Point2D getUV(final Collision collision) {
		float u, v;
		final Point3D p = collision.hitPoint;

		if ( uAxis.x != 0.0f ) {
			u = ( ( p.x - this.p.x ) / uAxis.x ) % 1.0f;
		} else if ( uAxis.y != 0.0f ) {
			u = ( ( p.y - this.p.y ) / uAxis.y ) % 1.0f;
		} else {
			u = ( ( p.z - this.p.z ) / uAxis.z ) % 1.0f;
		}

		if ( u < 0.0f ) {
			u += 1.0f;
		}


		if ( vAxis.x != 0.0f ) {
			v = ( ( p.x - this.p.x ) / vAxis.x ) % 1.0f;
		} else if ( vAxis.y != 0.0f ) {
			v = ( ( p.y - this.p.y ) / vAxis.y ) % 1.0f;
		} else {
			v = ( ( p.z - this.p.z ) / vAxis.z ) % 1.0f;
		}

		if ( v < 0.0f ) {
			v += 1.0f;
		}

		return new Point2D( u, v );
	}

	@Override
	public float getMaxXCoord() {
		if ( n.x != 1.0f ) {
			return Float.POSITIVE_INFINITY;
		}

		return p.x;
	}

	@Override
	public float getMinXCoord() {
		if ( n.x != 1.0f ) {
			return Float.NEGATIVE_INFINITY;
		}

		return p.x;
	}

	@Override
	public float getMaxYCoord() {
		if ( n.y != 1.0f ) {
			return Float.POSITIVE_INFINITY;
		}

		return p.y;
	}

	@Override
	public float getMinYCoord() {
		if ( n.y != 1.0f ) {
			return Float.NEGATIVE_INFINITY;
		}

		return p.y;
	}

	@Override
	public float getMaxZCoord() {
		if ( n.z != 1.0f ) {
			return Float.POSITIVE_INFINITY;
		}

		return p.z;
	}

	@Override
	public float getMinZCoord() {
		if ( n.z != 1.0f ) {
			return Float.NEGATIVE_INFINITY;
		}

		return p.z;
	}
}
