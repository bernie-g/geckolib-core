/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import java.util.Objects;

import com.eliotlash.molang.math.IValue;

import software.bernie.geckolib3.core.easing.EasingFunction;
import software.bernie.geckolib3.core.easing.EasingManager;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.util.MathUtil;

public class KeyFrame {
	public final double startTime;
	public final double length;
	private final IValue startValue;
	private final IValue endValue;
	private final EasingFunction easingFunction;

	public KeyFrame(double startTime, double length, IValue startValue, IValue endValue) {
		this(startTime, length, startValue, endValue, EasingType.Linear, null);
	}

	public KeyFrame(double startTime, double length, IValue startValue, IValue endValue, EasingType easingType) {
		this(startTime, length, startValue, endValue, easingType, null);
	}

	public KeyFrame(double startTime, double length, IValue startValue, IValue endValue, EasingType easingType, Double easingArg) {
		this.startTime = startTime;
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
		this.easingFunction = EasingManager.getEasingFunc(easingType, easingArg);
	}

	public double getLength() {
		return length;
	}

	public double getStartValue() {
		return startValue.get();
	}

	public double getEndValue() {
		return endValue.get();
	}

	@Override
	public int hashCode() {
		return Objects.hash(length, startValue, endValue);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof KeyFrame && hashCode() == obj.hashCode();
	}

	/**
	 * Gets this keyframe's value at the given time.
	 *
	 * <p>Given {@code t == 0}, this method will return {@code this.getStartValue()}.
	 * <p>Given {@code t == this.getLength()}, this method will return {@code this.getEndValue()}.
	 *
	 * @param t The time relative to this keyframe's start.
	 * @param override The override value to use instead of the keyframe's easing function.
	 * @return The interpolated value.
	 */
	public double getValueAt(double t, EasingFunction override) {
		if (t >= length) {
			return getEndValue();
		} else if (t <= 0) {
			return getStartValue();
		} else {
			EasingFunction f = override == null ? easingFunction : override;
			return MathUtil.lerp(f.apply(t / length), getStartValue(), getEndValue());
		}
	}

	public boolean isCompletedAfter(double animationTime) {
		return animationTime < startTime + length;
	}
}
