package cg.math;

public class Point3D implements Cloneable {
	public float x, y, z;

	public Point3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final float getDistance(final Point3D p) {
		final float x_sqrt = x - p.x;
		final float y_sqrt = y - p.y;
		final float z_sqrt = z - p.z;
		return (float) Math.sqrt( x_sqrt * x_sqrt + y_sqrt * y_sqrt + z_sqrt * z_sqrt);
	}

	public final float getDistanceSquared(final Point3D p) {
		final float x_sqrt = x - p.x;
		final float y_sqrt = y - p.y;
		final float z_sqrt = z - p.z;
		return x_sqrt * x_sqrt + y_sqrt * y_sqrt + z_sqrt * z_sqrt;
	}

	public final Point3D translate(final Vector3D v, final float s) {

		x += v.x * s;
		y += v.y * s;
		z += v.z * s;

		return this;
	}

	public final Point3D translateNew(final Vector3D v, final float s) {

		return new Point3D(x + v.x * s, y + v.y * s, z + v.z * s );
	}

	public final Point3D substract(final Point3D p) {

		x -= p.x;
		y -= p.y;
		z -= p.z;

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
