package cg.scene.object3d.boundingvolume;

import cg.math.Point3D;
import cg.raycasting.Ray;

public interface BoundingVolume {

	public boolean hitTest(Ray r);
	
	public Point3D getMinP();

	public Point3D getMaxP();
}
