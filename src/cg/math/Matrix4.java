package cg.math;


public class Matrix4 {
	private float m11, m12, m13, m14,
				m21, m22, m23, m24,
				m31, m32, m33, m34,
				m41, m42, m43, m44;

	public Matrix4() {
	}

	public Matrix4(final float[] floatArray, final boolean rowMajor) {
		int k = 0;
		if (rowMajor) {
			m11 = floatArray[k++];
			m12 = floatArray[k++];
			m13 = floatArray[k++];
			m14 = floatArray[k++];
			m21 = floatArray[k++];
			m22 = floatArray[k++];
			m23 = floatArray[k++];
			m24 = floatArray[k++];
			m31 = floatArray[k++];
			m32 = floatArray[k++];
			m33 = floatArray[k++];
			m34 = floatArray[k++];
			m41 = floatArray[k++];
			m42 = floatArray[k++];
			m43 = floatArray[k++];
			m44 = floatArray[k++];
		} else {
			m11 = floatArray[k++];
			m21 = floatArray[k++];
			m31 = floatArray[k++];
			m41 = floatArray[k++];
			m12 = floatArray[k++];
			m22 = floatArray[k++];
			m32 = floatArray[k++];
			m42 = floatArray[k++];
			m13 = floatArray[k++];
			m23 = floatArray[k++];
			m33 = floatArray[k++];
			m43 = floatArray[k++];
			m14 = floatArray[k++];
			m24 = floatArray[k++];
			m34 = floatArray[k++];
			m44 = floatArray[k++];
		}
	}

	public static Matrix4 identity() {
		final Matrix4 I = new Matrix4();
		I.m11 = I.m22 = I.m33 = I.m44 = 1.0f;
		return I;
	}

	// create and return the transpose of the invoking matrix
	public Matrix4 transpose() {
		float tmp = m12;
		m12 = m21;
		m21 = tmp;

		tmp = m13;
		m13 = m31;
		m31 = tmp;

		tmp = m14;
		m14 = m41;
		m41 = tmp;

		tmp = m23;
		m23 = m32;
		m32 = tmp;

		tmp = m24;
		m24 = m42;
		m42 = tmp;

		tmp = m34;
		m34 = m43;
		m43 = tmp;

		return this;
	}

	// Concatenate two matrices, effectively adding the effects of both
	public Matrix4 concat(final Matrix4 B) {
		final float tm11 = m11, tm12 = m12, tm13 = m13, tm14 = m14,
			tm21 = m21, tm22 = m22, tm23 = m23, tm24 = m24,
			tm31 = m31, tm32 = m32, tm33 = m33, tm34 = m34,
			tm41 = m41, tm42 = m42, tm43 = m43, tm44 = m44;

		m11 = tm11 * B.m11 + tm12 * B.m21 + tm13 * B.m31 + tm14 * B.m41;
		m12 = tm11 * B.m12 + tm12 * B.m22 + tm13 * B.m32 + tm14 * B.m42;
		m13 = tm11 * B.m13 + tm12 * B.m23 + tm13 * B.m33 + tm14 * B.m43;
		m14 = tm11 * B.m14 + tm12 * B.m24 + tm13 * B.m34 + tm14 * B.m44;
		m21 = tm21 * B.m11 + tm22 * B.m21 + tm23 * B.m31 + tm24 * B.m41;
		m22 = tm21 * B.m12 + tm22 * B.m22 + tm23 * B.m32 + tm24 * B.m42;
		m23 = tm21 * B.m13 + tm22 * B.m23 + tm23 * B.m33 + tm24 * B.m43;
		m24 = tm21 * B.m14 + tm22 * B.m24 + tm23 * B.m34 + tm24 * B.m44;
		m31 = tm31 * B.m11 + tm32 * B.m21 + tm33 * B.m31 + tm34 * B.m41;
		m32 = tm31 * B.m12 + tm32 * B.m22 + tm33 * B.m32 + tm34 * B.m42;
		m33 = tm31 * B.m13 + tm32 * B.m23 + tm33 * B.m33 + tm34 * B.m43;
		m34 = tm31 * B.m14 + tm32 * B.m24 + tm33 * B.m34 + tm34 * B.m44;
		m41 = tm41 * B.m11 + tm42 * B.m21 + tm43 * B.m31 + tm44 * B.m41;
		m42 = tm41 * B.m12 + tm42 * B.m22 + tm43 * B.m32 + tm44 * B.m42;
		m43 = tm41 * B.m13 + tm42 * B.m23 + tm43 * B.m33 + tm44 * B.m43;
		m44 = tm41 * B.m14 + tm42 * B.m24 + tm43 * B.m34 + tm44 * B.m44;

		return this;
	}

