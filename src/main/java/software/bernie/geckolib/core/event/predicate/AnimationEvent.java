package software.bernie.geckolib.core.event.predicate;

import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.controller.BaseAnimationController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnimationEvent<T extends IAnimatable>
{
	private final T animatable;
	public double animationTick;
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float partialTick;
	private final boolean isMoving;
	private final List<Object> extraData;
	protected BaseAnimationController controller;

	public AnimationEvent(T animatable, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving, List<Object> extraData)
	{
		this.animatable = animatable;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.partialTick = partialTick;
		this.isMoving = isMoving;
		this.extraData = extraData;
	}

	/**
	 * Gets the amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 *
	 * @return the animation tick
	 */
	public double getAnimationTick()
	{
		return animationTick;
	}
	public T getAnimatable() {return animatable; }
	public float getLimbSwing()
	{
		return limbSwing;
	}
	public float getLimbSwingAmount()
	{
		return limbSwingAmount;
	}
	public float getPartialTick()
	{
		return partialTick;
	}
	public boolean isMoving() { return isMoving; }
	public BaseAnimationController getController()
	{
		return controller;
	}

	public void setController(BaseAnimationController controller)
	{
		this.controller = controller;
	}

	public List<Object> getExtraData()
	{
		return extraData;
	}

	public <T> List<T> getExtraDataOfType(Class<T> type)
	{
		return extraData.stream().filter(x -> x.getClass() == type).map(x -> type.cast(x)).collect(Collectors.toList());
	}
}
