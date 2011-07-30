package cg.scene.object3d;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cg.exceptions.NoShaderException;
import cg.math.Matrix4;
import cg.math.Point2D;
import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.Object3D;
import cg.scene.object3d.boundingvolume.AABB;
import cg.scene.object3d.boundingvolume.BoundingVolume;
import cg.scene.shaders.Shader;
import cg.scene.tree.SAHKDTree;
import cg.scene.tree.SceneTree;
import cg.utils.Color;

public class TriangleMesh implements Object3D {

	public enum UVType {
		NONE, VERTEX, FACEVARYING
	}
	
	public enum NormalType {
		NONE, VERTEX, FACEVARYING
	}
	
	protected SceneTree triangles;
	protected Shader shader;
	protected BoundingVolume bb;
	
	protected Collision collidedTriangle;
	
	protected HashMap<Long, Collision> collisionMap;
	
	protected Point3D pos;
	
	public TriangleMesh(Point3D[] points, int[] vertices, Shader shader,
			UVType uvType, float[] uvs, NormalType normalType, float[] normals, Matrix4 transform) {
		super();
		
		if ( shader == null ) {
			throw new NoShaderException();
		}
		
		this.shader = shader;
		
		this.collidedTriangle = null;
		
		float minX, minY, minZ;
		float maxX, maxY, maxZ;
		
		minX = minY = minZ = Float.POSITIVE_INFINITY;
		maxX = maxY = maxZ = Float.NEGATIVE_INFINITY;
		
		Vector3D n1 = null, n2 = null, n3 = null;
		Vector3D reference = new Vector3D(0,0,0);
		
		if ( transform != null ) {
			reference = transform.transform(reference);
		}
		
		List<Object3D> triangles = new LinkedList<Object3D>();
		for (int i = 0; i < vertices.length; i+=3) {
			
			if ( normalType == NormalType.VERTEX ) {
				n1 = new Vector3D( normals[vertices[i] * 3], normals[vertices[i] * 3 + 1], normals[vertices[i] * 3 + 2] ).normalize();
				n2 = new Vector3D( normals[vertices[i+1] * 3], normals[vertices[i+1] * 3 + 1], normals[vertices[i+1] * 3 + 2] ).normalize();
				n3 = new Vector3D( normals[vertices[i+2] * 3], normals[vertices[i+2] * 3 + 1], normals[vertices[i+2] * 3 + 2] ).normalize();
				
				// Transform normals!!
				if ( transform != null ) {
					n1 = transform.transform(n1).substract(reference).normalize();
					n2 = transform.transform(n2).substract(reference).normalize();
					n3 = transform.transform(n3).substract(reference).normalize();
				}
			}
			
			if ( uvType != UVType.NONE ) {
				triangles.add(new Triangle(points[vertices[i]], points[vertices[i+1]], points[vertices[i+2]], shader,
							uvType, uvs[vertices[i] * 2], uvs[vertices[i] * 2 + 1], uvs[vertices[i+1] * 2], uvs[vertices[i+1] * 2 + 1],
							uvs[vertices[i+2] * 2], uvs[vertices[i+2] * 2 + 1], normalType, n1, n2, n3));
			} else {
				triangles.add(new Triangle(points[vertices[i]], points[vertices[i+1]], points[vertices[i+2]], shader, normalType, n1, n2, n3));
			}
			
			// Check for maximum / minimums for AABB
			for (int j = 0; j < 3; j++) {
				if ( points[vertices[i+j]].x > maxX ) {
					maxX = points[vertices[i+j]].x;
				} else if ( points[vertices[i+j]].x < minX ) {
					minX = points[vertices[i+j]].x;
				}
				
				if ( points[vertices[i+j]].y > maxY ) {
					maxY = points[vertices[i+j]].y;
				} else if ( points[vertices[i+j]].y < minY ) {
					minY = points[vertices[i+j]].y;
				}
				
				if ( points[vertices[i+j]].z > maxZ ) {
					maxZ = points[vertices[i+j]].z;
				} else if ( points[vertices[i+j]].z < minZ ) {
					minZ = points[vertices[i+j]].z;
				}
			}
		}
		
		this.triangles = new SAHKDTree( triangles );
		
		this.collisionMap = new HashMap<Long, Collision>();
		
		this.bb = new AABB(new Point3D(minX, minY, minZ), new Point3D(maxX, maxY, maxZ));
		
		this.pos = new Point3D( ( minX + maxX ) * 0.5f,
				( minY + maxY ) * 0.5f,
				( minZ + maxZ ) * 0.5f );
	}

	@Override
	public Color getColor(Collision collision) {
		return this.shader.getPointColor(collision);
	}

	@Override
	public Point3D getHitPoint(Ray r) {
		
		if ( this.bb.hitTest(r) == false ) {
			return null;
		}
		
		Collision collision = this.triangles.hitTest(r);
		
		if ( collision == null ) {
			return null;
		}
		
		// Set it as the collision for the current thread
		collisionMap.put(Thread.currentThread().getId(), collision);
		
		return collision.hitPoint;
	}

	@Override
	public String toString() {
		return "TRIANGLE-MESH *triangles: " + triangles;
	}

	@Override
	public Point3D getPos() {
		return this.pos;
	}

	@Override
	public Vector3D getNormal(Point3D p) {
		return collisionMap.get(Thread.currentThread().getId()).normal;
	}

	@Override
	public Point2D getUV(Collision collision) {
		return collisionMap.get(Thread.currentThread().getId()).getUV();
	}

	@Override
	public float getMaxXCoord() {
		return this.bb.getMaxP().x;
	}

	@Override
	public float getMaxYCoord() {
		return this.bb.getMaxP().y;
	}

	@Override
	public float getMaxZCoord() {
		return this.bb.getMaxP().z;
	}

	@Override
	public float getMinXCoord() {
		return this.bb.getMinP().x;
	}

	@Override
	public float getMinYCoord() {
		return this.bb.getMinP().y;
	}

	@Override
	public float getMinZCoord() {
		return this.bb.getMinP().z;
	}
}
