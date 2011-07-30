package cg.scene;

import cg.math.Point2D;
import cg.math.Point3D;
import cg.math.Vector3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.utils.Color;

public interface Object3D {

	public Point3D getHitPoint(Ray r);
	
	public Color getColor(Collision collision);
	
	public Point3D getPos();
	
	public Vector3D getNormal(Point3D p);
	
	public float getMaxXCoord();
	public float getMinXCoord();
	public float getMaxYCoord();
	public float getMinYCoord();
	public float getMaxZCoord();
	public float getMinZCoord();
	
	public Point2D getUV(Collision collision);	
}
