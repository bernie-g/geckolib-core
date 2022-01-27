package software.bernie.geckolib3.core.manager;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class AnimationFactory<T> {
	private final BiConsumer<T, AnimationData> initializer;
	private final Map<T, AnimationData> animationDataMap = new WeakHashMap<>();

	public AnimationFactory(BiConsumer<T, AnimationData> initializer) {
		this.initializer = initializer;
	}

	/**
	 * This creates or gets the cached animation manager for any unique ID.
	 * For itemstacks, this is typically a hashcode of their nbt. For entities it should be their unique uuid.
	 * For tile entities you can use nbt or just one constant value since they are not singletons.
	 *
	 * @param key A unique integer ID. For every ID the same animation manager will be returned.
	 * @return the animatable manager
	 */
	public AnimationData getOrCreateAnimationData(T key) {
		return animationDataMap.computeIfAbsent(key, this::createAnimationData);
	}

	private AnimationData createAnimationData(T key) {
		AnimationData data = new AnimationData();
		initializer.accept(key, data);
		return data;
	}
}
