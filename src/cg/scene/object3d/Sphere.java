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

	private Point3D pos;
	private float radius;
	private float radiusSq;
	private Shader shader;

	// Texturing
	private Matrix4 transform;
	private Vector3D vToCenter;

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
	public Point3D getHitPoint(Ray r) {

		Vector3D vToP = new Vector3D( pos, r.p );

		float b = 2.0f * (r.d.x * vToP.x + r.d.y * vToP.y + r.d.z * vToP.z);

		float disc = b * b - 4.0f * (vToP.x * vToP.x + vToP.y * vToP.y + vToP.z * vToP.z - radiusSq);

		if ( disc < 0.0f ) {
			return null;	// No real solutions => no intersection
		}

		float distSqrt = (float) Math.sqrt(disc);
	    float q;
	    b *= -1.0f;

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

	public void setPos(Point3D pos) {
		this.pos = pos;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public Color getColor(Collision collision) {
		return shader.getPointColor(collision);
	}

	@Override
	public String toString() {
		return "SPHERE: *pos: " + pos + " *radius: " + radius;
	}

	@Override
	public Vector3D getNormal(Point3D p) {

		return new Vector3D(pos, p).normalize();
	}

	@Override
	public Point2D getUV(Collision collision) {

		Vector3D n;

		if ( transform != null ) {
			n = transform.transform(collision.normal.clone()).substract(vToCenter).normalize();
		} else {
			n = collision.normal;
		}

        float phi = (float) Math.atan2(n.y, n.x);
        if (phi < 0.0f) {
			phi += TWO_PI;
		}
        float theta = (float) Math.acos(n.z);

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
