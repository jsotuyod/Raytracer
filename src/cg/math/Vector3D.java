package cg.math;

public class Vector3D implements Cloneable {

	public float x, y, z;

	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3D(Point3D p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	
	public Vector3D(Point3D from, Point3D to) {
		this.x = to.x - from.x;
		this.y = to.y - from.y;
		this.z = to.z - from.z;
	}

	public Vector3D normalize() {
		
		float d = 1.0f / (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		
		this.x *= d;
		this.y *= d;
		this.z *= d;
		
		return this;
	}
	
	public Vector3D add(Vector3D v, float s) {
		this.x += v.x * s;
		this.y += v.y * s;
		this.z += v.z * s;
		
		return this;
	}
	
	public Vector3D substract(Vector3D v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		
		return this;
	}
	
	public float dotProduct(Vector3D v) {
		
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
	
	public float dotProduct(Point3D v) {
		
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}
	
	public Vector3D scale(float d) {
		
		return new Vector3D(this.x * d, this.y * d, this.z * d );
	}
	
	public Vector3D scaleSelf(float d) {
		
		this.x *= d;
		this.y *= d;
		this.z *= d;
		
		return this;
	}
	
	public float getAngle(Vector3D v) {
		
		return (float) Math.acos(this.dotProduct(v));
	}
	
	public Vector3D crossProduct(Vector3D v) {
		
		return new Vector3D( this.y * v.z - this.z * v.y,
							 this.z * v.x - this.x * v.z,
							 this.x * v.y - this.y * v.x);
	}
	
	@Override
	public Vector3D clone() {
		return new Vector3D( this.x, this.y, this.z );
	}
	
	@Override
	public String toString() {
		return "v(" + x + ", " + y + ", " + z + ")";
	}

	public float getModule() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
}
