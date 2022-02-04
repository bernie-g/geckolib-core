/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.engine;

import java.util.ArrayList;
import java.util.List;

import com.eliotlash.molang.MolangParser;

import software.bernie.geckolib3.core.ModelType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.bone.BoneTree;
import software.bernie.geckolib3.core.bone.IBone;

public class Animator<T> {
	private final List<AnimationChannel<T>> channels = new ArrayList<>();
	private double resetTickLength = 1;
	public final BoneTree<?> boneTree;
	public final ModelType<T> modelType;
	public final T object;

	public Animator(T object, ModelType<T> modelType) {
		this.modelType = modelType;
		this.boneTree = modelType.getOrCreateBoneTree(object);
		this.object = object;
	}

	/**
	 * Helper method to build a new animation channel for this animator.
	 *
	 * <p>
	 *     Once you have finished configuring the channel,
	 *     you must call {@link AnimationChannel.Builder#build()} to add it to the animator.
	 * </p>
	 *
	 * @return An object to configure and register a new animation channel.
	 */
	public AnimationChannel.Builder<T> createChannel() {
		return new AnimationChannel.Builder<>(this);
	}

	/**
	 * Add a custom animation channel.
	 *
	 * @param value An externally built animation channel.
	 */
	public void addChannel(AnimationChannel<T> value) {
		this.channels.add(value);
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

	public IBone getBone(String name) {
		return boneTree.getBoneByName(name);
	}

	public void tickAnimation(AnimationEvent<T> event, MolangParser parser, double renderTime) {

		modelType.setMolangQueries(event.getAnimatable(), parser, renderTime);

		beginFrame();

		for (AnimationChannel<T> channel : channels) {
			channel.process(renderTime, event, parser);
		}

		//endFrame(renderTime);
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
