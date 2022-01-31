/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import java.util.Objects;

import com.eliotlash.mclib.math.IValue;

import software.bernie.geckolib3.core.easing.EasingType;

public class KeyFrame {
	private static final double[] EMPTY_ARGS = new double[0];

	private final double length;
	private final IValue startValue;
	private final IValue endValue;
	public final EasingType easingType;
	public final double[] easingArgs;

	public KeyFrame(double length, IValue startValue, IValue endValue) {
		this(length, startValue, endValue, EasingType.Linear, EMPTY_ARGS);
	}

	public KeyFrame(double length, IValue startValue, IValue endValue, EasingType easingType) {
		this(length, startValue, endValue, easingType, EMPTY_ARGS);
	}

	public KeyFrame(double length, IValue startValue, IValue endValue, EasingType easingType, double[] easingArgs) {
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
		this.easingType = easingType;
		this.easingArgs = easingArgs;
	}

	public double getLength() {
		return length;
	}

	public IValue getStartValue() {
		return startValue;
	}

	public IValue getEndValue() {
		return endValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(length, startValue, endValue);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof KeyFrame && hashCode() == obj.hashCode();
	}
}
