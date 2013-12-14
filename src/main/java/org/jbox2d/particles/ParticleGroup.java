package org.jbox2d.particles;

import org.jbox2d.common.Transform;

public class ParticleGroup {
	
	/*
	 * Public fields
	 */
	public int firstIndex;
	public int lastIndex;

	private ParticleSystem particleSystem;
	private Transform transform;
	private boolean destroyAutomatically;


	public void setParticleSystem(ParticleSystem particleSystem) {
		this.particleSystem = particleSystem;
	}


	public void setTransform(Transform transform) {
		this.transform = transform;
	}


	public void setDestroyAutomatically(boolean destroyAutomatically) {
		this.destroyAutomatically = destroyAutomatically;
	}

}
