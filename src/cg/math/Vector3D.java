package cg.math;

public class Vector3D implements Cloneable {

	public float x, y, z;

	public Vector3D(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3D(final Point3D p) {
		x = p.x;
		y = p.y;
		z = p.z;
	}

	public Vector3D(final Point3D from, final Point3D to) {
		x = to.x - from.x;
		y = to.y - from.y;
		z = to.z - from.z;
	}

	public final Vector3D normalize() {
		final float d = 1.0f / (float) Math.sqrt(x * x + y * y + z * z);

		x *= d;
		y *= d;
		z *= d;

		return this;
	}

	public final Vector3D add(final Vector3D v, final float s) {
		x += v.x * s;
		y += v.y * s;
		z += v.z * s;

		return this;
	}

	public final Vector3D substract(final Vector3D v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;

		return this;
	}

	public final float dotProduct(final Vector3D v) {

		return x * v.x + y * v.y + z * v.z;
	}

	public final float dotProduct(final Point3D v) {

		return x * v.x + y * v.y + z * v.z;
	}

	public final Vector3D scale(final float d) {

		return new Vector3D(x * d, y * d, z * d );
	}

	public final Vector3D scaleSelf(final float d) {

		x *= d;
		y *= d;
		z *= d;

		return this;
	}

	public final float getAngle(final Vector3D v) {

		return (float) Math.acos(dotProduct(v));
	}

	public final Vector3D crossProduct(final Vector3D v) {

		return new Vector3D( y * v.z - z * v.y,
							 z * v.x - x * v.z,
							 x * v.y - y * v.x);
	}

	@Override
	public Vector3D clone() {
		return new Vector3D( x, y, z );
	}

	@Override
	public String toString() {
		return "v(" + x + ", " + y + ", " + z + ")";
	}

	public final float getModule() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
}
