package software.bernie.geckolib3.core;

import software.bernie.geckolib3.core.builder.Animation;

public interface AnimationPage<E> {
	Animation getAnimation(E entity, String animationName);
}
