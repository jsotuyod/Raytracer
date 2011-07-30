package cg.scene.object3d.boundingvolume;

import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Ray;

public class AABB implements BoundingVolume {
	
	protected Vector3D[] normals = new Vector3D[6];
	protected float[] D = new float[6];
	
	protected Point3D minP, maxP;
	
	public AABB(Point3D minP, Point3D maxP) {
		super();

		this.minP = minP;
		this.maxP = maxP;
		
		this.normals[0] = new Vector3D(0.0f, 1.0f, 0.0f);
		this.normals[1] = new Vector3D(1.0f, 0.0f, 0.0f);
		this.normals[2] = new Vector3D(0.0f, 0.0f, 1.0f);
		this.normals[3] = new Vector3D(0.0f, -1.0f, 0.0f);
		this.normals[4] = new Vector3D(-1.0f, 0.0f, 0.0f);
		this.normals[5] = new Vector3D(0.0f, 0.0f, -1.0f);
		
		Vector3D u = new Vector3D(maxP);
		Vector3D v = new Vector3D(minP);
		
		for ( int i = 0; i < 3; i++ ) {
			D[i] = normals[i].dotProduct(u);
			D[i+3] = normals[i+3].dotProduct(v);
		}
	}
	
	public Point3D getMinP() {
		return minP;
	}

	public Point3D getMaxP() {
		return maxP;
	}

	public boolean hitTest(Ray r) {
		
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
					return false;	// ray is parallel to the plane and was casted from outside
				}
			} else {
				t = vn / vd;
				
				if ( vd > 0.0f ) {
					// it's a back-face
					if ( t < 0.0f ) {
						return false;	// Polyhedron is behind the ray
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
					return false;
				}
			}
		}
		
		return true;
	}
}
