package software.bernie.geckolib3.core.easing;

@FunctionalInterface
public interface EaseFunc {
	/**
	 * @param t Value between 0 and 1.
	 * @return Value between 0 and 1.
	 */
	double apply(double t);
}
