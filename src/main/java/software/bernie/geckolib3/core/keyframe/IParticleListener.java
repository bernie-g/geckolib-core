package software.bernie.geckolib3.core.keyframe;

import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;

/**
 * Particle Listeners are run when a sound keyframe is hit. You need to handle the actual playing of the particle yourself.
 */
@FunctionalInterface
public interface IParticleListener<A> {
	/**
	 * Particle Listeners are run when a sound keyframe is hit. You need to handle the actual playing of the particle yourself.
	 */
	void summonParticle(ParticleKeyFrameEvent<A> event);
}
