package cg.scene.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cg.math.Point3D;
import cg.raycasting.Collision;
import cg.raycasting.Ray;
import cg.scene.Object3D;

public class SAHKDTree implements SceneTree {

	enum Axis {
		X, Y, Z
	};

	private static final float COST_TRAVERSAL = 0.1f;
	private static final float COST_INTERSECTION = 80.0f * COST_TRAVERSAL;
	private static final float MINIMUM_COST_DIFFERENCE_PER_LEVEL = 0.5f;

	private final SAHKDNode root;

	public SAHKDTree( List<Object3D> objects ) {

		ObjectLimits oLimits = new ObjectLimits(objects);
		int objectCount = objects.size();

		// Get scene limits
		Point3D min = new Point3D( oLimits.minX.get(0), oLimits.minY.get(0), oLimits.minZ.get(0) );
		Point3D max = new Point3D( oLimits.maxX.get(objectCount - 1), oLimits.maxY.get(objectCount - 1), oLimits.maxZ.get(objectCount - 1) );

		SAHKDNode root = splitObjects(objects, Float.POSITIVE_INFINITY, min, max, oLimits);

		// If no good partitions are possible, just use a leaf
		if (null == root) {
			root = new SAHKDLeaf(objects);
		}

		this.root = root;
	}

	@Override
	public final Collision hitTest(final Ray r) {
		return root.hitTest(r, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY );
	}

