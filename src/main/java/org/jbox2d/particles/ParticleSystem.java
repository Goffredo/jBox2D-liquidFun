package org.jbox2d.particles;

import java.util.LinkedList;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;



public class ParticleSystem {

	final static int xTruncBits = 12;
	final static int yTruncBits = 12;
	final static int tagBits = 8 * 4; //int always 32 bit in jvm
	final static int yOffset = 1 << (yTruncBits - 1); // 0000 0000 0000 0000 0000 1000 0000 0000
	final static int yShift = tagBits - yTruncBits; // 32-12 = 20
	final static int xShift = tagBits - yTruncBits - xTruncBits;  // 32-12-12 = 8
	final static int xScale = 1 << xShift; // 0000 0000 0000 0000 0000 0001 0000 0000 when multiplying by this value, we are shifting 8 bits
	final static int xOffset = xScale * (1 << (xTruncBits - 1)); // 0000 0000 0000 1000 0000 0000 0000 0000 2^19

	int m_timestamp;
	int m_allParticleFlags;
	int m_allGroupFlags;
	float m_density;
	float m_inverseDensity;
	float m_gravityScale;
	float m_particleDiameter;
	float m_inverseDiameter;
	float m_squaredDiameter;

	int count;
	int m_internalAllocatedCapacity;
	int m_maxCount;
	//ParticleBuffer<uint> m_flagsBuffer;
	//ParticleBuffer<b2Vec2> m_positionBuffer;
	//ParticleBuffer<b2Vec2> m_velocityBuffer;
	//float* m_accumulationBuffer; // temporary values
	//b2Vec2* m_accumulation2Buffer; // temporary vector values
	//float* m_depthBuffer; // distance from the surface
	//ParticleBuffer<b2ParticleColor> m_colorBuffer;
	ParticleBuffers particleBuffers = new ParticleBuffers();
	//ParticleBuffer<void*> m_userDataBuffer;

	int proxyCount;
	int m_proxyCapacity;
	//Proxy* m_proxyBuffer;

	int m_contactCount;
	int m_contactCapacity;
	//b2ParticleContact* m_contactBuffer;

	int m_bodyContactCount;
	int m_bodyContactCapacity;
	//b2ParticleBodyContact* m_bodyContactBuffer;

	int m_pairCount;
	int m_pairCapacity;
	//Pair* m_pairBuffer;

	int m_triadCount;
	int m_triadCapacity;
	//Triad* m_triadBuffer;

	int m_groupCount;
	private LinkedList<ParticleGroup> groupList = new LinkedList<ParticleGroup>();

	float m_pressureStrength;
	float m_dampingStrength;
	float m_elasticStrength;
	float m_springStrength;
	float m_viscousStrength;
	float m_surfaceTensionStrengthA;
	float m_surfaceTensionStrengthB;
	float m_powderStrength;
	float m_ejectionStrength;
	float m_colorMixingStrength;

	public ParticleSystem(){

		m_timestamp = 0;
		m_allParticleFlags = 0;
		m_allGroupFlags = 0;
		m_density = 1;
		m_inverseDensity = 1;
		m_gravityScale = 1;
		m_particleDiameter = 1;
		m_inverseDiameter = 1;
		m_squaredDiameter = 1;

		count = 0;
		m_internalAllocatedCapacity = 0;
		m_maxCount = 0;
		//m_accumulationBuffer = NULL;
		//m_accumulation2Buffer = NULL;
		//m_depthBuffer = NULL;
		//m_groupBuffer = NULL;

		proxyCount = 0;
		m_proxyCapacity = 0;
		//m_proxyBuffer = NULL;

		m_contactCount = 0;
		m_contactCapacity = 0;
		//m_contactBuffer = NULL;

		m_bodyContactCount = 0;
		m_bodyContactCapacity = 0;
		//m_bodyContactBuffer = NULL;

		m_pairCount = 0;
		m_pairCapacity = 0;
		//m_pairBuffer = NULL;

		m_triadCount = 0;
		m_triadCapacity = 0;
		//m_triadBuffer = NULL;

		m_groupCount = 0;
		//m_groupList = NULL;

		m_pressureStrength = 0.05f;
		m_dampingStrength = 1.0f;
		m_elasticStrength = 0.25f;
		m_springStrength = 0.25f;
		m_viscousStrength = 0.25f;
		m_surfaceTensionStrengthA = 0.1f;
		m_surfaceTensionStrengthB = 0.2f;
		m_powderStrength = 0.5f;
		m_ejectionStrength = 0.5f;
		m_colorMixingStrength = 0.5f;
	}

