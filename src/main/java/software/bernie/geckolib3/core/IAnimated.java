/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */
package software.bernie.geckolib3.core;

import software.bernie.geckolib3.core.manager.AnimationData;

/**
 * This interface must be applied to any object that wants to be animated
 */
public interface IAnimated {
	/**
	 * Get (or create) the {@code AnimationData} unique to this object.
	 */
	AnimationData getAnimationData();
}
