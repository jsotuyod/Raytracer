package cg.scene.object3d;

import cg.exceptions.NoShaderException;
import cg.math.Matrix4;
import cg.math.Point2D;
import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.Object3D;
import cg.scene.shaders.Shader;
import cg.utils.Color;


public class Sphere implements Object3D {

	//TODO: ver el tema de que puede rotar por las texturas.

	private static final float TWO_PI = 2.0f * (float) Math.PI;
	private static final float I_TWO_PI = 1.0f / TWO_PI;
	private static final float I_PI = 1.0f / (float) Math.PI;

	private final Point3D pos;
	private final float radius;
	private final float radiusSq;
	private final Shader shader;

	// Texturing
	private final Matrix4 transform;
	private final Vector3D vToCenter;

	public Sphere(Point3D pos, float radius, Shader shader, Matrix4 transform) {
		super();

		if ( shader == null ) {
			throw new NoShaderException();
		}

		this.pos = pos;
		this.radius = radius;
		radiusSq = radius * radius;
		this.shader = shader;

		this.transform = transform;
		vToCenter = new Vector3D( pos );
	}

	@Override
	public Point3D getHitPoint(final Ray r) {

		final Vector3D vToP = new Vector3D( pos, r.p );

		float b = -2.0f * (r.d.x * vToP.x + r.d.y * vToP.y + r.d.z * vToP.z);

		final float disc = b * b - 4.0f * (vToP.x * vToP.x + vToP.y * vToP.y + vToP.z * vToP.z - radiusSq);

		if ( disc < 0.0f ) {
			return null;	// No real solutions => no intersection
		}

		final float distSqrt = (float) Math.sqrt(disc);
		final float q;

	    // Get smallest positive collision point
	    if (b > distSqrt) {
	    	q = (b - distSqrt) * 0.5f;
	    } else {
	        q = (b + distSqrt) * 0.5f;

	        if ( q < 0.0f ) {
	        	return null;	// Intersection is behind camera
	        }
	    }

	    if (q > r.travelledDistance) {
	    	// Hit was beyond a previously found hit
	    	return null;
	    }

	    r.travelledDistance = q;
	    return r.p.translateNew(r.d, q);
	}

	@Override
	public Point3D getPos() {
		return pos;
	}

	@Override
	public Color getColor(final Collision collision) {
		return shader.getPointColor(collision);
	}

	@Override
	public String toString() {
		return "SPHERE: *pos: " + pos + " *radius: " + radius;
	}

	@Override
	public Vector3D getNormal(final Point3D p) {

		return new Vector3D(pos, p).normalize();
	}

	@Override
	public Point2D getUV(final Collision collision) {

		final Vector3D n;

		if ( transform != null ) {
			n = transform.transform(collision.normal.clone()).substract(vToCenter).normalize();
		} else {
			n = collision.normal;
		}

        float phi = (float) Math.atan2(n.y, n.x);
        if (phi < 0.0f) {
			phi += TWO_PI;
		}
        final float theta = (float) Math.acos(n.z);

        return new Point2D( phi * I_TWO_PI, theta * I_PI);
	}

	@Override
	public float getMaxXCoord() {
		return pos.x + radius;
	}

	@Override
	public float getMaxYCoord() {
		return pos.y + radius;
	}

	@Override
	public float getMaxZCoord() {
		return pos.z + radius;
	}

	@Override
	public float getMinXCoord() {
		return pos.x - radius;
	}

	@Override
	public float getMinYCoord() {
		return pos.y - radius;
	}

	@Override
	public float getMinZCoord() {
		return pos.z - radius;
	}
}