	public ParticleGroup createParticleGroup(ParticleGroupDef groupDef) {
		float stride = getParticleStride();
		Transform identity = new Transform();
		identity.setIdentity();
		Transform transform = identity;
		int firstIndex = count;
		if (groupDef.getShape() != null);
		{
			ParticleDef particleDef = new ParticleDef();
			//TODO particleDef.setFlags(groupDef.getFlags());
			//TODO particleDef.color = groupDef.color;
			//TODO particleDef.userData = groupDef.userData;
			final Shape shape = groupDef.getShape();
			transform.set(groupDef.getPosition(), groupDef.getAngle());
			AABB aabb = new AABB();
			int childCount = shape.getChildCount();
			for (int childIndex = 0; childIndex < childCount; childIndex++)
			{
				if (childIndex == 0)
				{
					shape.computeAABB(aabb, identity, childIndex);
				}
				else
				{
					AABB childAABB = new AABB();
					shape.computeAABB(childAABB, identity, childIndex);
					aabb.combine(childAABB);
				}
			}
			for (float y = MathUtils.floor(aabb.lowerBound.y / stride) * stride; y < aabb.upperBound.y; y += stride)
			{
				for (float x = MathUtils.floor(aabb.lowerBound.x / stride) * stride; x < aabb.upperBound.x; x += stride)
				{
					Vec2 p = new Vec2(x, y);
					if (shape.testPoint(identity, p))
					{
						p = Transform.mul(transform, p);
						particleDef.setPosition(p);
						particleDef.setVelocity(
							groupDef.getLinearVelocity().add(
							Vec2.cross(groupDef.getAngularVelocity(), p.sub(groupDef.getPosition()))));
						createParticle(particleDef);
					}
				}
			}
		}
		int lastIndex = count;

		ParticleGroup group = new ParticleGroup();
		group.setParticleSystem(this);
		group.firstIndex = firstIndex;
		group.lastIndex = lastIndex;
		//TODO group->m_groupFlags = groupDef.groupFlags;
		//TODO group->m_strength = groupDef.strength;
		//TODO group->m_userData = groupDef.userData;
		group.setTransform(transform);
		group.setDestroyAutomatically(groupDef.isDestroyAutomatically());
		groupList.add(group);
		
		//TODO ++m_groupCount; maybe not needed since we already have a list
		
		for (int i = firstIndex; i < lastIndex; i++)
		{
			particleBuffers.groupAdd(i, group);
		}
		
		updateContacts(true);
		
		/* TODO this block  is needed to implement particles that are not liquid
		if (groupDef.flags & k_pairFlags)
		{
			for (int k = 0; k < m_contactCount; k++)
			{
				const ParticleContact& contact = m_contactBuffer[k];
				int a = contact.indexA;
				int b = contact.indexB;
				if (a > b) Swap(a, b);
				if (firstIndex <= a && b < lastIndex)
				{
					if (m_pairCount >= m_pairCapacity)
					{
						int oldCapacity = m_pairCapacity;
						int newCapacity = m_pairCount ? 2 * m_pairCount : _minParticleBufferCapacity;
						m_pairBuffer = ReallocateBuffer(m_pairBuffer, oldCapacity, newCapacity);
						m_pairCapacity = newCapacity;
					}
					Pair& pair = m_pairBuffer[m_pairCount];
					pair.indexA = a;
					pair.indexB = b;
					pair.flags = contact.flags;
					pair.strength = groupDef.strength;
					pair.distance = Distance(
						m_positionBuffer.data[a],
						m_positionBuffer.data[b]);
					m_pairCount++;
				}
			}
		}
		if (groupDef.flags & k_triadFlags)
		{
			VoronoiDiagram diagram(
				&m_world->m_stackAllocator, lastIndex - firstIndex);
			for (int i = firstIndex; i < lastIndex; i++)
			{
				diagram.AddGenerator(m_positionBuffer.data[i], i);
			}
			diagram.Generate(stride / 2);
			CreateParticleGroupCallback callback;
			callback.system = this;
			callback.def = &groupDef;
			callback.firstIndex = firstIndex;
			diagram.GetNodes(callback);
		}
		if (groupDef.groupFlags & _solidParticleGroup)
		{
			ComputeDepthForGroup(group);
		}
		*/
		return group;
	}
	
