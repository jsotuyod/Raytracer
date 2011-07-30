package cg.scene;

import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Ray;

public class Camera {

	protected Point3D pos;
	protected Vector3D target;
	protected Vector3D up;
	protected Vector3D sideVector;
	
	protected float fovX;
	protected float fovY;
	
	public Camera(Point3D pos, Point3D target, Vector3D up, float fov,
			float aspect) {
		super();
		this.pos = pos;
		this.target = new Vector3D( pos, target ).normalize();
		
		// this guarantees the up vector is orthogonal to target vector.
		this.up = this.target.crossProduct(up).normalize().crossProduct(this.target);
		
		this.fovX = (float) Math.tan(Math.toRadians(fov * 0.5f)) * 2.0f;
		this.fovY = this.fovX / aspect;
		
		this.sideVector = this.target.crossProduct(this.up);
	}
	
	public Ray getRay(float x, float y) {
		
		// Apply translations to the corresponding point & direction
		Vector3D v = target.clone().add( up, fovY * y )
			.add( sideVector, fovX * x );
		
		return new Ray( pos, v );
	}
	
	public Point3D getPos() {
		return pos.clone();
	}
	
	public Vector3D getTarget() {
		return target.clone();
	}
	
	public Vector3D getUp() {
		return up.clone();
	}
	
	public float getFovX() {
		return fovX;
	}
	
	public float getFovY() {
		return fovY;
	}
	
	@Override
	public String toString() {
		return "CAMERA *pos: " + pos + " *target: " + target + " *up: " + up + " *fovX: " + fovX + " *fovY: " + fovY;
	}
}
