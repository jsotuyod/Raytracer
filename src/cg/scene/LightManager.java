package cg.scene;

import java.util.LinkedList;
import java.util.List;

import cg.raycasting.Collision;

public class LightManager {

	protected List<Light> lights;
	
	public LightManager() {
		this.lights = new LinkedList<Light>();
	}
	
	public boolean add(Light light) {
		return lights.add( light );
	}
	
	public void addLightSamples(Collision collision) {
		
		for (Light light : lights) {
			collision.addLightSample(light.getColorSample(collision));
		}
	}
}
