package cg.scene.tree;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cg.math.Point3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.Object3D;

public class KDTree implements SceneTree {

	enum Axis {
		X, Y, Z
	};
	
	private static final int OBJECTS_THRESHOLD = 5;
	
	private static final int MAX_TREE_DEPTH = 20;
	private static final int MIN_TREE_DEPTH = 5;
	
	protected KDNode root;
	
	public KDTree( List<Object3D> objects ) {
		
		if ( objects.size() <= OBJECTS_THRESHOLD ) {
			this.root = new KDLeaf( objects );
		} else {
			this.root = splitObjects( objects, Axis.X, 1 );
		}
	}
	
	public Collision hitTest( Ray r ) {
		return this.root.hitTest( r, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY );
	}
	
	private KDNode splitObjects( List<Object3D> objects, Axis splitAxis, int depth ) {
		
		List<Object3D> left = new LinkedList<Object3D>();
		List<Object3D> right = new LinkedList<Object3D>();
		
		KDNode leftNode, rightNode;
		Axis nextAxis = Axis.values()[(splitAxis.ordinal() + 1) % 3];
		
		float splitPoint = splitOverAxis( objects, splitAxis, left, right );
		
		if ( (left.size() <= OBJECTS_THRESHOLD && (depth > MIN_TREE_DEPTH || left.size() == 0)) || depth == MAX_TREE_DEPTH ) {
			leftNode = new KDLeaf( left );
		} else {
			leftNode = splitObjects( left, nextAxis, depth + 1 );
		}
		
		if ( (right.size() <= OBJECTS_THRESHOLD && (depth > MIN_TREE_DEPTH || right.size() == 0)) || depth == MAX_TREE_DEPTH ) {
			rightNode = new KDLeaf( right );
		} else {
			rightNode = splitObjects( right, nextAxis, depth + 1 );
		}
		
		switch ( splitAxis ) {
		case X:
			return new KDInternalNodeXAxis(splitPoint, leftNode, rightNode );
			
		case Y:
			return new KDInternalNodeYAxis(splitPoint, leftNode, rightNode );
			
		case Z:
			return new KDInternalNodeZAxis(splitPoint, leftNode, rightNode );
		}
		
		// This should never happen....
		return null;
	}
	
	private float splitOverAxis( List<Object3D> objects, Axis splitAxis, List<Object3D> left, List<Object3D> right ) {
		
		// Build list of min and max
		List<Float> mins = new LinkedList<Float>();
		List<Float> maxs = new LinkedList<Float>();
		
		for (Object3D object : objects) {
			
			switch (splitAxis) {
			case X:
				mins.add(object.getMinXCoord());
				maxs.add(object.getMaxXCoord());
				break;

			case Y:
				mins.add(object.getMinYCoord());
				maxs.add(object.getMaxYCoord());
				break;
				
			case Z:
				mins.add(object.getMinZCoord());
				maxs.add(object.getMaxZCoord());
				break;
			}
		}
		
		Collections.sort(mins);
		Collections.sort(maxs);
		
		// brute force most equitative split
		int leftObjects = -1;
		int rightObjects = objects.size() + 1;
		int maxIndex = 0;
		float splitPoint = mins.get(0) - 0.001f;
		int bestCountLeft = -objects.size(), bestCountRight = objects.size();
		int maxTotal = (int) Math.ceil( objects.size() * 1.1f );
		int totalObjects = objects.size();
		
		for (Float min : mins) {
			leftObjects++;	// We raise the minimum, a new object goes to the left
			
			while ( maxIndex < totalObjects && min >= maxs.get(maxIndex) ) {
				maxIndex++;
				rightObjects--;	// An object is completely on the left!
			}
			
			if ( leftObjects + rightObjects <= maxTotal
						&& Math.abs(bestCountLeft - bestCountRight) > Math.abs( leftObjects - rightObjects ) ) {
				// We found a more equitative split!
				bestCountLeft = leftObjects;
				bestCountRight = rightObjects;
				
				splitPoint = min - 0.001f;
			}
		}
		
		// Check if an object should be in both nodes
		for (Object3D object : objects) {
			switch ( splitAxis ) {
			case X:
				if ( object.getMinXCoord() < splitPoint ) {
					left.add(object);
				}
				
				if ( object.getMaxXCoord() > splitPoint ) {
					right.add(object);
				}
				break;
				
			case Y:
				if ( object.getMinYCoord() < splitPoint ) {
					left.add(object);
				}
				
				if ( object.getMaxYCoord() > splitPoint ) {
					right.add(object);
				}
				break;
				
			case Z:
				if ( object.getMinZCoord() < splitPoint ) {
					left.add(object);
				}
				
				if ( object.getMaxZCoord() > splitPoint ) {
					right.add(object);
				}
				break;
			}
		}
		
		return splitPoint;
	}
	
	private interface KDNode {
		public Collision hitTest( Ray r, float min, float max );
	}
	
	private class KDInternalNodeXAxis implements KDNode {
		
		private float divisionPoint;
		
		private KDNode nodes0, nodes1;
		
		public KDInternalNodeXAxis(float divisionPoint, KDNode leftNode, KDNode rightNode) {
			super();
			this.divisionPoint = divisionPoint;
			
			this.nodes0 = leftNode;
			this.nodes1 = rightNode;
		}
		
