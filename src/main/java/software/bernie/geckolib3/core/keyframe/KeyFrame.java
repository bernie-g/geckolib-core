/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import software.bernie.geckolib3.core.easing.EasingType;

public class KeyFrame<T> {
	private static final double[] EMPTY_ARGS = new double[0];

	private final double length;
	private final T startValue;
	private final T endValue;
	public final EasingType easingType;
	public final double[] easingArgs;

	public KeyFrame(double length, T startValue, T endValue) {
		this(length, startValue, endValue, EasingType.Linear, EMPTY_ARGS);
	}

	public KeyFrame(double length, T startValue, T endValue, EasingType easingType) {
		this(length, startValue, endValue, easingType, EMPTY_ARGS);
	}

	public KeyFrame(double length, T startValue, T endValue, EasingType easingType, double[] easingArgs) {
		this.length = length;
		this.startValue = startValue;
		this.endValue = endValue;
		this.easingType = easingType;
		this.easingArgs = easingArgs;
	}

	public double getLength() {
		return length;
	}

	public T getStartValue() {
		return startValue;
	}

	public T getEndValue() {
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