	private SAHKDNode splitObjects(List<Object3D> objects, float prevCost, Point3D min, Point3D max, ObjectLimits oLimits) {

		List<Object3D> left = new LinkedList<Object3D>();
		List<Object3D> right = new LinkedList<Object3D>();
		float bestSplitPoint = Float.NEGATIVE_INFINITY;
		float splitPoint = 0.0f;
		float bestSplitCost = Float.POSITIVE_INFINITY;
		float splitCost;
		int leftCount, rightCount;
		int objectCount = objects.size();
		int lastIndexMinX = oLimits.minX.size() - 1;
		int lastIndexMaxX = oLimits.maxX.size() - 1;
		int lastIndexMinY = oLimits.minY.size() - 1;
		int lastIndexMaxY = oLimits.maxY.size() - 1;
		int lastIndexMinZ = oLimits.minZ.size() - 1;
		int lastIndexMaxZ = oLimits.maxZ.size() - 1;
		int minIndex, maxIndex;
		Axis splitAxis = null;
		float x, y, z;
		float leftArea, rightArea;
		float curMin, curMax;

		leftCount = 0;
		rightCount = objectCount;
		minIndex = maxIndex = 0;

		// Try X axis
		y = Math.abs(max.y - min.y);
		z = Math.abs(max.z - min.z);

		if ( lastIndexMinX < 0 ) {
			curMin = Float.POSITIVE_INFINITY;
		} else {
			curMin = oLimits.minX.get(minIndex);
		}

		if ( lastIndexMaxX < 0 ) {
			curMax = Float.NEGATIVE_INFINITY;
		} else {
			curMax = oLimits.maxX.get(maxIndex);
		}

		while ( maxIndex < lastIndexMaxX || minIndex < lastIndexMinX ) {
			splitPoint = Math.min(curMin, curMax);

			// Compute the cost of splitting here
			x = Math.abs(splitPoint - min.x);
			leftArea = 2.0f * ( x * y + y * z + z * x );

			x = Math.abs(max.x - splitPoint);
			rightArea = 2.0f * ( x * y + y * z + z * x );

			splitCost = COST_TRAVERSAL + COST_INTERSECTION * (leftArea * leftCount + rightArea * rightCount);

			// Move on to the next position
			if (curMin < curMax && minIndex < lastIndexMinX) {
				leftCount++;

				curMin = oLimits.minX.get(++minIndex);

				// Take care of duplicate values
				while (minIndex < lastIndexMinX && oLimits.minX.get(minIndex + 1) == splitPoint) {
					minIndex++;
					leftCount++;
				}
			} else if (curMin == curMax) {
				rightCount--;
				leftCount++;

				if (minIndex < lastIndexMinX) {
					curMin = oLimits.minX.get(++minIndex);
				} else {
					curMin = Float.POSITIVE_INFINITY;
				}

				if (maxIndex < lastIndexMaxX) {
					curMax = oLimits.maxX.get(++maxIndex);
				} else {
					curMax = Float.POSITIVE_INFINITY;
				}

				// Take care of duplicate values
				while (minIndex < lastIndexMinX && oLimits.minX.get(minIndex + 1) == splitPoint) {
					minIndex++;
					leftCount++;
				}

				// Take care of duplicate values
				while (maxIndex < lastIndexMaxX && oLimits.maxX.get(maxIndex + 1) == splitPoint) {
					maxIndex++;
					rightCount--;
				}
			} else {
				rightCount--;

				if ( maxIndex < lastIndexMaxX ) {
					curMax = oLimits.maxX.get(++maxIndex);
				} else {
					curMax = Float.POSITIVE_INFINITY;
				}

				// Take care of duplicate values
				while (maxIndex < lastIndexMaxX && oLimits.maxX.get(maxIndex + 1) == splitPoint) {
					maxIndex++;
					rightCount--;
				}
			}

			// This may happen if we are checking for a border of an object that is partially in this subspace, but the checked border is outside
			if (leftArea < 0.0f || rightArea < 0.0f) {
				continue;
			}

			if ( splitCost < bestSplitCost ) {
				bestSplitCost = splitCost;
				bestSplitPoint = splitPoint;

				splitAxis = Axis.X;
			}
		}


		leftCount = 0;
		rightCount = objectCount;
		minIndex = maxIndex = 0;

		// Try Y axis
		x = Math.abs(max.x - min.x);
		z = Math.abs(max.z - min.z);

		if ( lastIndexMinY < 0 ) {
			curMin = Float.POSITIVE_INFINITY;
		} else {
			curMin = oLimits.minY.get(minIndex);
		}

		if ( lastIndexMaxY < 0 ) {
			curMax = Float.NEGATIVE_INFINITY;
		} else {
			curMax = oLimits.maxY.get(maxIndex);
		}

		while ( maxIndex < lastIndexMaxY || minIndex < lastIndexMinY ) {
			splitPoint = Math.min(curMin, curMax);

			// Compute the cost of splitting here
			y = Math.abs(splitPoint - min.y);
			leftArea = 2.0f * ( x * y + y * z + z * x );

			y = Math.abs(max.y - splitPoint);
			rightArea = 2.0f * ( x * y + y * z + z * x );

			splitCost = COST_TRAVERSAL + COST_INTERSECTION * (leftArea * leftCount + rightArea * rightCount);

			// Move on to the next position
			if (curMin < curMax && minIndex < lastIndexMinX) {
				leftCount++;

				curMin = oLimits.minY.get(++minIndex);

				// Take care of duplicate values
				while (minIndex < lastIndexMinY && oLimits.minY.get(minIndex + 1) == splitPoint) {
					minIndex++;
					leftCount++;
				}
			} else if (curMin == curMax) {
				rightCount--;
				leftCount++;

				if (minIndex < lastIndexMinY) {
					curMin = oLimits.minY.get(++minIndex);
				} else {
					curMin = Float.POSITIVE_INFINITY;
				}

				if (maxIndex < lastIndexMaxY) {
					curMax = oLimits.maxY.get(++maxIndex);
				} else {
					curMax = Float.POSITIVE_INFINITY;
				}

				// Take care of duplicate values
				while (minIndex < lastIndexMinY && oLimits.minY.get(minIndex + 1) == splitPoint) {
					minIndex++;
					leftCount++;
				}

				// Take care of duplicate values
				while (maxIndex < lastIndexMaxY && oLimits.maxY.get(maxIndex + 1) == splitPoint) {
					maxIndex++;
					rightCount--;
				}
			} else {
				rightCount--;

				if (maxIndex < lastIndexMaxY) {
					curMax = oLimits.maxY.get(++maxIndex);
				} else {
					curMax = Float.POSITIVE_INFINITY;
				}

				// Take care of duplicate values
				while (maxIndex < lastIndexMaxY && oLimits.maxY.get(maxIndex + 1) == splitPoint) {
					maxIndex++;
					rightCount--;
				}
			}

			// This may happen if we are checking for a border of an object that is partially in this subspace, but the checked border is outside
			if (leftArea < 0.0f || rightArea < 0.0f) {
				continue;
			}

			if ( splitCost < bestSplitCost ) {
				bestSplitCost = splitCost;
				bestSplitPoint = splitPoint;

				splitAxis = Axis.Y;
			}
		}


		leftCount = 0;
		rightCount = objectCount;
		minIndex = maxIndex = 0;

		// Try Z axis
		x = Math.abs(max.x - min.x);
		y = Math.abs(max.y - min.y);

		if ( lastIndexMinZ < 0 ) {
			curMin = Float.POSITIVE_INFINITY;
		} else {
			curMin = oLimits.minZ.get(minIndex);
		}

		if ( lastIndexMaxZ < 0 ) {
			curMax = Float.NEGATIVE_INFINITY;
		} else {
			curMax = oLimits.maxZ.get(maxIndex);
		}

		while ( maxIndex < lastIndexMaxZ || minIndex < lastIndexMinZ ) {
			splitPoint = Math.min(curMin, curMax);

			// Compute the cost of splitting here
			z = Math.abs(splitPoint - min.z);
			leftArea = 2.0f * ( x * y + y * z + z * x );

			z = Math.abs(max.z - splitPoint);
			rightArea = 2.0f * ( x * y + y * z + z * x );

			splitCost = COST_TRAVERSAL + COST_INTERSECTION * (leftArea * leftCount + rightArea * rightCount);

			// Move on to the next position
			if (curMin < curMax && minIndex < lastIndexMinZ) {
				leftCount++;

				curMin = oLimits.minZ.get(++minIndex);

				// Take care of duplicate values
				while (minIndex < lastIndexMinZ && oLimits.minZ.get(minIndex + 1) == splitPoint) {
					minIndex++;
					leftCount++;
				}
			} else if (curMin == curMax) {
				rightCount--;
				leftCount++;

				if (minIndex < lastIndexMinZ) {
					curMin = oLimits.minZ.get(++minIndex);
				} else {
					curMin = Float.POSITIVE_INFINITY;
				}

				if (maxIndex < lastIndexMaxZ) {
					curMax = oLimits.maxZ.get(++maxIndex);
				} else {
					curMax = Float.POSITIVE_INFINITY;
				}

				// Take care of duplicate values
				while (minIndex < lastIndexMinZ && oLimits.minZ.get(minIndex + 1) == splitPoint) {
					minIndex++;
					leftCount++;
				}

				// Take care of duplicate values
				while (maxIndex < lastIndexMaxZ && oLimits.maxZ.get(maxIndex + 1) == splitPoint) {
					maxIndex++;
					rightCount--;
				}
			} else {
				rightCount--;

				if (maxIndex < lastIndexMaxZ) {
					curMax = oLimits.maxZ.get(++maxIndex);
				} else {
					curMax = Float.POSITIVE_INFINITY;
				}

				// Take care of duplicate values
				while (maxIndex < lastIndexMaxZ && oLimits.maxZ.get(maxIndex + 1) == splitPoint) {
					maxIndex++;
					rightCount--;
				}
			}

			// This may happen if we are checking for a border of an object that is partially in this subspace, but the checked border is outside
			if (leftArea < 0.0f || rightArea < 0.0f) {
				continue;
			}

			if ( splitCost < bestSplitCost ) {
				bestSplitCost = splitCost;
				bestSplitPoint = splitPoint;

				splitAxis = Axis.Z;
			}
		}

		if (splitAxis == null) {
			// No good split found, revert!
			return null;
		}

		// Actually split objects!
		for (Object3D object : objects) {
			switch ( splitAxis ) {
			case X:
				if ( object.getMinXCoord() < bestSplitPoint ) {
					left.add(object);
				}

				if ( object.getMaxXCoord() >= bestSplitPoint ) {
					right.add(object);
				}
				break;

			case Y:
				if ( object.getMinYCoord() < bestSplitPoint ) {
					left.add(object);
				}

				if ( object.getMaxYCoord() >= bestSplitPoint ) {
					right.add(object);
				}
				break;

			case Z:
				if ( object.getMinZCoord() < bestSplitPoint ) {
					left.add(object);
				}

				if ( object.getMaxZCoord() >= bestSplitPoint ) {
					right.add(object);
				}
				break;
			}
		}

		if (left.size() == objects.size() && right.size() == objects.size()) {
			// We didn't split a thing! Revert!
			return null;
		}

		if (prevCost - bestSplitCost < MINIMUM_COST_DIFFERENCE_PER_LEVEL) {
			// The split was meaningless, revert!
			return null;
		}

		// Construct new object limits for left and right
		ObjectLimits leftLimits = new ObjectLimits(left);
		ObjectLimits rightLimits = new ObjectLimits(right);

		// Create SAHKDNodes and recurse!
		SAHKDNode leftNode = null, rightNode = null;

		if (left.size() > 1) {
			Point3D newMax = null;
			switch (splitAxis) {
			case X:
				newMax = new Point3D(bestSplitPoint, max.y, max.z);
				break;

			case Y:
				newMax = new Point3D(max.x, bestSplitPoint, max.z);
				break;

			case Z:
				newMax = new Point3D(max.x, max.y, bestSplitPoint);
				break;
			}

			leftNode = splitObjects(left, bestSplitCost, min, newMax, leftLimits);
		}

		// If the split was meaningless, just use a leaf
		if (leftNode == null) {
			leftNode = new SAHKDLeaf(left);
		}

		if (right.size() > 1) {
			Point3D newMin = null;
			switch (splitAxis) {
			case X:
				newMin = new Point3D(bestSplitPoint, min.y, min.z);
				break;

			case Y:
				newMin = new Point3D(min.x, bestSplitPoint, min.z);
				break;

			case Z:
				newMin = new Point3D(min.x, min.y, bestSplitPoint);
				break;
			}

			rightNode = splitObjects(right, bestSplitCost, newMin, max, rightLimits);
		}

		// If the split was meaningless, just use a leaf
		if (rightNode == null) {
			rightNode = new SAHKDLeaf(right);
		}

		switch (splitAxis) {
		case X:
			return new SAHKDInternalNodeXAxis(bestSplitPoint, leftNode, rightNode);

		case Y:
			return new SAHKDInternalNodeYAxis(bestSplitPoint, leftNode, rightNode);

		case Z:
			return new SAHKDInternalNodeZAxis(bestSplitPoint, leftNode, rightNode);
		}

		// Should never happen...
		return null;
	}

