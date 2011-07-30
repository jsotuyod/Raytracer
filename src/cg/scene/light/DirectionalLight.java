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

	protected Color color;
	protected Point3D pos;
	protected Vector3D dir;
	protected float radiusSq;
	
	public DirectionalLight(Color color, Point3D pos, Vector3D dir, float radius) {
		super();
		this.color = color;
		this.pos = pos;
		this.dir = dir.normalize();
		this.radiusSq = radius * radius;
	}

	@Override
	public ColorSample getColorSample(Collision collision) {
		
		if ( dir.dotProduct(collision.normal) < 0.0f ) {
			
			Point3D p = collision.hitPoint;
			
			// project point onto source plane
            float x = p.x - pos.x;
            float y = p.y - pos.y;
            float z = p.z - pos.z;
            float t = ((x * dir.x) + (y * dir.y) + (z * dir.z));
            if (t >= 0.0f) {
                x -= (t * dir.x);
                y -= (t * dir.y);
                z -= (t * dir.z);
                if (((x * x) + (y * y) + (z * z)) <= radiusSq) {
                	Color c = color;
                	
                	Point3D p2 = new Point3D(pos.x + x, pos.y + y, pos.z + z);
        			
                	Vector3D v = new Vector3D(p, p2);
                	
        			// Check if there is something hiding the point
        			Ray r = new Ray(p, v.clone() );
        			Collision test = Registry.getScene().castRay(r);
        			
        			if ( test != null && r.travelledDistance < (p.x - pos.x) * dir.x ) {
        				c = new Color();
        			}
        			
                    return new ColorSample( c, c, v );
                }
            }
		}
		
		return null;
	}

	@Override
	public void setColor(Color c) {
		this.color= c;
	}
}
