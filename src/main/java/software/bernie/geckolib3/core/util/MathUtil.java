package software.bernie.geckolib3.core.util;

public class MathUtil {

	/**
	 * This is the actual function that smoothly interpolates (lerp) between keyframes
	 *
	 * @param startValue The animation's start value
	 * @param endValue   The animation's end value
	 * @return The interpolated value
	 */
	public static float lerpValues(double percentCompleted, double startValue, double endValue) {
		// current tick / position should be between 0 and 1 and represent the percentage of the lerping that has completed
		return (float) lerp(percentCompleted, startValue, endValue);
	}

	public static double lerp(double pct, double start, double end) {
		return start + pct * (end - start);
	}
}
