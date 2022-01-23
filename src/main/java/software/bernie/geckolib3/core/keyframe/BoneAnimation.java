/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;


import com.eliotlash.mclib.math.IValue;

public class BoneAnimation
{
	public String boneName;
	public VectorKeyFrameList<IValue> rotationKeyFrames;
	public VectorKeyFrameList<IValue> positionKeyFrames;
	public VectorKeyFrameList<IValue> scaleKeyFrames;
}
