package cg.scene.tree;

import cg.raycasting.Collision;
import cg.raycasting.Ray;

public interface SceneTree {

	public Collision hitTest( Ray r );
}
