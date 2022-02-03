package software.bernie.geckolib3.core.event.predicate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AnimationEvent<T> {
	private final T animatable;
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float partialTick;
	private final boolean isMoving;
	private final List<Object> extraData;

	public AnimationEvent(T animatable) {
		this(animatable, 0, 0, 0, false, Collections.emptyList());
	}

	public AnimationEvent(T animatable, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving,
			List<Object> extraData) {
		this.animatable = animatable;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.partialTick = partialTick;
		this.isMoving = isMoving;
		this.extraData = extraData;
	}

	public T getAnimatable() {
		return animatable;
	}

	public float getLimbSwing() {
		return limbSwing;
	}

	public float getLimbSwingAmount() {
		return limbSwingAmount;
	}

	public float getPartialTick() {
		return partialTick;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public List<Object> getExtraData() {
		return extraData;
	}

	public <Data> List<Data> getExtraDataOfType(Class<Data> type) {
		return extraData.stream().filter(x -> type.isAssignableFrom(x.getClass())).map(type::cast)
				.collect(Collectors.toList());
	}
}
