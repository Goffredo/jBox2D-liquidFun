package org.jbox2d.particles;

import java.util.Map;

public class ParticleSettings {
	/**
	 * The standard distance between particles, divided by the particle radius.
	 */
	public static final float	particleStride = 0.75f;
	
	/**
	 * Initial buckets for the hashmaps used for particle storage
	 */
	public static final int		bufferCapacity = 256;

}
