package cg.scene.object3d;

import cg.exceptions.NoShaderException;
import cg.exceptions.UnsupportedInterpolationType;
import cg.math.Point2D;
import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.Object3D;
import cg.scene.object3d.TriangleMesh.NormalType;
import cg.scene.object3d.TriangleMesh.UVType;
import cg.scene.shaders.Shader;
import cg.utils.Color;

public class Triangle implements Object3D {

	protected Point3D p1, p2, p3;
	protected Shader shader;
	
	// Used for hitTests, precomputed for performance
	protected Vector3D v1, v2, n, nn;
	protected float uu_D, uv_D, vv_D;
	
	// used for texture mapping, precomputed for performance
	protected float tu1, tu2, tu3;
	protected float tv1, tv2, tv3;
	
	// Normals for vertex mode
	protected Vector3D n1, n2, n3;
	
	protected UVType uvType;
	protected NormalType normalType;
	
	protected Point3D pos;
	
	public Triangle(Point3D p1, Point3D p2, Point3D p3, Shader shader, UVType uvType,
			float u1, float v1, float u2, float v2, float u3, float v3,
			NormalType normalType, Vector3D n1, Vector3D n2, Vector3D n3) {
		super();
		
		if ( shader == null ) {
			throw new NoShaderException();
		}
		
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
		// TODO : Que pasa si 2 o más puntos son iguales?? n da 0,0,0 en ese caso
		this.v1 = new Vector3D( p1, p2 );
		this.v2 = new Vector3D( p1, p3 );
		
		this.n = this.v1.crossProduct(this.v2);
		this.nn = this.n.clone().normalize();
		
		float uu = this.v1.dotProduct(this.v1);
		float uv = this.v1.dotProduct(this.v2);
		float vv = this.v2.dotProduct(this.v2);
		float iD = 1.0f / (uv * uv - uu * vv);
		
		this.uu_D = uu * iD;
		this.uv_D = uv * iD;
		this.vv_D = vv * iD;

		this.pos = new Point3D( ( p1.x + p2.x + p3.x ) / 3.0f,
				( p1.y + p2.y + p3.y ) / 3.0f,
				( p1.z + p2.z + p3.z ) / 3.0f );

		this.shader = shader;
		
		this.uvType = uvType;
		
		this.tu1 = u1;
		this.tu2 = u2;
		this.tu3 = u3;
		this.tv1 = v1;
		this.tv2 = v2;
		this.tv3 = v3;
		
		this.normalType = normalType;
		
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;
	}

	public Triangle(Point3D p1, Point3D p2, Point3D p3, Shader shader, NormalType normalType, Vector3D n1, Vector3D n2, Vector3D n3) {
		this(p1, p2, p3, shader, UVType.NONE, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, normalType, n1, n2, n3);
	}

	@Override
	public Color getColor(Collision collision) {
		return this.shader.getPointColor(collision);
	}

	@Override
	public Point3D getHitPoint(Ray p) {
		
		float b = n.x * p.d.x + n.y * p.d.y + n.z * p.d.z;
		
		if (b == 0.0f) {
			return null;	// Ray is parallel to the plane in which the triangle is
		}
		
		Vector3D v = new Vector3D( this.p1, p.p );
	    float r = -(n.x * v.x + n.y * v.y + n.z * v.z ) / b;
	    
	    if (r < 0.0f || r > p.travelledDistance) {
	        return null;	// Ray goes away from triangle, or hits further than previously found hit
	    }
	    
	    // for a segment, also test if (r > 1.0) => no intersect

	    Point3D hitPoint = p.p.translateNew( p.d, r );	// intersect point of ray and plane

	    // is hitPoint inside the triangle?
	    Vector3D w = new Vector3D( p1, hitPoint );
	    float wu = w.x * v1.x + w.y * v1.y + w.z * v1.z;
	    float wv = w.x * v2.x + w.y * v2.y + w.z * v2.z;

	    // get and test parametric coords
	    float s = uv_D * wv - vv_D * wu;
	    if (s < 0.0f || s > 1.0f)        // hitPoint is outside triangle
	        return null;
	    float t = uv_D * wu - uu_D * wv;
	    if (t < 0.0f || (s + t) > 1.0f)  // hitPoint is outside triangle
	        return null;
	    
	    p.travelledDistance = r;
		return hitPoint;
	}

	@Override
	public String toString() {
		return "TRIANGLE *p1: " + p1 + " *p2: " + p2 + " *p3: " + p3;
	}

	@Override
	public Point3D getPos() {
		return this.pos;
	}

	@Override
	public Vector3D getNormal(Point3D p) {
		
		switch (normalType) {
        case NONE:
            return this.nn.clone();
            
        case VERTEX:
    	    Vector3D ww = new Vector3D( p1, p );
    	    float wu = ww.x * v1.x + ww.y * v1.y + ww.z * v1.z;
		    float wv = ww.x * v2.x + ww.y * v2.y + ww.z * v2.z;

    	    // get parametric coords
    	    float s = uv_D * wv - vv_D * wu;
    	    float t = uv_D * wu - uu_D * wv;
    	    
        	float w = 1.0f - s - t;
        	
        	return new Vector3D( w * n1.x + s * n2.x + t * n3.x,
        			w * n1.y + s * n2.y + t * n3.y,
        			w * n1.z + s * n2.z + t * n3.z ).normalize();
        	
        default:
			throw new UnsupportedInterpolationType();
        }
	}

	@Override
	public Point2D getUV(Collision collision) {
		float u = 0.0f;
		float v = 0.0f;
		
		switch ( uvType ) {
		case NONE:
			break;
			
		case VERTEX:
			Vector3D ww = new Vector3D( p1, collision.hitPoint );
			float wu = ww.x * v1.x + ww.y * v1.y + ww.z * v1.z;
		    float wv = ww.x * v2.x + ww.y * v2.y + ww.z * v2.z;

    	    // get parametric coords
    	    float s = uv_D * wv - vv_D * wu;
    	    float t = uv_D * wu - uu_D * wv;
			float w = 1.0f - s - t;
            u = w * tu1 + s * tu2 + t * tu3;
            v = w * tv1 + s * tv2 + t * tv3;
			break;
			
		default:
			throw new UnsupportedInterpolationType();
		}
		
		return new Point2D( u, v );
	}

	@Override
	public float getMaxXCoord() {
		return Math.max(Math.max(p1.x, p2.x), p3.x);
	}

	@Override
	public float getMaxYCoord() {
		return Math.max(Math.max(p1.y, p2.y), p3.y);
	}

	@Override
	public float getMaxZCoord() {
		return Math.max(Math.max(p1.z, p2.z), p3.z);
	}

	@Override
	public float getMinXCoord() {
		return Math.min(Math.min(p1.x, p2.x), p3.x);
	}

	@Override
	public float getMinYCoord() {
		return Math.min(Math.min(p1.y, p2.y), p3.y);
	}

	@Override
	public float getMinZCoord() {
		return Math.min(Math.min(p1.z, p2.z), p3.z);
	}
}
