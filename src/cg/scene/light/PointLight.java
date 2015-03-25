package cg.scene.light;

import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.ColorSample;
import cg.scene.Light;
import cg.utils.Color;
import cg.utils.Registry;

public class PointLight implements Light {

	private final static float FourPI = 4.0f * (float) Math.PI;

	private final Color color;
	private final Point3D pos;

	public PointLight(final Color color, final Point3D pos) {
		this.color = color;
		this.pos = pos;
	}

	@Override
	public ColorSample getColorSample(final Collision collision) {
		final Vector3D d = new Vector3D(collision.hitPoint, pos);

		if (d.x * collision.normal.x + d.y * collision.normal.y + d.z * collision.normal.z > 0.0f) {
			final Color c;
			final float distanceSq = pos.getDistanceSquared(collision.hitPoint);

			// Check if there is something hiding the point
			final Ray r = new Ray(collision.hitPoint, d.clone());
			final Collision test = Registry.getScene().castRay(r);

			if (test != null && r.travelledDistance * r.travelledDistance < distanceSq) {
				c = Color.BLACK;
			} else {
				/*
				 *  Energy is equally distributed among the surface of the sphere
				 *  with center in the light, and including the hit point:
				 *  1 / (4 * PI * r^2)
				 */
				final float scale = 1.0f / (PointLight.FourPI * distanceSq);

				c = color.clone().mul(scale);
			}

			return new ColorSample(c, c, d);
		}

		// No light from the source reaches the collision point.
		return null;
	}
}
