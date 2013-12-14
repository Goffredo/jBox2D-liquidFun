package org.jbox2d.particles;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;


public class ParticleSystem {

	int m_timestamp;
	int m_allParticleFlags;
	int m_allGroupFlags;
	float m_density;
	float m_inverseDensity;
	float m_gravityScale;
	float m_particleDiameter;
	float m_inverseDiameter;
	float m_squaredDiameter;

	int m_count;
	int m_internalAllocatedCapacity;
	int m_maxCount;
	//ParticleBuffer<uint> m_flagsBuffer;
	//ParticleBuffer<b2Vec2> m_positionBuffer;
	//ParticleBuffer<b2Vec2> m_velocityBuffer;
	//float* m_accumulationBuffer; // temporary values
	//b2Vec2* m_accumulation2Buffer; // temporary vector values
	//float* m_depthBuffer; // distance from the surface
	//ParticleBuffer<b2ParticleColor> m_colorBuffer;
	//b2ParticleGroup** m_groupBuffer;
	//ParticleBuffer<void*> m_userDataBuffer;

	int m_proxyCount;
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
	private Vec2 groupList;

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

		m_count = 0;
		m_internalAllocatedCapacity = 0;
		m_maxCount = 0;
		//m_accumulationBuffer = NULL;
		//m_accumulation2Buffer = NULL;
		//m_depthBuffer = NULL;
		//m_groupBuffer = NULL;

		m_proxyCount = 0;
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
		//TODO finish porting
		return null;
		/*
		float stride = getParticleStride();
		Transform identity = new Transform();
		identity.setIdentity();
		Transform transform = identity;
		int firstIndex = m_count;
		if (groupDef.getShape() != null);
		{
			ParticleDef particleDef = new ParticleDef();
			//TODO particleDef.setFlags(groupDef.getFlags());
			//TODO particleDef.color = groupDef.color;
			//TODO particleDef.userData = groupDef.userData;
			final Shape shape = groupDef.getShape();
			transform.set(groupDef.getPosition(), groupDef.getAngle());
			AABB aabb;
			int childCount = shape.getChildCount();
			for (int childIndex = 0; childIndex < childCount; childIndex++)
			{
				if (childIndex == 0)
				{
					shape.computeAABB(aabb, identity, childIndex);
				}
				else
				{
					AABB childAABB;
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
		int lastIndex = m_count;

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
		group->m_prev = NULL;
		group->m_next = m_groupList;
		if (m_groupList)
		{
			m_groupList->m_prev = group;
		}
		m_groupList = group;
		++m_groupCount;
		for (int i = firstIndex; i < lastIndex; i++)
		{
			m_groupBuffer[i] = group;
		}

		UpdateContacts(true);
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

		return group;*/
	}
	/*
	private float getParticleStride() {
		return b2_particleStride * m_particleDiameter;
	}*/

}