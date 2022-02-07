/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.engine;

import java.util.Collections;
import java.util.List;

import com.eliotlash.molang.MolangParser;

import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.AnimationQueue;
import software.bernie.geckolib3.core.builder.RawAnimation;
import software.bernie.geckolib3.core.easing.EaseFunc;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.ICustomInstructionListener;
import software.bernie.geckolib3.core.keyframe.IParticleListener;
import software.bernie.geckolib3.core.keyframe.ISoundListener;

/**
 * Runs exactly one animation at a time.
 *
 * @param <T> The type of object that this channel is processing animations for.
 */
public class AnimationChannel<T> {

	public static class Builder<T> {

		final Animator<T> parent;

		double transitionLengthTicks = 0;

		IAnimationPredicate<T> predicate;

		ISoundListener<T> soundListener;
		IParticleListener<T> particleListener;
		ICustomInstructionListener<T> customInstructionListener;

		EaseFunc easeOverride;

		public Builder(Animator<T> parent) {
			this.parent = parent;
		}

		/**
		 * Set an override ease function for any animation processed by this controller.
		 *
		 * <p>
		 *     If unset, the ease functions specified by each keyframe will be used.
		 * </p>
		 * @param easeOverride An override for the ease functions.
		 * @return {@code this}
		 */
		public Builder<T> setEaseOverride(EaseFunc easeOverride) {
			this.easeOverride = easeOverride;
			return this;
		}

		public Builder<T> setTransitionLengthTicks(double transitionLengthTicks) {
			this.transitionLengthTicks = transitionLengthTicks;
			return this;
		}

		public Builder<T> setPredicate(IAnimationPredicate<T> predicate) {
			this.predicate = predicate;
			return this;
		}

		/**
		 * Set a callback to run when a sound keyframe is reached.
		 * @param soundListener The callback to run.
		 * @return {@code this}
		 */
		public Builder<T> setSoundListener(ISoundListener<T> soundListener) {
			this.soundListener = soundListener;
			return this;
		}

		/**
		 * Set a callback to run when a particle keyframe is reached.
		 * @param particleListener The callback to run.
		 * @return {@code this}
		 */
		public Builder<T> setParticleListener(IParticleListener<T> particleListener) {
			this.particleListener = particleListener;
			return this;
		}

		/**
		 * Set a callback to run when a custom instruction is reached.
		 * @param customInstructionListener The callback to run.
		 * @return {@code this}
		 */
		public Builder<T> setCustomInstructionListener(ICustomInstructionListener<T> customInstructionListener) {
			this.customInstructionListener = customInstructionListener;
			return this;
		}

		public Animator<T> build() {
			parent.addChannel(new AnimationChannel<>(parent, this));
			return parent;
		}
	}

	public final Animator<T> parent;

	/**
	 * The animation predicate, is tested in every process call (i.e. every frame)
	 */
	protected final IAnimationPredicate<T> animationPredicate;

	/**
	 * How long it takes to transition between animations
	 */
	public double transitionLengthTicks;

	/**
	 * The sound listener is called every time a sound keyframe is encountered (i.e. every frame)
	 */
	public final ISoundListener<T> soundListener;

	/**
	 * The particle listener is called every time a particle keyframe is encountered (i.e. every frame)
	 */
	public final IParticleListener<T> particleListener;

	/**
	 * The custom instruction listener is called every time a custom instruction keyframe is encountered (i.e. every frame)
	 */
	public final ICustomInstructionListener<T> customInstructionListener;

	/**
	 * By default Geckolib uses the easing types of every keyframe. If you want to override that for an entire AnimationController, change this value.
	 */
	public final EaseFunc easeOverride;

	protected AnimationQueue animationQueue = new AnimationQueue();
	protected List<RawAnimation> rawAnimations = Collections.emptyList();
	protected RunningAnimation currentAnimation;

	/**
	 * Instantiates a new Animation controller.
	 *
	 * <p>
	 * Each animation controller can run one animation at a time. You can have several animation controllers for
	 * each entity, i.e. one animation to control the entity's size, one to control movement, attacks, etc.
	 *
	 */
	public AnimationChannel(Animator<T> parent, Builder<T> builder) {
		this.parent = parent;
		this.animationPredicate = builder.predicate;
		this.transitionLengthTicks = builder.transitionLengthTicks;
		this.easeOverride = builder.easeOverride;
		this.soundListener = builder.soundListener;
		this.particleListener = builder.particleListener;
		this.customInstructionListener = builder.customInstructionListener;
	}

	public T getEntity() {
		return parent.object;
	}

	/**
	 * This method sets the current animation with an animation builder. You can run this method every frame, if you pass in the same animation builder every time, it won't restart. Additionally, it smoothly transitions between animation states.
	 */
	public void setAnimation(AnimationBuilder builder) {
		if (builder == null) {
			return;
		}

		List<RawAnimation> animationList = builder.getRawAnimationList();

		if (animationList.size() == 0) {
			return;
		}

		// Convert the list of animation names to the actual list, keeping track of the loop boolean along the way
		AnimationQueue animations = new AnimationQueue();
		boolean errored = false;

		for (RawAnimation rawAnimation : animationList) {
			Animation animation = parent.modelType.getAnimation(getEntity(), rawAnimation.animationName);
			if (animation == null) {
				System.out.printf("Could not load animation: %s. Is it missing?", rawAnimation.animationName);
				errored = true;
				continue;
			}

			boolean loop = animation.loop;
			if (rawAnimation.loop != null) {
				loop = rawAnimation.loop;
			}

			animations.add(animation, loop);
		}
		if (errored) {
			return;
		} else {
			animationQueue = animations;
		}
		rawAnimations = animationList;
	}

	/**
	 * Gets the current animation. Can be null
	 *
	 * @return the current animation
	 */
	public RunningAnimation getCurrentAnimation() {
		return currentAnimation;
	}


	/**
	 * This method is called every frame in order to populate the animation point queues, and process animation state logic.
	 *
	 * @param renderTime The current tick + partial tick
	 * @param event      The animation test event
	 */
	public void process(double renderTime, AnimationEvent<T> event, MolangParser parser) {
		setAnimTime(parser, 0);

		checkForNewAnimation(event);

		if (currentAnimation == null) {
			if (queueNextAnimation(renderTime)) return;
		}

		if (currentAnimation != null) {
			currentAnimation.process(renderTime, this);

			if (currentAnimation.isFinished(renderTime)) {
				queueNextAnimation(renderTime);
			}
		}
	}

	/**
	 * @param renderTime The current tick + partial tick.
	 * @return True if no animation was queued
	 */
	private boolean queueNextAnimation(double renderTime) {
		if (animationQueue.isEmpty()) {
			return true;
		}

		AnimationQueue.QueuedAnimation next = animationQueue.peek();

		currentAnimation = new RunningAnimation(next.animation, parent.boneTree, renderTime);

		if (!next.loop) {
			animationQueue.poll();
		}
		return false;
	}

	private void checkForNewAnimation(AnimationEvent<T> event) {
		AnimationBuilder animation = this.animationPredicate.test(this, event);

		setAnimation(animation);
	}

	private void setAnimTime(MolangParser parser, double tick) {
		parser.setValue("query.anim_time", tick / 20);
		parser.setValue("query.life_time", tick / 20);
	}
}
