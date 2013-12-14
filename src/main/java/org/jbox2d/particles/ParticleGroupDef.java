package org.jbox2d.particles;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

public class ParticleGroupDef {

	private Shape shape;
	private Vec2 position;
	private float angle;
	private Vec2 linearVelocity;
	private float angularVelocity;
	private boolean destroyAutomatically;

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Shape getShape() {
		return shape;
	}

	public Object getFlags() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vec2 getPosition() {
		return position;
	}

	public float getAngle() {
		return angle;
	}

	public Vec2 getLinearVelocity() {
		return linearVelocity;
	}

	public float getAngularVelocity() {
		return angularVelocity;
	}

	public boolean isDestroyAutomatically() {
		return destroyAutomatically;
	}

}