	private interface SAHKDNode {
		public Collision hitTest(final Ray r, final float min, final float max);
	}

	private class SAHKDInternalNodeXAxis implements SAHKDNode {

		private final float divisionPoint;

		private final SAHKDNode nodes0, nodes1;

		public SAHKDInternalNodeXAxis(float divisionPoint, SAHKDNode leftNode, SAHKDNode rightNode) {
			super();
			this.divisionPoint = divisionPoint;

			nodes0 = leftNode;
			nodes1 = rightNode;
		}

		@Override
		public Collision hitTest(final Ray r, final float min, final float max) {

			final float distance = divisionPoint - r.p.x;
			final float thit = distance * r.id.x;

			if ( thit < 0.0f || thit >= max ) {
				// check the half containing the ray pos
				if ( distance < 0.0f ) {
					return nodes1.hitTest(r, min, max);
				} else {
					return nodes0.hitTest(r, min, max);
				}
			} else if ( thit <= min ) {
				if ( r.d.x < 0.0f ) {
					return nodes0.hitTest(r, min, max);
				} else {
					return nodes1.hitTest(r, min, max);
				}
			} else {
				Collision c;

				// check the half not containing the ray pos
				if ( distance < 0.0f ) {
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

	private class SAHKDInternalNodeYAxis implements SAHKDNode {

		private final float divisionPoint;

		private final SAHKDNode nodes0, nodes1;

		public SAHKDInternalNodeYAxis(final float divisionPoint, final SAHKDNode leftNode, final SAHKDNode rightNode) {
			this.divisionPoint = divisionPoint;

			nodes0 = leftNode;
			nodes1 = rightNode;
		}

		@Override
		public Collision hitTest(final Ray r, final float min, final float max) {
			final float distance = divisionPoint - r.p.y;
			final float thit = distance * r.id.y;

			if (thit < 0.0f || thit >= max) {
				// check the half containing the ray pos
				if (distance < 0.0f) {
					return nodes1.hitTest(r, min, max);
				} else {
					return nodes0.hitTest(r, min, max);
				}
			} else if (thit <= min) {
				if (r.d.y < 0.0f) {
					return nodes0.hitTest(r, min, max);
				} else {
					return nodes1.hitTest(r, min, max);
				}
			} else {
				Collision c;

				// check the half not containing the ray pos
				if (distance < 0.0f) {
					if ((c = nodes1.hitTest(r, min, thit)) == null) {
						c = nodes0.hitTest(r, thit, max);
					}
				} else {
					if ((c = nodes0.hitTest(r, min, thit)) == null) {
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

	private class SAHKDInternalNodeZAxis implements SAHKDNode {

		private final float divisionPoint;

		private final SAHKDNode nodes0, nodes1;

		public SAHKDInternalNodeZAxis(float divisionPoint, SAHKDNode leftNode, SAHKDNode rightNode) {
			super();
			this.divisionPoint = divisionPoint;

			nodes0 = leftNode;
			nodes1 = rightNode;
		}

		@Override
		public Collision hitTest(final Ray r, final float min, final float max) {

			final float distance = divisionPoint - r.p.z;
			final float thit = distance * r.id.z;

			if ( thit < 0.0f || thit >= max ) {
				// check the half containing the ray pos
				if ( distance < 0.0f ) {
					return nodes1.hitTest(r, min, max);
				} else {
					return nodes0.hitTest(r, min, max);
				}
			} else if ( thit <= min ) {
				if ( r.d.z < 0.0f ) {
					return nodes0.hitTest(r, min, max);
				} else {
					return nodes1.hitTest(r, min, max);
				}
			} else {
				Collision c;

				// check the half not containing the ray pos
				if ( distance < 0.0f ) {
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

	private class SAHKDLeaf implements SAHKDNode {

		private final Object3D objects[];

		public SAHKDLeaf(final List<Object3D> objects) {

			this.objects = new Object3D[objects.size()];

			for (int i = 0; i < this.objects.length; i++) {
				this.objects[i] = objects.get(i);
			}
		}

		@Override
		public Collision hitTest(final Ray r, final float min, final float max) {

			Object3D collidedObject = null;
			Point3D hitPoint = null, bestHitPoint = null;

			for (int i = objects.length - 1; i >= 0; i--) {
				if ((hitPoint = objects[i].getHitPoint(r)) != null) {
					collidedObject = objects[i];
					bestHitPoint = hitPoint;
				}
			}


			if (collidedObject == null || r.travelledDistance > max) {
				return null;
			}

			return new Collision(bestHitPoint, collidedObject, collidedObject.getNormal(bestHitPoint), r);
		}

		@Override
		public String toString() {
			return "Objects: " + objects;
		}
	}

	private class ObjectLimits {

		private List<Float> minX;
		private List<Float> maxX;
		private List<Float> minY;
		private List<Float> maxY;
		private List<Float> minZ;
		private List<Float> maxZ;

		public ObjectLimits(List<Object3D> objects) {

			int objectCount = objects.size();

			minX = new ArrayList<Float>(objectCount);
			maxX = new ArrayList<Float>(objectCount);
			minY = new ArrayList<Float>(objectCount);
			maxY = new ArrayList<Float>(objectCount);
			minZ = new ArrayList<Float>(objectCount);
			maxZ = new ArrayList<Float>(objectCount);

			for (Object3D object : objects) {
				minX.add(object.getMinXCoord());
				maxX.add(object.getMaxXCoord());

				minY.add(object.getMinYCoord());
				maxY.add(object.getMaxYCoord());

				minZ.add(object.getMinZCoord());
				maxZ.add(object.getMaxZCoord());
			}

			Collections.sort(minX);
			Collections.sort(maxX);
			Collections.sort(minY);
			Collections.sort(maxY);
			Collections.sort(minZ);
			Collections.sort(maxZ);
		}
	}
}
