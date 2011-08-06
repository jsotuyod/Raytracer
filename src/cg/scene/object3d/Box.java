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

	private Shader shader;
	private Vector3D[] normals = new Vector3D[6];
	private float[] D = new float[6];
	private Point3D pos;
	private Point3D bottomLeftBack, topRightFront;
	private Point3D bottomLeftFront, topRightBack;
	private Point3D bottomRightBack, topLeftFront;
	private Point3D bottomRightFront, topLeftBack;

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

		normals[0] = up.clone();
		normals[1] = front.clone();
		normals[2] = right.clone();
		normals[3] = up.scale(-1.0f);
		normals[4] = front.scale(-1.0f);
		normals[5] = right.scale(-1.0f);

		pos = new Point3D( ( topRightFront.x + bottomLeftBack.x ) * 0.5f,
				( topRightFront.y + bottomLeftBack.y ) * 0.5f,
				( topRightFront.z + bottomLeftBack.z ) * 0.5f );

		Vector3D u = new Vector3D(topRightFront);
		Vector3D v = new Vector3D(bottomLeftBack);

		for ( int i = 0; i < 3; i++ ) {
			D[i] = normals[i].dotProduct(u);
			D[i+3] = normals[i+3].dotProduct(v);
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
		return shader.getPointColor(collision);
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
		return pos;
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
		float max = bottomLeftBack.x;

		if ( bottomLeftFront.x > max ) {
			max = bottomLeftFront.x;
		}

		if ( bottomRightBack.x > max ) {
			max = bottomRightBack.x;
		}

		if ( bottomRightFront.x > max ) {
			max = bottomRightFront.x;
		}

		if ( topLeftBack.x > max ) {
			max = topLeftBack.x;
		}

		if ( topLeftFront.x > max ) {
			max = topLeftFront.x;
		}

		if ( topRightBack.x > max ) {
			max = topRightBack.x;
		}

		if ( topRightFront.x > max ) {
			max = topRightFront.x;
		}

		return max;
	}

	@Override
	public float getMaxYCoord() {
		float max = bottomLeftBack.y;

		if ( bottomLeftFront.y > max ) {
			max = bottomLeftFront.y;
		}

		if ( bottomRightBack.y > max ) {
			max = bottomRightBack.y;
		}

		if ( bottomRightFront.y > max ) {
			max = bottomRightFront.y;
		}

		if ( topLeftBack.y > max ) {
			max = topLeftBack.y;
		}

		if ( topLeftFront.y > max ) {
			max = topLeftFront.y;
		}

		if ( topRightBack.y > max ) {
			max = topRightBack.y;
		}

		if ( topRightFront.y > max ) {
			max = topRightFront.y;
		}

		return max;
	}

	@Override
	public float getMaxZCoord() {
		float max = bottomLeftBack.z;

		if ( bottomLeftFront.z > max ) {
			max = bottomLeftFront.z;
		}

		if ( bottomRightBack.z > max ) {
			max = bottomRightBack.z;
		}

		if ( bottomRightFront.z > max ) {
			max = bottomRightFront.z;
		}

		if ( topLeftBack.z > max ) {
			max = topLeftBack.z;
		}

		if ( topLeftFront.z > max ) {
			max = topLeftFront.z;
		}

		if ( topRightBack.z > max ) {
			max = topRightBack.z;
		}

		if ( topRightFront.z > max ) {
			max = topRightFront.z;
		}

		return max;
	}

	@Override
	public float getMinXCoord() {
		float min = bottomLeftBack.x;

		if ( bottomLeftFront.x < min ) {
			min = bottomLeftFront.x;
		}

		if ( bottomRightBack.x < min ) {
			min = bottomRightBack.x;
		}

		if ( bottomRightFront.x < min ) {
			min = bottomRightFront.x;
		}

		if ( topLeftBack.x < min ) {
			min = topLeftBack.x;
		}

		if ( topLeftFront.x < min ) {
			min = topLeftFront.x;
		}

		if ( topRightBack.x < min ) {
			min = topRightBack.x;
		}

		if ( topRightFront.x < min ) {
			min = topRightFront.x;
		}

		return min;
	}

	@Override
	public float getMinYCoord() {
		float min = bottomLeftBack.y;

		if ( bottomLeftFront.y < min ) {
			min = bottomLeftFront.y;
		}

		if ( bottomRightBack.y < min ) {
			min = bottomRightBack.y;
		}

		if ( bottomRightFront.y < min ) {
			min = bottomRightFront.y;
		}

		if ( topLeftBack.y < min ) {
			min = topLeftBack.y;
		}

		if ( topLeftFront.y < min ) {
			min = topLeftFront.y;
		}

		if ( topRightBack.y < min ) {
			min = topRightBack.y;
		}

		if ( topRightFront.y < min ) {
			min = topRightFront.y;
		}

		return min;
	}

	@Override
	public float getMinZCoord() {
		float min = bottomLeftBack.z;

		if ( bottomLeftFront.z < min ) {
			min = bottomLeftFront.z;
		}

		if ( bottomRightBack.z < min ) {
			min = bottomRightBack.z;
		}

		if ( bottomRightFront.z < min ) {
			min = bottomRightFront.z;
		}

		if ( topLeftBack.z < min ) {
			min = topLeftBack.z;
		}

		if ( topLeftFront.z < min ) {
			min = topLeftFront.z;
		}

		if ( topRightBack.z < min ) {
			min = topRightBack.z;
		}

		if ( topRightFront.z < min ) {
			min = topRightFront.z;
		}

		return min;
	}
}
