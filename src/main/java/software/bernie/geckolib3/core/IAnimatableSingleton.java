package software.bernie.geckolib3.core;

import software.bernie.geckolib3.core.manager.AnimationData;

public interface IAnimatableSingleton<K> extends IAnimate {
    AnimationData getAnimationData(K key);
}