	private void updateContacts(boolean b) {
		
		for (ParticleProxy particleProxy : particleBuffers.getProxies())
		{
			int i = particleProxy.index;
			Vec2 p = particleBuffers.getPosition(i);
			particleProxy.tag = computeTag(m_inverseDiameter * p.x, m_inverseDiameter * p.y);
		}
		
		std::sort(beginParticleProxy, endParticleProxy);
		m_contactCount = 0;
		for (ParticleProxy *a = beginParticleProxy, *c = beginParticleProxy; a < endParticleProxy; a++)
		{
			uint32 rightTag = computeRelativeTag(a->tag, 1, 0);
			for (ParticleProxy* b = a + 1; b < endParticleProxy; b++)
			{
				if (rightTag < b->tag) break;
				AddContact(a->index, b->index);
			}
			uint32 bottomLeftTag = computeRelativeTag(a->tag, -1, 1);
			for (; c < endParticleProxy; c++)
			{
				if (bottomLeftTag <= c->tag) break;
			}
			uint32 bottomRightTag = computeRelativeTag(a->tag, 1, 1);
			for (ParticleProxy* b = c; b < endParticleProxy; b++)
			{
				if (bottomRightTag < b->tag) break;
				AddContact(a->index, b->index);
			}
		}
		if (exceptZombie)
		{
			b2ParticleContact* lastContact = std::remove_if(
				m_contactBuffer, m_contactBuffer + m_contactCount,
				b2ParticleContactIsZombie);
			m_contactCount = (int32) (lastContact - m_contactBuffer);
		}
	}

	private final static int computeTag(float x, float y) {
		/* why doesn't java have unsigned ints? :'(
		 * apart from being terribly slow in java, this piece of code, as I understood it, simply
		 * "hashes" the proxy based on its position.
		 * The first 12 bits are composed of an hash of the y value, while the last 20 of an hash of the x.
		 * 
		 * The y value is taken and 2048(2^11) is added to it, not sure actually why.
		 * Then it's converted to unsigned int, and shifted so that only the first 12 bits are occupied.
		 * 
		 * The x value is taken and multiplied by 2^8 then 2^19 is added (unknown reason)
		 * Lastly it's converted to an unsigned int
		 */
		return (int) (((((long)(y + yOffset))&0xFFFFFFFF) << yShift) + ((long)(xScale * x + xOffset)&0xFFFFFFFF));
	}

	private int createParticle(ParticleDef def) {
		int index = count++;
		//TODO m_flagsBuffer.data[index] = def.flags;
		particleBuffers.positionAdd(index, def.getPosition());
		particleBuffers.velocityAdd(index, def.getVelocity());
		particleBuffers.groupAdd(index, null);
		
		particleBuffers.proxyAdd(proxyCount++, index);
		
		return index;
	}

	private float getParticleStride() {
		return ParticleSettings.particleStride * m_particleDiameter;
	}

}