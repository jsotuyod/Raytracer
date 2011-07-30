package cg.scene;

import cg.raycasting.Collision;
import cg.utils.Color;

public interface Light {

	public ColorSample getColorSample(Collision collision);
	
	public void setColor(Color c);
}
