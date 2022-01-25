package software.bernie.geckolib3.core;

import software.bernie.geckolib3.core.manager.AnimationData;

public interface IAnimatableSingleton<K> {
    AnimationData getAnimationData(K key);
}
