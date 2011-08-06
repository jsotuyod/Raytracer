package cg.scene.light;

import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.ColorSample;
import cg.scene.Light;
import cg.utils.Color;
import cg.utils.Registry;

public class DirectionalLight implements Light {

	private final Color color;
	private final Point3D pos;
	private final Vector3D dir;
	private final float radiusSq;

	public DirectionalLight(Color color, Point3D pos, Vector3D dir, float radius) {
		super();
		this.color = color;
		this.pos = pos;
		this.dir = dir.normalize();
		radiusSq = radius * radius;
	}

	@Override
	public ColorSample getColorSample(Collision collision) {

		if ( dir.dotProduct(collision.normal) < 0.0f ) {

			final Point3D p = collision.hitPoint;

			// project point onto source plane
            float x = p.x - pos.x;
            float y = p.y - pos.y;
            float z = p.z - pos.z;
            final float t = ((x * dir.x) + (y * dir.y) + (z * dir.z));
            if (t >= 0.0f) {
                x -= (t * dir.x);
                y -= (t * dir.y);
                z -= (t * dir.z);
                if (((x * x) + (y * y) + (z * z)) <= radiusSq) {
                	Color c = color;

                	final Point3D p2 = new Point3D(pos.x + x, pos.y + y, pos.z + z);

                	final Vector3D v = new Vector3D(p, p2);

        			// Check if there is something hiding the point
        			final Ray r = new Ray(p, v.clone() );
        			final Collision test = Registry.getScene().castRay(r);

        			if ( test != null && r.travelledDistance < (p.x - pos.x) * dir.x ) {
        				c = new Color();
        			}

                    return new ColorSample( c, c, v );
                }
            }
		}

		return null;
	}
}