	public Matrix4 rotateX(final float angle) {
		// Generate rotation matrix
		final Matrix4 A = new Matrix4();
		A.m11 = 1.0f;
		A.m22 = (float) Math.cos(angle);
		A.m23 = (float) -Math.sin(angle);
		A.m32 = -A.m23;
		A.m33 = A.m22;
		A.m44 = 1.0f;

		// apply it!
		return A.concat(this);
	}

	public Matrix4 rotateY(final float angle) {
		// Generate rotation matrix
		final Matrix4 A = new Matrix4();
		A.m11 = (float) Math.cos(angle);
		A.m13 = (float) Math.sin(angle);
		A.m22 = 1.0f;
		A.m31 = -A.m13;
		A.m33 = A.m11;
		A.m44 = 1.0f;

		// apply it!
		return A.concat(this);
	}

	public Matrix4 rotateZ(final float angle) {
		// Generate rotation matrix
		final Matrix4 A = new Matrix4();
		A.m11 = (float) Math.cos(angle);
		A.m12 = (float) -Math.sin(angle);
		A.m21 = -A.m12;
		A.m22 = A.m11;
		A.m33 = 1.0f;
		A.m44 = 1.0f;

		// apply it!
		return A.concat(this);
	}

	public Matrix4 rotate(float x, float y, float z, final float theta) {
		final Matrix4 m = new Matrix4();
		final float invLen = 1.0f / (float) Math.sqrt(x * x + y * y + z * z);
		x *= invLen;
		y *= invLen;
		z *= invLen;
		final float s = (float) Math.sin(theta);
		final float c = (float) Math.cos(theta);
		final float t = 1.0f - c;
		m.m11 = t * x * x + c;
		m.m22 = t * y * y + c;
		m.m33 = t * z * z + c;
		final float txy = t * x * y;
		final float sz = s * z;
		m.m12 = txy - sz;
		m.m21 = txy + sz;
		final float txz = t * x * z;
		final float sy = s * y;
		m.m13 = txz + sy;
		m.m21 = txz - sy;
		final float tyz = t * y * z;
		final float sx = s * x;
		m.m23 = tyz - sx;
		m.m32 = tyz + sx;
		m.m44 = 1.0f;
		// apply it
		return m.concat(this);
	}

	public Matrix4 scale(final float x, final float y, final float z) {
		// Generate escalation matrix
		final Matrix4 A = new Matrix4();
		A.m11 = x;
		A.m22 = y;
		A.m33 = z;
		A.m44 = 1.0f;

		// apply it!
		return A.concat(this);
	}

	public Matrix4 scale(final float s) {
		return this.scale(s, s, s);
	}

	public Matrix4 translate(final float x, final float y, final float z) {
		// Generate translation matrix
		final Matrix4 A = Matrix4.identity();
		A.m14 = x;
		A.m24 = y;
		A.m34 = z;

		// apply it!
		return A.concat(this);
	}

	public Point3D transform(final Point3D p) {
		final float x = p.x;
		final float y = p.y;
		final float z = p.z;
		final float newX, newY, newZ;

		newX = x * m11 + y * m12 + z * m13 + m14;
		newY = x * m21 + y * m22 + z * m23 + m24;
		newZ = x * m31 + y * m32 + z * m33 + m34;

		return new Point3D(newX, newY, newZ);
	}

	public Vector3D transform(final Vector3D v) {
		final float x = v.x;
		final float y = v.y;
		final float z = v.z;
		final float newX, newY, newZ;

		newX = x * m11 + y * m12 + z * m13 + m14;
		newY = x * m21 + y * m22 + z * m23 + m24;
		newZ = x * m31 + y * m32 + z * m33 + m34;

		return new Vector3D(newX, newY, newZ);
	}

	@Override
	public String toString() {
		final StringBuffer s = new StringBuffer("MATRIX( ");
		s.append(m11).append(' ').append(m12).append(' ').append(m13).append(' ').append(m14).append('\n');
		s.append(m21).append(' ').append(m22).append(' ').append(m23).append(' ').append(m24).append('\n');
		s.append(m31).append(' ').append(m32).append(' ').append(m33).append(' ').append(m34).append('\n');
		s.append(m41).append(' ').append(m42).append(' ').append(m43).append(' ').append(m44).append('\n');
		s.append(") ");

		return s.toString();
	}
}
