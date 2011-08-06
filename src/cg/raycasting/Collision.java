package cg.raycasting;

import java.util.LinkedList;
import java.util.List;

import cg.math.Point2D;
import cg.math.Point3D;
import cg.math.Vector3D;
import cg.scene.ColorSample;
import cg.scene.Object3D;
import cg.utils.Color;

public class Collision {

	final protected static float inversePI = 1.0f / (float) Math.PI;

	public Point3D hitPoint;
	private Point2D uv;
	public Object3D object;
	public Vector3D normal;
	public Ray ray;
	private List<ColorSample> samples;
	public boolean isBehind;

	public Collision(Point3D hitPoint, Object3D object, Vector3D normal, Ray ray) {
		super();
		this.hitPoint = hitPoint;
		uv = null;
		this.object = object;
		this.ray = ray;
		samples = new LinkedList<ColorSample>();

		// Make sure this is facing forward!
		if ( ray.d.dotProduct(normal) >= 0.0f ) {
			this.normal = normal.scaleSelf(-1.0f);
			isBehind = true;
		} else {
			this.normal = normal;
			isBehind = false;
		}

		float bias = 0.001f;

		// offset the shaded point away from the surface to prevent
        // self-intersection errors
        if (Math.abs(this.normal.x) > Math.abs(this.normal.y)) {
        	if (Math.abs(this.normal.x) > Math.abs(this.normal.z)) {
        		bias = Math.max(bias, 25.0f * Math.ulp(hitPoint.x));
        	} else {
        		bias = Math.max(bias, 25.0f * Math.ulp(hitPoint.z));
        	}
        } else if (Math.abs(this.normal.y) > Math.abs(this.normal.z)) {
            bias = Math.max(bias, 25.0f * Math.ulp(hitPoint.y));
        } else {
        	bias = Math.max(bias, 25.0f * Math.ulp(hitPoint.z));
        }

        this.hitPoint.translate(this.normal, bias);
	}

	public Point2D getUV() {
		return uv == null? uv = object.getUV(this) :uv;
	}

	public boolean addLightSample(ColorSample colorSample) {

		if ( colorSample == null ) {
			return true;
		}

		return samples.add(colorSample);
	}

	public Color getDiffuse(Color c) {

		if ( c.isBlack() ) {
			return c;
		}

		Color color = new Color();

		for (ColorSample cs : samples) {
			color.madd( cs.dotProduct(normal), cs.diffuse);
		}

		return color.mul(c).mul(Collision.inversePI);
	}
}
