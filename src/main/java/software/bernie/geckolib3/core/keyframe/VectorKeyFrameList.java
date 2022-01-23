/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import java.util.ArrayList;
import java.util.List;

/**
 * A vector key frame list is a handy class used to store 3 lists of keyframes: the X, Y, and Z keyframes. The keyframes can be rotation, scale, or position.
 *
 * @param <T> the type parameter
 */
public class VectorKeyFrameList<T>
{
	/**
	 * The X key frames.
	 */
	public List<KeyFrame<T>> xKeyFrames;
	/**
	 * The Y key frames.
	 */
	public List<KeyFrame<T>> yKeyFrames;
	/**
	 * The Z key frames.
	 */
	public List<KeyFrame<T>> zKeyFrames;

	/**
	 * Instantiates a new vector key frame list from 3 lists of keyframes
	 *
	 * @param XKeyFrames the x key frames
	 * @param YKeyFrames the y key frames
	 * @param ZKeyFrames the z key frames
	 */
	public VectorKeyFrameList(List<KeyFrame<T>> XKeyFrames, List<KeyFrame<T>> YKeyFrames, List<KeyFrame<T>> ZKeyFrames)
	{
		xKeyFrames = XKeyFrames;
		yKeyFrames = YKeyFrames;
		zKeyFrames = ZKeyFrames;
	}

	/**
	 * Instantiates a new blank key frame list
	 */
	public VectorKeyFrameList()
	{
		xKeyFrames = new ArrayList<>();
		yKeyFrames = new ArrayList<>();
		zKeyFrames = new ArrayList<>();
	}

	public double getLastKeyframeTime()
	{
		double xTime = 0;
		for (KeyFrame<T> frame : xKeyFrames)
		{
			xTime += frame.getLength();
		}

		double yTime = 0;
		for (KeyFrame<T> frame : yKeyFrames)
		{
			yTime += frame.getLength();
		}

		double zTime = 0;
		for (KeyFrame<T> frame : zKeyFrames)
		{
			zTime += frame.getLength();
		}

		return Math.max(xTime, Math.max(yTime, zTime));
	}
}
