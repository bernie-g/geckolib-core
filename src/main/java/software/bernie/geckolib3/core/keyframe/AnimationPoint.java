/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import java.util.function.Function;

import com.eliotlash.mclib.math.IValue;

import software.bernie.geckolib3.core.easing.EasingManager;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.util.MathUtil;


public class AnimationPoint
{
	/**
	 * The current tick in the animation to lerp from
	 */
	public final Double currentTick;
	/**
	 * The tick that the current animation should end at
	 */
	public final Double animationEndTick;
	/**
	 * The Animation start value.
	 */
	public final Double animationStartValue;
	/**
	 * The Animation end value.
	 */
	public final Double animationEndValue;

	/**
	 * The current keyframe.
	 */
	public final KeyFrame<IValue> keyframe;

	public AnimationPoint( KeyFrame<IValue> keyframe, Double currentTick, Double animationEndTick, Double animationStartValue, Double animationEndValue)
	{
		this.keyframe = keyframe;
		this.currentTick = currentTick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = animationStartValue;
		this.animationEndValue = animationEndValue;
	}

	public AnimationPoint(KeyFrame<IValue> keyframe, double tick, double animationEndTick, float animationStartValue, double animationEndValue)
	{
		this.keyframe = keyframe;
		this.currentTick = tick;
		this.animationEndTick = animationEndTick;
		this.animationStartValue = (double) animationStartValue;
		this.animationEndValue = animationEndValue;
	}

	/**
	 * Lerps an AnimationPoint
	 *
	 * @return the resulting lerped value
	 */
	public float lerpValues(EasingType easingType, Function<Double, Double> customEasingMethod)
	{
		if (currentTick >= animationEndTick)
		{
			return animationEndValue.floatValue();
		}
		if (currentTick == 0 && animationEndTick == 0)
		{
			return animationEndValue.floatValue();
		}

		if (easingType == EasingType.CUSTOM && customEasingMethod != null)
		{
			return MathUtil.lerpValues(customEasingMethod.apply(currentTick / animationEndTick),
					animationStartValue, animationEndValue);
		}
		else if (easingType == EasingType.NONE && keyframe != null)
		{
			easingType = keyframe.easingType;
		}
		double ease = EasingManager.ease(currentTick / animationEndTick, easingType, keyframe == null ? null : keyframe.easingArgs);
		return MathUtil.lerpValues(ease,
				animationStartValue, animationEndValue);
	}

	@Override
	public String toString()
	{
		return "Tick: " + currentTick + " | End Tick: " + animationEndTick + " | Start Value: " + animationStartValue + " | End Value: " + animationEndValue;
	}
}
