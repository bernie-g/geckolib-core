/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.event;

import java.util.List;

import software.bernie.geckolib3.core.engine.AnimationChannel;

public class CustomInstructionKeyframeEvent<T> extends KeyframeEvent<T> {
	public final List<String> instructions;

	/**
	 * This stores all the fields that are needed in the AnimationTestEvent
	 *
	 * @param animationTick The amount of ticks that have passed in either the current transition or animation, depending on the controller's AnimationState.
	 * @param instructions  A list of all the custom instructions. In blockbench, each line in the custom instruction box is a separate instruction.
	 * @param controller    the controller
	 */
	public CustomInstructionKeyframeEvent(double animationTick, List<String> instructions,
			AnimationChannel<T> controller) {
		super(animationTick, controller);
		this.instructions = instructions;
	}
}
