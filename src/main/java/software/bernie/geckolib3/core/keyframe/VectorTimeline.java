/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

/**
 * A vector key frame list is a handy class used to store 3 lists of keyframes: the X, Y, and Z keyframes. The keyframes can be rotation, scale, or position.
 */
public class VectorTimeline {
	/**
	 * The X key frames.
	 */
	public final Timeline x;
	/**
	 * The Y key frames.
	 */
	public final Timeline y;
	/**
	 * The Z key frames.
	 */
	public final Timeline z;

	/**
	 * Instantiates a new vector key frame list from 3 lists of keyframes
	 *
	 * @param XKeyFrames the x key frames
	 * @param YKeyFrames the y key frames
	 * @param ZKeyFrames the z key frames
	 */
	public VectorTimeline(Timeline XKeyFrames, Timeline YKeyFrames, Timeline ZKeyFrames) {
		x = XKeyFrames;
		y = YKeyFrames;
		z = ZKeyFrames;
	}

	/**
	 * Instantiates a new blank key frame list
	 */
	public VectorTimeline() {
		x = Timeline.EMPTY;
		y = Timeline.EMPTY;
		z = Timeline.EMPTY;
	}

	public double getLastKeyframeTime() {
		double xTime = x.getTotalTime();

		double yTime = y.getTotalTime();

		double zTime = z.getTotalTime();

		return Math.max(xTime, Math.max(yTime, zTime));
	}

	public boolean hasKeyFrames() {
		return x.hasKeyFrames() || y.hasKeyFrames() || z.hasKeyFrames();
	}
}
