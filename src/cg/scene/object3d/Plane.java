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

	protected Shader shader;
	protected Vector3D n;
	protected Vector3D uAxis;
	protected Vector3D vAxis;
	protected Point3D p;
	protected float D;
	
	public Plane( Point3D p1, Point3D p2, Point3D p3, Shader shader ) {
		this( p3, new Vector3D( p2, p1 ).crossProduct( new Vector3D( p3, p2 ) ), shader );
	}
	
	public Plane( Point3D p, Vector3D n, Shader shader ) {
		
		if ( shader == null ) {
			throw new NoShaderException();
		}
		
		this.p = p.clone();
		this.n = n.clone().normalize();
		this.shader = shader;
		
		// prevent uAxis from being colineal with normal
		Vector3D uAxis = new Vector3D( 0.0f, 1.0f, 1.0f + this.n.z );
		
		this.vAxis = uAxis.crossProduct(this.n).normalize();
		this.uAxis = this.n.crossProduct(this.vAxis);
		
		// Precompute D factor of the general equation.
		this.D = this.n.dotProduct( this.p );
	}
	
	@Override
	public Color getColor(Collision collision) {
		return this.shader.getPointColor(collision);
	}

	@Override
	public Point3D getHitPoint(Ray r) {
		
		float aux = n.x * r.d.x + n.y * r.d.y + n.z * r.d.z;
		
		if (aux == 0.0f) {
			return null;	// Parallel to the plane (or on it), it's a miss
		}
		
		float t = (D - n.x * r.p.x - n.y * r.p.y - n.z * r.p.z) / aux;
		
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
		return this.p;
	}

	@Override
	public Vector3D getNormal(Point3D p) {
		return this.n;
	}

	@Override
	public Point2D getUV(Collision collision) {
		float u, v;
		Point3D p = collision.hitPoint;
		
		if ( this.uAxis.x != 0.0f ) {
			u = ( ( p.x - this.p.x ) / this.uAxis.x ) % 1.0f;
		} else if ( this.uAxis.y != 0.0f ) {
			u = ( ( p.y - this.p.y ) / this.uAxis.y ) % 1.0f;
		} else {
			u = ( ( p.z - this.p.z ) / this.uAxis.z ) % 1.0f;
		}
		
		if ( u < 0.0f ) {
			u += 1.0f;
		}
		
		
		if ( this.vAxis.x != 0.0f ) {
			v = ( ( p.x - this.p.x ) / this.vAxis.x ) % 1.0f;
		} else if ( this.vAxis.y != 0.0f ) {
			v = ( ( p.y - this.p.y ) / this.vAxis.y ) % 1.0f;
		} else {
			v = ( ( p.z - this.p.z ) / this.vAxis.z ) % 1.0f;
		}
		
		if ( v < 0.0f ) {
			v += 1.0f;
		}
        
		return new Point2D( u, v );
	}

	@Override
	public float getMaxXCoord() {
		if ( this.n.x != 1.0f )
			return Float.POSITIVE_INFINITY;
		
		return this.p.x;
	}

	@Override
	public float getMinXCoord() {
		if ( this.n.x != 1.0f )
			return Float.NEGATIVE_INFINITY;
		
		return this.p.x;
	}

	@Override
	public float getMaxYCoord() {
		if ( this.n.y != 1.0f )
			return Float.POSITIVE_INFINITY;
		
		return this.p.y;
	}

	@Override
	public float getMinYCoord() {
		if ( this.n.y != 1.0f )
			return Float.NEGATIVE_INFINITY;
		
		return this.p.y;
	}

	@Override
	public float getMaxZCoord() {
		if ( this.n.z != 1.0f )
			return Float.POSITIVE_INFINITY;
		
		return this.p.z;
	}

	@Override
	public float getMinZCoord() {
		if ( this.n.z != 1.0f )
			return Float.NEGATIVE_INFINITY;
		
		return this.p.z;
	}
}
