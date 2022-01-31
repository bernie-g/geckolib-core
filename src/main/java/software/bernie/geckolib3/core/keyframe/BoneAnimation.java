/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;


public class BoneAnimation {
	public final String boneName;
	public final VectorTimeline rotationKeyFrames;
	public final VectorTimeline positionKeyFrames;
	public final VectorTimeline scaleKeyFrames;

	public BoneAnimation(String boneName, VectorTimeline rotationKeyFrames,
			VectorTimeline positionKeyFrames, VectorTimeline scaleKeyFrames) {
		this.boneName = boneName;
		this.rotationKeyFrames = rotationKeyFrames;
		this.positionKeyFrames = positionKeyFrames;
		this.scaleKeyFrames = scaleKeyFrames;
	}
}
