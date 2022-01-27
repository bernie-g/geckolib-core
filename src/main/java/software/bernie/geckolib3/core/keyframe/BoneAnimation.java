/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;


import com.eliotlash.mclib.math.IValue;

public class BoneAnimation
{
	public final String boneName;
	public final VectorKeyFrameList<IValue> rotationKeyFrames;
	public final VectorKeyFrameList<IValue> positionKeyFrames;
	public final VectorKeyFrameList<IValue> scaleKeyFrames;

	public BoneAnimation(String boneName, VectorKeyFrameList<IValue> rotationKeyFrames, VectorKeyFrameList<IValue> positionKeyFrames, VectorKeyFrameList<IValue> scaleKeyFrames) {
		this.boneName = boneName;
		this.rotationKeyFrames = rotationKeyFrames;
		this.positionKeyFrames = positionKeyFrames;
		this.scaleKeyFrames = scaleKeyFrames;
	}
}
