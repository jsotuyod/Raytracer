package cg.scene;

import java.util.ArrayList;
import java.util.List;

import cg.raycasting.Collision;

public class LightManager {
	private final List<Light> lights;

	public LightManager() {
		lights = new ArrayList<Light>();
	}

	public boolean add(final Light light) {
		return lights.add(light);
	}

	public void addLightSamples(final Collision collision) {
		for (final Light light : lights) {
			collision.addLightSample(light.getColorSample(collision));
		}
	}
}
