package software.bernie.geckolib3.core.keyframe;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import software.bernie.geckolib3.core.easing.EaseFunc;

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

	/**
	 * @return The total time this timeline spans.
	 */
	public double getTotalTime() {
		return totalTime;
	}

	/**
	 * @return The last keyframe in this timeline.
	 */
	public KeyFrame getLast() {
		return keyFrames.get(keyFrames.size() - 1);
	}

	/**
	 * @return {@code true} if this timeline has keyframes, {@code false} otherwise.
	 */
	public boolean hasKeyFrames() {
		return !keyFrames.isEmpty();
	}

	/**
	 * Calculates the interpolated value of this timeline at the given time.
	 * @param t The time to interpolate at.
	 * @param override An optional override easing function to use in place of the keyframes' easing functions.
	 * @return The interpolated value.
	 */
	public double getValueAt(double t, EaseFunc override) {

		if (t < totalTime) {
			double keyFrameStartTime = 0;
			for (KeyFrame frame : keyFrames) {
				double keyFrameLength = frame.getLength();

				if (keyFrameStartTime + keyFrameLength > t) {
					double keyFrameLocalTime = t - keyFrameStartTime;
					return frame.getValueAt(keyFrameLocalTime, override);
				}
				keyFrameStartTime += keyFrameLength;
			}
		}

		return getLast().getEndValue();
	}

	@Override
	public Iterator<KeyFrame> iterator() {
		return keyFrames.iterator();
	}

	private static double calculateTotalTime(List<KeyFrame> keyFrames) {
		double totalTime = 0;
		for (KeyFrame keyFrame : keyFrames) {
			totalTime += keyFrame.getLength();
		}
		return totalTime;
	}
}
