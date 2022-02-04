package software.bernie.geckolib3.core.engine;

import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

/**
 * An AnimationPredicate is run every render frame for ever AnimationController. The "test" method is where you should change animations, stop animations, restart, etc.
 */
@FunctionalInterface
public interface IAnimationPredicate<P> {
	/**
	 * An AnimationPredicate is run every render frame for every AnimationController. The "test" method is where you should change animations, stop animations, restart, etc.
	 *
	 * @return CONTINUE if the animation should continue, STOP if it should stop.
	 */
	AnimationBuilder test(AnimationChannel<P> controller, AnimationEvent<P> event);
}
