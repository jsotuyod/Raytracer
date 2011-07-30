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

public class Box implements Object3D {

	protected Shader shader;
	protected Vector3D[] normals = new Vector3D[6];
	protected float[] D = new float[6];
	protected Point3D pos;
	protected Point3D bottomLeftBack, topRightFront;
	protected Point3D bottomLeftFront, topRightBack;
	protected Point3D bottomRightBack, topLeftFront;
	protected Point3D bottomRightFront, topLeftBack;
	
	public Box(Point3D bottomLeftBack, Point3D topRightFront, Point3D bottomLeftFront,
			Point3D topRightBack, Point3D bottomRightBack, Point3D topLeftFront,
			Point3D bottomRightFront, Point3D topLeftBack, Vector3D up, Vector3D front,
			Shader shader) {
		
		if ( shader == null ) {
			throw new NoShaderException();
		}
		
		up.normalize();
		front.normalize();
		Vector3D right = front.crossProduct(up);
		
		this.normals[0] = up.clone();
		this.normals[1] = front.clone();
		this.normals[2] = right.clone();
		this.normals[3] = up.scale(-1);
		this.normals[4] = front.scale(-1);
		this.normals[5] = right.scale(-1);
		
		this.pos = new Point3D( ( topRightFront.x + bottomLeftBack.x ) * 0.5f,
				( topRightFront.y + bottomLeftBack.y ) * 0.5f,
				( topRightFront.z + bottomLeftBack.z ) * 0.5f );
		
		Vector3D u = new Vector3D(topRightFront);
		Vector3D v = new Vector3D(bottomLeftBack);
		
		for ( int i = 0; i < 3; i++ ) {
			this.D[i] = this.normals[i].dotProduct(u);
			this.D[i+3] = this.normals[i+3].dotProduct(v);
		}
		
		this.shader = shader;
		
		this.bottomLeftBack = bottomLeftBack;
		this.topRightFront = topRightFront;
		this.bottomLeftFront = bottomLeftFront;
		this.topRightBack = topRightBack;
		this.bottomRightBack = bottomRightBack;
		this.topLeftFront = topLeftFront;
		this.bottomRightFront = bottomRightFront;
		this.topLeftBack = topLeftBack;
	}
	
	@Override
	public Point3D getHitPoint(Ray r) {
		
		float tNear = Float.NEGATIVE_INFINITY;
		float tFar = r.travelledDistance;
		Point3D p = r.p;
		Vector3D d = r.d;
		Vector3D n;
		
		float vd, vn, t;
		
		// Check every plane defining the convex body
		for ( int i = 0 ; i < 6; i++ ) {
			n = normals[i];
			vd = n.x * d.x + n.y * d.y + n.z * d.z;
			vn = D[i] - (n.x * p.x + n.y * p.y + n.z * p.z);
			
			if ( vd == 0.0f ) {
				if ( vn < 0.0f ) {
					return null;	// ray is parallel to the plane and was casted from outside
				}
			} else {
				t = vn / vd;
				
				if ( vd > 0.0f ) {
					// it's a back-face
					if ( t < 0.0f ) {
						return null;	// Polyhedron is behind the ray
					}
					
					if ( t < tFar ) {
						tFar = t;
					}
				} else {
					// it's a front-face
					if ( t > tNear ) {
						tNear = t;
					}
				}
				
				if ( tNear > tFar ) {
					return null;
				}
			}
		}
		
		if ( tNear < 0.0f ) {
			tNear = tFar;
		}
		
		r.travelledDistance = tNear;
		return r.p.translateNew(r.d, tNear);
	}

	@Override
	public Color getColor(Collision collision) {
		return this.shader.getPointColor(collision);
	}
	
	@Override
	public String toString() {
		StringBuffer s =  new StringBuffer("BOX *normals: ");
		for (int i = 0; i < normals.length; i++) {
			s.append(normals[i]);
		}
		s.append(" *D: ");
		for (int i = 0; i < D.length; i++) {
			s.append(D[i]+ " ");
		}
		return s.toString();
	}

	@Override
	public Point3D getPos() {
		return this.pos;
	}

	@Override
	public Vector3D getNormal(Point3D p) {
		
		for ( int i = 0; i < 6; i++ ) {
			if ( Math.abs( normals[i].dotProduct(p) - D[i] ) <= 0.00001f ) {
				return normals[i].clone();
			}
		}
		
		// Should never happen....
		return null;
	}

	@Override
	public Point2D getUV(Collision collision) {
		// Box can't be textured
		return new Point2D(0,0);
	}

