package software.bernie.geckolib3.core;

import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.AnimationProcessor;

public interface IAnimatableModel<E>
{
	default double getCurrentTick()
	{
		return (System.nanoTime() / 1000000L / 50.0);
	}

	default void setLivingAnimations(E entity, AnimationData data)
	{
		this.setLivingAnimations(entity, data, null);
	}

	void setLivingAnimations(E entity, AnimationData data, AnimationEvent<E> customPredicate);

	AnimationProcessor<E> getAnimationProcessor();

	Animation getAnimation(String name, E animatable);

	void setMolangQueries(E animatable, double currentTick);
}
