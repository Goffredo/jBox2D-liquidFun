package org.jbox2d.particles;

import java.util.Collection;
import java.util.HashMap;

import org.jbox2d.common.Vec2;

public class ParticleBuffers {

	/*TODO VERY not sure about this.
	 * 
	 * Maybe ArrayList is better? But map overwrites content instead of shifting, so more similar to an array.
	 * 
	 */
	private HashMap<Integer, ParticleGroup> groupMap = new HashMap<Integer, ParticleGroup>(ParticleSettings.bufferCapacity);
	private HashMap<Integer, Vec2> positionMap = new HashMap<Integer, Vec2>();
	private HashMap<Integer, Vec2> velocityMap = new HashMap<Integer, Vec2>();

	private HashMap<Integer, ParticleProxy> proxyMap = new HashMap<Integer, ParticleProxy>();
	
	public void groupAdd(int index, ParticleGroup group) {
		/*Avoiding autoboxing since it may impact performance*/
		groupMap.put(new Integer(index), group);		
	}

	public void positionAdd(int index, Vec2 position) {
		/*Avoiding autoboxing since it may impact performance*/
		positionMap.put(new Integer(index), position);
	}

	public void velocityAdd(int index, Vec2 velocity) {
		/*Avoiding autoboxing since it may impact performance*/
		velocityMap.put(new Integer(index), velocity);
	}

	public void proxyAdd(int i, int index) {
		proxyMap.put(new Integer(index), new ParticleProxy(index));
	}

	public Collection<ParticleProxy> getProxies() {
		return proxyMap.values();
	}

	public Vec2 getPosition(int i) {
		/*Avoiding autoboxing since it may impact performance*/
		return positionMap.get(new Integer(i));
	}

}
