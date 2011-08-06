package cg.scene;

import cg.raycasting.Collision;

public interface Light {

	public ColorSample getColorSample(Collision collision);
}
