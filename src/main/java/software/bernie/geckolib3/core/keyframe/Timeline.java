package software.bernie.geckolib3.core.keyframe;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import software.bernie.geckolib3.core.easing.EasingFunction;

/**
 * A class that represents a timeline of keyframes.
 */
public class Timeline implements TimelineValue, Iterable<KeyFrame> {

	public static final Timeline EMPTY = new Timeline(Collections.emptyList());

	private final List<KeyFrame> keyFrames;
	private final double totalTime;
	private final boolean hasKeyFrames;

	public Timeline(List<KeyFrame> keyFrames) {
		this.keyFrames = keyFrames;
		this.totalTime = calculateTotalTime(keyFrames);
		this.hasKeyFrames = !keyFrames.isEmpty();
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
		return hasKeyFrames;
	}

	/**
	 * Calculates the interpolated value of this timeline at the given time.
	 * @param t The time to interpolate at.
	 * @param override An optional override easing function to use in place of the keyframes' easing functions.
	 * @return The interpolated value.
	 */
	public double get(double t, EasingFunction override) {

		if (t < totalTime) {
			for (KeyFrame frame : keyFrames) {
				if (frame.startTime + frame.length > t) {
					double keyFrameLocalTime = t - frame.startTime;
					return frame.getValueAt(keyFrameLocalTime, override);
				}
			}
		}

		return getLast().getEndValue();
	}

	public TimelineValue seek(double t) {
		if (t < totalTime) {
			for (int i = 0, keyFramesSize = keyFrames.size(); i < keyFramesSize; i++) {
				KeyFrame frame = keyFrames.get(i);

				if (frame.startTime + frame.length > t) {
					return new Scrubber(frame, i);
				}
			}
		}

		return new Scrubber(getLast(), keyFrames.size() - 1);
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

	public static class Constant implements TimelineValue {
		private final double value;

		public Constant(double value) {
			this.value = value;
		}

		@Override
		public double get(double animationTime, EasingFunction easeOverride) {
			return value;
		}
	}

	public class Scrubber implements TimelineValue {

		private KeyFrame currentKeyFrame;
		private int currentIndex;

		public Scrubber(KeyFrame frame, int keyFrameIndex) {
			this.currentKeyFrame = frame;
			this.currentIndex = keyFrameIndex;
		}

		@Override
		public double get(double animationTime, EasingFunction easeOverride) {
			advanceToTime(animationTime);

			double keyFrameLocalTime = animationTime - currentKeyFrame.startTime;
			return currentKeyFrame.getValueAt(keyFrameLocalTime, easeOverride);
		}

		private void advanceToTime(double animationTime) {
			while (hasNextKeyFrame() && !currentKeyFrame.isCompletedAfter(animationTime)) {
				currentIndex++;
				currentKeyFrame = keyFrames.get(currentIndex);
			}
		}

		private boolean hasNextKeyFrame() {
			return currentIndex < keyFrames.size() - 1;
		}
	}
}
