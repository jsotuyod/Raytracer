package cg.scene.light;

import java.util.Random;

import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.ColorSample;
import cg.scene.Light;
import cg.scene.Scene;
import cg.utils.Color;
import cg.utils.Registry;

public class SphereLight implements Light {
	private final static float FourPI = 4.0f * (float) Math.PI;

	private final Color color;
	private final Point3D pos;
	private final float radius;
	private final float radiusSq;
	private final int samples;
	private final float iSamples;
	private final Random random = new Random();

	public SphereLight(final Color color, final Point3D pos, final float radius, final int samples) {
		this.color = color;
		this.pos = pos;
		this.radius = radius;
		this.samples = samples;

		radiusSq = radius * radius;
		iSamples = 1.0f / samples;
	}

	@Override
	public ColorSample getColorSample(final Collision collision) {
		final Point3D p = collision.hitPoint;
		final Vector3D d = new Vector3D(p, pos);

		if (d.dotProduct(collision.normal) > 0.0f) {
			final float distanceSq = pos.getDistanceSquared(p);

			if ( distanceSq < radiusSq ) {
				return null;	// We assume the surface is shinning, everything inside is undefined
			}

			/*
			 *  Energy is equally distributed among the surface of the sphere
			 *  with center in the light, and including the hit point:
			 *  1 / (4 * PI * r^2)
			 */
			final float scale = 1.0f / (SphereLight.FourPI * distanceSq);

			final Color light = color.clone().mul( scale );
			final Color c = new Color();

			// v1 can NEVER be collineal with d this way
			final Vector3D dn = d.clone().normalize();
			Vector3D v1 = new Vector3D(0.0f, 1.0f, 1.0f + dn.z);

			// Get a direction perpendicular to d, and fine-tune the third one
			final Vector3D v2 = d.crossProduct(v1).normalize();
			v1 = v2.crossProduct(d).normalize().scaleSelf(radius);

			Vector3D dir, dv;
			float distance, dvMod;
			Ray r;
			Collision test;
			final Scene scene = Registry.getScene();

			// Generate rays for shadows - more samples get better penumbra
			for (int i = 0; i < samples; i++) {
				// get a vector to a random point in the sphere's plane orthogonal to d
				dv = v1.scale(random.nextFloat() * 2.0f - 1.0f);
				dir = d.clone().add(dv,1.0f);

				// Move on the other axis, making sure we remain within the sphere's projection onto the plane (circle)
				dvMod = dv.getModule();
				dir.add(v2, ( random.nextFloat() * 2.0f - 1.0f) * (float) Math.sqrt( radiusSq - dvMod * dvMod ));

				distance = dir.getModule();

				// Check if there is something hiding the point
				r = new Ray(p, dir);
				test = scene.castRay(r);

				if ( test == null || r.travelledDistance >= distance ) {
					c.add(light);
				}
			}

			c.mul(iSamples);

			return new ColorSample( c, c, d );
		}

		// No light from the source reaches the collision point.
		return null;
	}
}
