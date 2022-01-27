/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.manager;

import java.util.HashMap;
import java.util.Map;

import com.eliotlash.molang.MolangParser;

import software.bernie.geckolib3.core.AnimationPage;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.BoneTree;
import software.bernie.geckolib3.core.processor.IBone;

public class AnimationData {
	private final Map<String, AnimationController> animationControllers = new HashMap<>();
	public boolean isFirstTick = true;
	private double resetTickLength = 1;
	public boolean shouldPlayWhilePaused = false;
	private BoneTree<?> boneTree;

	/**
	 * This method is how you register animation controllers, without this, your AnimationPredicate method will never be called
	 *
	 * @param value The value
	 * @return the animation controller
	 */
	public <T> AnimationController<T> addAnimationController(AnimationController<T> value) {
		this.animationControllers.put(value.getName(), value);
		return value;
	}

	public double getResetSpeed() {
		return resetTickLength;
	}

	/**
	 * This is how long it takes for any bones that don't have an animation to revert back to their original position
	 *
	 * @param resetTickLength The amount of ticks it takes to reset. Cannot be negative.
	 */
	public void setResetSpeedInTicks(double resetTickLength) {
		this.resetTickLength = resetTickLength < 0 ? 0 : resetTickLength;
	}

	public void setBoneTree(BoneTree<?> boneTree) {
		this.boneTree = boneTree;
	}

	public AnimationController getAnimationController(String name) {
		return animationControllers.get(name);
	}

	public BoneTree<?> getBoneTree() {
		return boneTree;
	}

	public IBone getBone(String name) {
		return boneTree.getBoneByName(name);
	}

	public <T> void tickAnimation(AnimationEvent<T> event, AnimationPage<T> animationPage, MolangParser parser,
			double renderTime) {
		for (IBone bone : boneTree.getAllBones()) {
			bone.getDirtyTracker().beginFrame();
		}

		for (AnimationController<T> controller : animationControllers.values()) {

			controller.isJustStarting = isFirstTick;
			controller.animationPage = animationPage;

			// Process animations and add new values to the point queues
			controller.process(boneTree, renderTime, event, parser);
		}

		//        for (IBone bone : boneTree.getAllBones()) {
		//            bone.getDirtyTracker().endFrame(renderTime);
		//        }
		isFirstTick = false;
	}

}
