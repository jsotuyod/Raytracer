package cg.scene;

import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Ray;

public class Camera {

	private final Point3D pos;
	private final Vector3D target;
	private final Vector3D up;
	private final Vector3D sideVector;

	private final float fovX;
	private final float fovY;

	public Camera(final Point3D pos, final Point3D target, final Vector3D up, final float fov,
			final float aspect) {
		this.pos = pos;
		this.target = new Vector3D(pos, target).normalize();

		// this guarantees the up vector is orthogonal to target vector.
		this.up = this.target.crossProduct(up).normalize().crossProduct(this.target);

		fovX = (float) Math.tan(Math.toRadians(fov * 0.5f)) * 2.0f;
		fovY = fovX / aspect;

		sideVector = this.target.crossProduct(this.up);
	}

	public Ray getRay(final float x, final float y) {
		// Apply translations to the corresponding point & direction
		final Vector3D v = target.clone().add(up, fovY * y)
			.add(sideVector, fovX * x);

		return new Ray(pos, v);
	}

	@Override
	public String toString() {
		return "CAMERA *pos: " + pos + " *target: " + target + " *up: " + up + " *fovX: " + fovX + " *fovY: " + fovY;
	}
}
