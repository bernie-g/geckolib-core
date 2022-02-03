/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.manager;

import java.util.HashMap;
import java.util.Map;

import com.eliotlash.molang.MolangParser;

import software.bernie.geckolib3.core.ModelType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.BoneTree;
import software.bernie.geckolib3.core.processor.IBone;

public class Animator<T> {
	private final Map<String, AnimationController<T>> animationControllers = new HashMap<>();
	private double resetTickLength = 1;
	public final BoneTree<?> boneTree;
	public final ModelType<T> modelType;

	public Animator(T object, ModelType<T> modelType) {
		this.modelType = modelType;
		boneTree = modelType.getOrCreateBoneTree(object);
	}

	/**
	 * This method is how you register animation controllers, without this, your AnimationPredicate method will never be called
	 *
	 * @param value The value
	 * @return the animation controller
	 */
	public AnimationController<T> addAnimationController(AnimationController<T> value) {
		value.modelType = modelType;
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
		this.resetTickLength = Math.max(0, resetTickLength);
	}

	public AnimationController<T> getAnimationController(String name) {
		return animationControllers.get(name);
	}

	public IBone getBone(String name) {
		return boneTree.getBoneByName(name);
	}

	public void tickAnimation(AnimationEvent<T> event, MolangParser parser, double renderTime) {

		modelType.setMolangQueries(event.getAnimatable(), parser, renderTime);

		beginFrame();

		for (AnimationController<T> controller : animationControllers.values()) {

			// Process animations and add new values to the point queues
			controller.process(boneTree, renderTime, event, parser);
		}

		endFrame(renderTime);
	}

	private void beginFrame() {
		for (IBone bone : boneTree.getAllBones()) {
			bone.getDirtyTracker().beginFrame();
		}
	}

	private void endFrame(double renderTime) {
		for (IBone bone : boneTree.getAllBones()) {
			bone.getDirtyTracker().endFrame(renderTime);
		}
	}

}
