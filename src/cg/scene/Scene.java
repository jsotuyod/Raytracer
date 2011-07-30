package cg.scene;

import java.util.LinkedList;
import java.util.List;

import cg.math.Point3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.tree.SAHKDTree;
import cg.scene.tree.SceneTree;
import cg.utils.Registry;

public class Scene {
	protected Camera camera = null;
	protected LightManager lm = null; 
	protected List<Object3D> objects = new LinkedList<Object3D>();
	protected ImageData imageData = null;
	protected SceneTree tree;
	
	public LightManager getLightManager() {
		return lm;
	}

	public void setLightManager(LightManager lm) {
		this.lm = lm;
	}

	public Camera getCamera() {
		return camera;
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public boolean add(Object3D o) {
		
		return objects.add(o);
	}
	
	public boolean remove(Object3D o) {
		
		return objects.remove(o);
	}
	
	public boolean add(Light l) {
		
		return lm.add(l);
	}
	
	public List<Object3D> getNearbyObjects(Point3D p) {
		
		return objects;
	}
	
	public ImageData getImageData() {
		return imageData;
	}

	public void setImageData(ImageData imageData) {
		this.imageData = imageData;
	}
	
	public void initialize() {
		this.tree = new SAHKDTree( this.objects );
		
		// Store self in global registry
		Registry.setScene(this);
	}

	@Override
	public String toString() {
		return "SCENE *camera: " + camera + " *objects: " + objects;
	}

	public Collision castRay(Ray r) {
		return this.tree.hitTest(r);
	}
}
