package software.bernie.geckolib3.core.easing;

/**
 * A function that maps a linear {@code t} value in the range [0, 1] to an eased value in the range [0, 1].
 */
@FunctionalInterface
public interface EasingFunction {
	/**
	 * @param t Linear input value in the range [0, 1].
	 * @return Eased output value in the range [0, 1].
	 */
	double apply(double t);
}
