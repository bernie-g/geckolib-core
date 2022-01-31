package software.bernie.geckolib3.core.keyframe;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import software.bernie.geckolib3.core.ConstantValue;
import software.bernie.geckolib3.core.easing.EaseFunc;
import software.bernie.geckolib3.core.easing.EasingManager;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.util.Axis;
import software.bernie.geckolib3.core.util.MathUtil;

/**
 * A class that represents a timeline of keyframes.
 */
public class Timeline implements Iterable<KeyFrame> {

	public static final Timeline EMPTY = new Timeline(Collections.emptyList());

	private final List<KeyFrame> keyFrames;
	private final double totalTime;

	public Timeline(List<KeyFrame> keyFrames) {
		this.keyFrames = keyFrames;

		this.totalTime = calculateTotalTime(keyFrames);
	}

	public double getTotalTime() {
		return totalTime;
	}

	public KeyFrame getLast() {
		return keyFrames.get(keyFrames.size() - 1);
	}

	@Override
	public Iterator<KeyFrame> iterator() {
		return keyFrames.iterator();
	}

	public boolean hasKeyFrames() {
		return !keyFrames.isEmpty();
	}

	/**
	 * Calculates the interpolated value of this timeline at the given time.
	 * @param t The time to interpolate at.
	 * @param isRotation
	 * @param axis
	 * @param easingType
	 * @param customEasingMethod
	 * @return
	 */
	public double getValueAt(double t, boolean isRotation, Axis axis, EasingType easingType, EaseFunc customEasingMethod) {
		double totalTimeTracker = 0;
		for (KeyFrame frame : this) {
			totalTimeTracker += frame.getLength();
			if (totalTimeTracker > t) {
				return interpolate(isRotation, axis, easingType, customEasingMethod, frame, (t - (totalTimeTracker - frame.getLength())));
			}
		}

		return interpolate(isRotation, axis, easingType, customEasingMethod, getLast(), t);
	}

	private static double interpolate(boolean isRotation, Axis axis, EasingType easingType, EaseFunc customEasingMethod,
			KeyFrame keyFrame, double interpolationTime) {
		double startValue = keyFrame.getStartValue().get();
		double endValue = keyFrame.getEndValue().get();

		if (isRotation) {
			if (!(keyFrame.getStartValue() instanceof ConstantValue)) {
				startValue = Math.toRadians(startValue);
				if (axis == Axis.X || axis == Axis.Y) {
					startValue *= -1;
				}
			}
			if (!(keyFrame.getEndValue() instanceof ConstantValue)) {
				endValue = Math.toRadians(endValue);
				if (axis == Axis.X || axis == Axis.Y) {
					endValue *= -1;
				}
			}
		}

		if (interpolationTime >= keyFrame.getLength()) {
			return endValue;
		}
		if (interpolationTime == 0 && keyFrame.getLength() == 0) {
			return endValue;
		}

		if (easingType == EasingType.CUSTOM && customEasingMethod != null) {
			return MathUtil.lerp(customEasingMethod.apply(interpolationTime / keyFrame.getLength()), startValue, endValue);
		} else if (easingType == EasingType.NONE) {
			easingType = keyFrame.easingType;
		}
		double ease = EasingManager.ease(interpolationTime / keyFrame.getLength(), easingType, keyFrame.easingArgs);
		return MathUtil.lerp(ease, startValue, endValue);
	}

	private static double calculateTotalTime(List<KeyFrame> keyFrames) {
		double totalTime = 0;
		for (KeyFrame keyFrame : keyFrames) {
			totalTime += keyFrame.getLength();
		}
		return totalTime;
	}
}
