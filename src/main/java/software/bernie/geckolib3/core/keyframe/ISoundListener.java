package software.bernie.geckolib3.core.keyframe;

import software.bernie.geckolib3.core.event.SoundKeyframeEvent;

/**
 * Sound Listeners are run when a sound keyframe is hit. You can either return the SoundEvent and geckolib will play the sound for you, or return null and handle the sounds yourself.
 */
@FunctionalInterface
public interface ISoundListener<A> {
	/**
	 * Sound Listeners are run when a sound keyframe is hit. You can either return the SoundEvent and geckolib will play the sound for you, or return null and handle the sounds yourself.
	 */
	void playSound(SoundKeyframeEvent<A> event);
}