	@Override
	public float getMaxXCoord() {
		float max = this.bottomLeftBack.x;
		
		if ( this.bottomLeftFront.x > max ) {
			max = this.bottomLeftFront.x;
		}
		
		if ( this.bottomRightBack.x > max ) {
			max = this.bottomRightBack.x;
		}
		
		if ( this.bottomRightFront.x > max ) {
			max = this.bottomRightFront.x;
		}
		
		if ( this.topLeftBack.x > max ) {
			max = this.topLeftBack.x;
		}
		
		if ( this.topLeftFront.x > max ) {
			max = this.topLeftFront.x;
		}
		
		if ( this.topRightBack.x > max ) {
			max = this.topRightBack.x;
		}
		
		if ( this.topRightFront.x > max ) {
			max = this.topRightFront.x;
		}
		
		return max;
	}

	@Override
	public float getMaxYCoord() {
		float max = this.bottomLeftBack.y;
		
		if ( this.bottomLeftFront.y > max ) {
			max = this.bottomLeftFront.y;
		}
		
		if ( this.bottomRightBack.y > max ) {
			max = this.bottomRightBack.y;
		}
		
		if ( this.bottomRightFront.y > max ) {
			max = this.bottomRightFront.y;
		}
		
		if ( this.topLeftBack.y > max ) {
			max = this.topLeftBack.y;
		}
		
		if ( this.topLeftFront.y > max ) {
			max = this.topLeftFront.y;
		}
		
		if ( this.topRightBack.y > max ) {
			max = this.topRightBack.y;
		}
		
		if ( this.topRightFront.y > max ) {
			max = this.topRightFront.y;
		}
		
		return max;
	}

	@Override
	public float getMaxZCoord() {
		float max = this.bottomLeftBack.z;
		
		if ( this.bottomLeftFront.z > max ) {
			max = this.bottomLeftFront.z;
		}
		
		if ( this.bottomRightBack.z > max ) {
			max = this.bottomRightBack.z;
		}
		
		if ( this.bottomRightFront.z > max ) {
			max = this.bottomRightFront.z;
		}
		
		if ( this.topLeftBack.z > max ) {
			max = this.topLeftBack.z;
		}
		
		if ( this.topLeftFront.z > max ) {
			max = this.topLeftFront.z;
		}
		
		if ( this.topRightBack.z > max ) {
			max = this.topRightBack.z;
		}
		
		if ( this.topRightFront.z > max ) {
			max = this.topRightFront.z;
		}
		
		return max;
	}

	@Override
	public float getMinXCoord() {
		float min = this.bottomLeftBack.x;
		
		if ( this.bottomLeftFront.x < min ) {
			min = this.bottomLeftFront.x;
		}
		
		if ( this.bottomRightBack.x < min ) {
			min = this.bottomRightBack.x;
		}
		
		if ( this.bottomRightFront.x < min ) {
			min = this.bottomRightFront.x;
		}
		
		if ( this.topLeftBack.x < min ) {
			min = this.topLeftBack.x;
		}
		
		if ( this.topLeftFront.x < min ) {
			min = this.topLeftFront.x;
		}
		
		if ( this.topRightBack.x < min ) {
			min = this.topRightBack.x;
		}
		
		if ( this.topRightFront.x < min ) {
			min = this.topRightFront.x;
		}
		
		return min;
	}

	@Override
	public float getMinYCoord() {
		float min = this.bottomLeftBack.y;
		
		if ( this.bottomLeftFront.y < min ) {
			min = this.bottomLeftFront.y;
		}
		
		if ( this.bottomRightBack.y < min ) {
			min = this.bottomRightBack.y;
		}
		
		if ( this.bottomRightFront.y < min ) {
			min = this.bottomRightFront.y;
		}
		
		if ( this.topLeftBack.y < min ) {
			min = this.topLeftBack.y;
		}
		
		if ( this.topLeftFront.y < min ) {
			min = this.topLeftFront.y;
		}
		
		if ( this.topRightBack.y < min ) {
			min = this.topRightBack.y;
		}
		
		if ( this.topRightFront.y < min ) {
			min = this.topRightFront.y;
		}
		
		return min;
	}

	@Override
	public float getMinZCoord() {
		float min = this.bottomLeftBack.z;
		
		if ( this.bottomLeftFront.z < min ) {
			min = this.bottomLeftFront.z;
		}
		
		if ( this.bottomRightBack.z < min ) {
			min = this.bottomRightBack.z;
		}
		
		if ( this.bottomRightFront.z < min ) {
			min = this.bottomRightFront.z;
		}
		
		if ( this.topLeftBack.z < min ) {
			min = this.topLeftBack.z;
		}
		
		if ( this.topLeftFront.z < min ) {
			min = this.topLeftFront.z;
		}
		
		if ( this.topRightBack.z < min ) {
			min = this.topRightBack.z;
		}
		
		if ( this.topRightFront.z < min ) {
			min = this.topRightFront.z;
		}
		
		return min;
	}
}
