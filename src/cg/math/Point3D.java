package cg.math;

public class Point3D implements Cloneable {
	public float x, y, z;

	public Point3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getDistance(Point3D p) {
		float x_sqrt = x - p.x;
		float y_sqrt = y - p.y;
		float z_sqrt = z - p.z;
		return (float) Math.sqrt( x_sqrt * x_sqrt + y_sqrt * y_sqrt + z_sqrt * z_sqrt);
	}
	
	public float getDistanceSquared(Point3D p) {
		float x_sqrt = x - p.x;
		float y_sqrt = y - p.y;
		float z_sqrt = z - p.z;
		return x_sqrt * x_sqrt + y_sqrt * y_sqrt + z_sqrt * z_sqrt;
	}
	
	public Point3D translate(Vector3D v, float s) {
		
		this.x += v.x * s;
		this.y += v.y * s;
		this.z += v.z * s;
		
		return this;
	}
	
	public Point3D translateNew(Vector3D v, float s) {
		
		return new Point3D(this.x + v.x * s, this.y + v.y * s, this.z + v.z * s );
	}
	
	public Point3D substract(Point3D p) {
		
		this.x -= p.x;
		this.y -= p.y;
		this.z -= p.z;
		
		return this;
	}
	
	@Override
	public Point3D clone() {
		return new Point3D(x, y, z);
	}
	
	@Override
	public String toString() {
		return "p(" + x + ", " + y + ", " + z + ")";
	}
}
