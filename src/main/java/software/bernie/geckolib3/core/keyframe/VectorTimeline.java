/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import java.util.Collections;
import java.util.List;

/**
 * A vector key frame list is a handy class used to store 3 lists of keyframes: the X, Y, and Z keyframes. The keyframes can be rotation, scale, or position.
 */
public class VectorTimeline {
	/**
	 * The X key frames.
	 */
	public final Timeline xKeyFrames;
	/**
	 * The Y key frames.
	 */
	public final Timeline yKeyFrames;
	/**
	 * The Z key frames.
	 */
	public final Timeline zKeyFrames;

	/**
	 * Instantiates a new vector key frame list from 3 lists of keyframes
	 *
	 * @param XKeyFrames the x key frames
	 * @param YKeyFrames the y key frames
	 * @param ZKeyFrames the z key frames
	 */
	public VectorTimeline(List<KeyFrame> XKeyFrames, List<KeyFrame> YKeyFrames,
			List<KeyFrame> ZKeyFrames) {
		xKeyFrames = new Timeline(XKeyFrames);
		yKeyFrames = new Timeline(YKeyFrames);
		zKeyFrames = new Timeline(ZKeyFrames);
	}

	/**
	 * Instantiates a new blank key frame list
	 */
	public VectorTimeline() {
		xKeyFrames = Timeline.EMPTY;
		yKeyFrames = Timeline.EMPTY;
		zKeyFrames = Timeline.EMPTY;
	}

	public double getLastKeyframeTime() {
		double xTime = xKeyFrames.getTotalTime();

		double yTime = yKeyFrames.getTotalTime();

		double zTime = zKeyFrames.getTotalTime();

		return Math.max(xTime, Math.max(yTime, zTime));
	}
}