		@Override
		public Collision hitTest(Ray r, float min, float max) {
			
			float distance = divisionPoint - r.p.x;
			float thit = distance / r.d.x;
			
			if ( thit < 0 || thit >= max ) {
				// check the half containing the ray pos
				if ( distance < 0 ) {
					return nodes1.hitTest(r, min, max);
				} else {
					return nodes0.hitTest(r, min, max);
				}
			} else if ( thit <= min ) {
				if ( r.d.x < 0 ) {
					return nodes0.hitTest(r, min, max);
				} else {
					return nodes1.hitTest(r, min, max);
				}
			} else {
				Collision c;
				
				// check the half not containing the ray pos
				if ( distance < 0 ) {
					if ( ( c = nodes1.hitTest(r, min, thit) ) == null ) {
						c = nodes0.hitTest(r, thit, max);
					}
				} else {
					if ( ( c = nodes0.hitTest(r, min, thit) ) == null ) {
						c = nodes1.hitTest(r, thit, max);
					}
				}
				
				return c;
			}
		}
		
		@Override
		public String toString() {
			return "point: " + divisionPoint;
		}
	}
	
	private class KDInternalNodeYAxis implements KDNode {
		
		private float divisionPoint;
		
		private KDNode nodes0, nodes1;
		
		public KDInternalNodeYAxis(float divisionPoint, KDNode leftNode, KDNode rightNode) {
			super();
			this.divisionPoint = divisionPoint;
			
			this.nodes0 = leftNode;
			this.nodes1 = rightNode;
		}
		
		@Override
		public Collision hitTest(Ray r, float min, float max) {
			
			float distance = divisionPoint - r.p.y;
			float thit = distance / r.d.y;
			
			if ( thit < 0 || thit >= max ) {
				// check the half containing the ray pos
				if ( distance < 0 ) {
					return nodes1.hitTest(r, min, max);
				} else {
					return nodes0.hitTest(r, min, max);
				}
			} else if ( thit <= min ) {
				if ( r.d.y < 0 ) {
					return nodes0.hitTest(r, min, max);
				} else {
					return nodes1.hitTest(r, min, max);
				}
			} else {
				Collision c;
				
				// check the half not containing the ray pos
				if ( distance < 0 ) {
					if ( ( c = nodes1.hitTest(r, min, thit) ) == null ) {
						c = nodes0.hitTest(r, thit, max);
					}
				} else {
					if ( ( c = nodes0.hitTest(r, min, thit) ) == null ) {
						c = nodes1.hitTest(r, thit, max);
					}
				}
				
				return c;
			}
		}
		
		@Override
		public String toString() {
			return "point: " + divisionPoint;
		}
	}
	
	private class KDInternalNodeZAxis implements KDNode {
		
		private float divisionPoint;
		
		private KDNode nodes0, nodes1;
		
		public KDInternalNodeZAxis(float divisionPoint, KDNode leftNode, KDNode rightNode) {
			super();
			this.divisionPoint = divisionPoint;
			
			this.nodes0 = leftNode;
			this.nodes1 = rightNode;
		}

		@Override
		public Collision hitTest(Ray r, float min, float max) {
			
			float distance = divisionPoint - r.p.z;
			float thit = distance / r.d.z;
			
			if ( thit < 0 || thit >= max ) {
				// check the half containing the ray pos
				if ( distance < 0 ) {
					return nodes1.hitTest(r, min, max);
				} else {
					return nodes0.hitTest(r, min, max);
				}
			} else if ( thit <= min ) {
				if ( r.d.z < 0 ) {
					return nodes0.hitTest(r, min, max);
				} else {
					return nodes1.hitTest(r, min, max);
				}
			} else {
				Collision c;
				
				// check the half not containing the ray pos
				if ( distance < 0 ) {
					if ( ( c = nodes1.hitTest(r, min, thit) ) == null ) {
						c = nodes0.hitTest(r, thit, max);
					}
				} else {
					if ( ( c = nodes0.hitTest(r, min, thit) ) == null ) {
						c = nodes1.hitTest(r, thit, max);
					}
				}
				
				return c;
			}
		}
		
		@Override
		public String toString() {
			return "point: " + divisionPoint;
		}
	}
	
	private class KDLeaf implements KDNode {
		
		private Object3D objects[];
		
		private final static float EPSILON = 0.01f;
		
		public KDLeaf( List<Object3D> objects ) {
			
			this.objects = new Object3D[objects.size()];
			
			for ( int i = 0; i < this.objects.length; i++ ) {
				this.objects[i] = objects.get(i);
			}
		}

		@Override
		public Collision hitTest(Ray r, float min, float max) {
			
			Object3D collidedObject = null;
			Point3D hitPoint = null, bestHitPoint = null;
			float minDistance = Float.POSITIVE_INFINITY;
			float distance;
			
			for ( int i = 0; i < this.objects.length; i++ ) {
				if ( ( hitPoint = this.objects[i].getHitPoint(r) ) != null ) {
					distance = r.p.getDistanceSquared( hitPoint );
					if ( distance < minDistance ) {
						minDistance = distance;
						
						collidedObject = this.objects[i];
						bestHitPoint = hitPoint;
					}
				}
			}
			
			if ( collidedObject == null || minDistance > max * max + KDLeaf.EPSILON ) {
				return null;
			}
			
			r.travelledDistance = (float) Math.sqrt(minDistance);
			return new Collision( bestHitPoint, collidedObject, collidedObject.getNormal( bestHitPoint ), r);
		}
		
		@Override
		public String toString() {
			return "Objects: " + objects;
		}
	}
}
