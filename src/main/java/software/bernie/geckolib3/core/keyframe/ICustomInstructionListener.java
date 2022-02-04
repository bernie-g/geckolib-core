package software.bernie.geckolib3.core.keyframe;

import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;

/**
 * Custom instructions can be added in blockbench by enabling animation effects in Animation - Animate Effects. You can then add custom instruction keyframes and use them as timecodes/events to handle in code.
 */
@FunctionalInterface
public interface ICustomInstructionListener<A> {
	/**
	 * Custom instructions can be added in blockbench by enabling animation effects in Animation - Animate Effects. You can then add custom instruction keyframes and use them as timecodes/events to handle in code.
	 */
	void executeInstruction(CustomInstructionKeyframeEvent<A> event);
}
