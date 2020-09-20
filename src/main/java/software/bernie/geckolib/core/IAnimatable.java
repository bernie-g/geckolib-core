/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */
package software.bernie.geckolib.core;

import software.bernie.geckolib.core.manager.AnimationManager;

/**
 * This interface must be applied to any object that wants to be animated
 */
public interface IAnimatable
{
	/**
	 * This method MUST return an Animation Manager, otherwise no animations will be played.
	 *
	 * @return the animation controllers
	 */
	AnimationManager getAnimationManager();
}
