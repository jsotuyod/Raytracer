package cg.utils;

import cg.scene.Scene;

public class Registry {

	private static Scene scene;
	
	public static Scene getScene() {
		return scene;
	}
	
	public static void setScene(Scene scene) {
		Registry.scene = scene;
	}
}
