/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.event;

import software.bernie.geckolib3.core.engine.AnimationChannel;

public class SoundKeyframeEvent<T> extends KeyframeEvent<T> {
	public final String sound;

	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *
	 * @param animationTick The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param sound         The name of the sound to play
	 * @param controller    the controller
	 */
	public SoundKeyframeEvent(double animationTick, String sound, AnimationChannel<T> controller) {
		super(animationTick, controller);
		this.sound = sound;
	}
}
