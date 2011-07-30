package cg.math;


public class Matrix4 implements Cloneable {
	protected float[][] data;

	public Matrix4() {
		data = new float[4][4];
	}

	// create matrix based on 2d array
	public Matrix4(float[][] data) {
		if ( data.length != 4 ) {
			throw new RuntimeException( "Matrix data must be 4x4" );
		}
		if ( data[0].length != 4 ) {
			throw new RuntimeException( "Matrix data must be 4x4" );
		}
		
		this.data = new float[4][4];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				this.data[i][j] = data[i][j];
	}
	
	public Matrix4(float[] floatArray, boolean rowMajor) {
		int k = 0;
		this.data = new float[4][4];
		if(rowMajor){
			for (int i = 0; i < 4; i++){
				for (int j = 0; j < 4; j++){
					this.data[i][j] = floatArray[k++];
				}
			}
		}else{
			for (int i = 0; i < 4; i++){
				for (int j = 0; j < 4; j++){
					this.data[j][i] = floatArray[k++];
				}
			}
		}
	}

	public float[][] getData() {
		return this.data;
	}

	public static Matrix4 identity() {
		Matrix4 I = new Matrix4();
		for (int i = 0; i < 4; i++)
			I.data[i][i] = 1;
		return I;
	}

	// create and return the transpose of the invoking matrix
	public Matrix4 transpose() {
		Matrix4 A = new Matrix4(data);
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				data[j][i] = A.data[i][j];
			}
		}
		return this;
	}

	public boolean equals(Matrix4 B) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (data[i][j] != B.data[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	// Concatenate two matrices, effectively adding the effects of both
	public Matrix4 concat(Matrix4 B) {
		Matrix4 C = new Matrix4();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					C.data[i][j] += (data[i][k] * B.data[k][j]);
				}
			}
		}
		
		this.data = C.data;
		
		return this;
	}
	
	public Matrix4 rotateX(float angle) {
		// Generate rotation matrix
		Matrix4 A = new Matrix4();
		A.data[0][0] = 1;
		A.data[1][1] = (float) Math.cos(angle);
		A.data[1][2] = (float) -Math.sin(angle);
		A.data[2][1] = (float) Math.sin(angle);
		A.data[2][2] = (float) Math.cos(angle);
		A.data[3][3] = 1;
		
		// apply it!
		return A.concat(this);
	}
	
	public Matrix4 rotateY(float angle) {
		// Generate rotation matrix
		Matrix4 A = new Matrix4();
		A.data[0][0] = (float) Math.cos(angle);
		A.data[0][2] = (float) Math.sin(angle);
		A.data[1][1] = 1;
		A.data[2][0] = (float) -Math.sin(angle);
		A.data[2][2] = (float) Math.cos(angle);
		A.data[3][3] = 1;
		
		// apply it!
		return A.concat(this);
	}
	
	public Matrix4 rotateZ(float angle) {
		// Generate rotation matrix
		Matrix4 A = new Matrix4();
		A.data[0][0] = (float) Math.cos(angle);
		A.data[0][1] = (float) -Math.sin(angle);
		A.data[1][0] = (float) Math.sin(angle);
		A.data[1][1] = (float) Math.cos(angle);
		A.data[2][2] = 1;
		A.data[3][3] = 1;
		
		// apply it!
		return A.concat(this);
	}

	public Matrix4 rotate(float x, float y, float z, float theta) {
		Matrix4 m = new Matrix4();
		float invLen = 1.0f / (float) Math.sqrt(x * x + y * y + z * z);
		x *= invLen;
		y *= invLen;
		z *= invLen;
		float s = (float) Math.sin(theta);
		float c = (float) Math.cos(theta);
		float t = 1 - c;
		m.data[0][0] = t * x * x + c;
		m.data[1][1] = t * y * y + c;
		m.data[2][2] = t * z * z + c;
		float txy = t * x * y;
		float sz = s * z;
		m.data[0][1] = txy - sz;
		m.data[1][0] = txy + sz;
		float txz = t * x * z;
		float sy = s * y;
		m.data[0][2] = txz + sy;
		m.data[2][0] = txz - sy;
		float tyz = t * y * z;
		float sx = s * x;
		m.data[1][2] = tyz - sx;
		m.data[2][1] = tyz + sx;
		m.data[3][3] = 1;
		// apply it
		return m.concat(this);
	}
	
	public Matrix4 scale(float x, float y, float z) {
		// Generate escalation matrix
		Matrix4 A = Matrix4.identity();
		A.data[0][0] = x;
		A.data[1][1] = y;
		A.data[2][2] = z;
		
		// apply it!
		return A.concat(this);
	}
	
	public Matrix4 scale(float s){
		return this.scale(s, s, s);
	}
	
	public Matrix4 translate(float x, float y, float z) {
		// Generate translation matrix
		Matrix4 A = Matrix4.identity();
		A.data[0][3] = x;
		A.data[1][3] = y;
		A.data[2][3] = z;
		
		// apply it!
		return A.concat(this);
	}
	
	public Point3D transform(Point3D p) {
		float x = p.x;
		float y = p.y;
		float z = p.z;
		float newX, newY, newZ;
		
		newX = x * data[0][0] + y * data[0][1] + z * data[0][2] + data[0][3];
		newY = x * data[1][0] + y * data[1][1] + z * data[1][2] + data[1][3];
		newZ = x * data[2][0] + y * data[2][1] + z * data[2][2] + data[2][3];
		
		return new Point3D(newX, newY, newZ);
	}
	
	public Vector3D transform(Vector3D v) {
		float x = v.x;
		float y = v.y;
		float z = v.z;
		float newX, newY, newZ;
		
		newX = x * data[0][0] + y * data[0][1] + z * data[0][2] + data[0][3];
		newY = x * data[1][0] + y * data[1][1] + z * data[1][2] + data[1][3];
		newZ = x * data[2][0] + y * data[2][1] + z * data[2][2] + data[2][3];
		
		return new Vector3D(newX, newY, newZ);
	}
	
	@Override
	public Matrix4 clone() {
		return new Matrix4( this.data );
	}
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer("MATRIX4: ");
		for (int i = 0; i < 4; i++) {
			s.append("( ");
			for (int j = 0; j < 4; j++) {
				s.append(data[i][j] + " ");
			}
			s.append(") ");
		}
		return s.append("\n").toString();
	}
}
