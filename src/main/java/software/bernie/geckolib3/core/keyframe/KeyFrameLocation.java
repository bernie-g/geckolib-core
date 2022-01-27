/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

/**
 * This class stores a location in an animation, and returns the keyframe that should be executed.
 *
 */
public class KeyFrameLocation<T>
{
	/**
	 * The curent frame.
	 */
	public KeyFrame<T> currentFrame;

	/**
	 * This is the combined total time of all the previous keyframes
	 */
	public double currentTick;

	/**
	 * Instantiates a new Key frame location.
	 *
	 * @param currentFrame         the current frame
	 * @param currentTick the current animation tick
	 */
	public KeyFrameLocation(KeyFrame<T> currentFrame, double currentTick)
	{
		this.currentFrame = currentFrame;
		this.currentTick = currentTick;
	}
}
