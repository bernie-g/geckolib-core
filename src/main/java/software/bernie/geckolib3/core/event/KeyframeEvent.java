/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.event;

import software.bernie.geckolib3.core.engine.AnimationChannel;

public abstract class KeyframeEvent<T> {
	private final T entity;
	private final double animationTick;
	private final AnimationChannel<T> controller;

	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *
	 * @param animationTick The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param controller    the controller
	 */
	public KeyframeEvent(double animationTick, AnimationChannel<T> controller) {
		this.entity = controller.getEntity();
		this.animationTick = animationTick;
		this.controller = controller;
	}

	/**
	 * Gets the amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 *
	 * @return the animation tick
	 */
	public double getAnimationTick() {
		return animationTick;
	}

	public T getEntity() {
		return entity;
	}

	public AnimationChannel<T> getController() {
		return controller;
	}
}
