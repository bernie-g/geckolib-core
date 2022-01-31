/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.controller;

import java.util.Collections;
import java.util.List;

import com.eliotlash.molang.MolangParser;

import software.bernie.geckolib3.core.AnimationPage;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.AnimationQueue;
import software.bernie.geckolib3.core.builder.RawAnimation;
import software.bernie.geckolib3.core.easing.EaseFunc;
import software.bernie.geckolib3.core.easing.EasingManager;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.BoneTree;

/**
 * The type Animation controller.
 *
 * @param <T> the type parameter
 */
public class AnimationController<T> {
	public AnimationPage<T> animationPage;

	/**
	 * The Entity.
	 */
	protected final T animatable;

	/**
	 * The animation predicate, is tested in every process call (i.e. every frame)
	 */
	protected final IAnimationPredicate<T> animationPredicate;

	/**
	 * The name of the animation controller
	 */
	private final String name;

	/**
	 * How long it takes to transition between animations
	 */
	public double transitionLengthTicks;

	/**
	 * The sound listener is called every time a sound keyframe is encountered (i.e. every frame)
	 */
	public ISoundListener<T> soundListener;

	/**
	 * The particle listener is called every time a particle keyframe is encountered (i.e. every frame)
	 */
	public IParticleListener<T> particleListener;

	/**
	 * The custom instruction listener is called every time a custom instruction keyframe is encountered (i.e. every frame)
	 */
	public ICustomInstructionListener<T> customInstructionListener;

	/**
	 * An AnimationPredicate is run every render frame for ever AnimationController. The "test" method is where you should change animations, stop animations, restart, etc.
	 */
	@FunctionalInterface
	public interface IAnimationPredicate<P> {
		/**
		 * An AnimationPredicate is run every render frame for every AnimationController. The "test" method is where you should change animations, stop animations, restart, etc.
		 *
		 * @return CONTINUE if the animation should continue, STOP if it should stop.
		 */
		AnimationBuilder test(AnimationController<P> controller, AnimationEvent<P> event);
	}

	/**
	 * Sound Listeners are run when a sound keyframe is hit. You can either return the SoundEvent and geckolib will play the sound for you, or return null and handle the sounds yourself.
	 */
	@FunctionalInterface
	public interface ISoundListener<A> {
		/**
		 * Sound Listeners are run when a sound keyframe is hit. You can either return the SoundEvent and geckolib will play the sound for you, or return null and handle the sounds yourself.
		 */
		void playSound(SoundKeyframeEvent<A> event);
	}

	/**
	 * Particle Listeners are run when a sound keyframe is hit. You need to handle the actual playing of the particle yourself.
	 */
	@FunctionalInterface
	public interface IParticleListener<A> {
		/**
		 * Particle Listeners are run when a sound keyframe is hit. You need to handle the actual playing of the particle yourself.
		 */
		void summonParticle(ParticleKeyFrameEvent<A> event);
	}

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


	protected AnimationQueue animationQueue = new AnimationQueue();
	protected List<RawAnimation> rawAnimations = Collections.emptyList();
	protected RunningAnimation currentAnimation;

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
			Animation animation = animationPage.getAnimation(animatable, rawAnimation.animationName);
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
	 * By default Geckolib uses the easing types of every keyframe. If you want to override that for an entire AnimationController, change this value.
	 */
	public final EaseFunc easeOverride;

	/**
	 * Instantiates a new Animation controller. Each animation controller can run one animation at a time. You can have several animation controllers for each entity, i.e. one animation to control the entity's size, one to control movement, attacks, etc.
	 *
	 * @param animatable            The entity
	 * @param name                  Name of the animation controller (move_controller, size_controller, attack_controller, etc.)
	 * @param transitionLengthTicks How long it takes to transition between animations (IN TICKS!!)
	 */
	public AnimationController(T animatable, String name, float transitionLengthTicks,
			IAnimationPredicate<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLengthTicks = transitionLengthTicks;
		this.animationPredicate = animationPredicate;
		this.easeOverride = null;
	}


	/**
	 * Instantiates a new Animation controller. Each animation controller can run one animation at a time. You can have several animation controllers for each entity, i.e. one animation to control the entity's size, one to control movement, attacks, etc.
	 *
	 * @param animatable            The entity
	 * @param name                  Name of the animation controller (move_controller, size_controller, attack_controller, etc.)
	 * @param transitionLengthTicks How long it takes to transition between animations (IN TICKS!!)
	 * @param easingType            The method of easing to use. The other constructor defaults to no easing.
	 */
	public AnimationController(T animatable, String name, float transitionLengthTicks, EasingType easingType,
			IAnimationPredicate<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLengthTicks = transitionLengthTicks;
		this.animationPredicate = animationPredicate;
		this.easeOverride = EasingManager.getEasingFunc(easingType, null);
	}

	/**
	 * Instantiates a new Animation controller. Each animation controller can run one animation at a time. You can have several animation controllers for each entity, i.e. one animation to control the entity's size, one to control movement, attacks, etc.
	 *
	 * @param animatable            The entity
	 * @param name                  Name of the animation controller (move_controller, size_controller, attack_controller, etc.)
	 * @param transitionLengthTicks How long it takes to transition between animations (IN TICKS!!)
	 * @param easeOverride    If you want to use an easing method that's not included in the EasingType enum, pass your method into here. The parameter that's passed in will be a number between 0 and 1. Return a number also within 0 and 1. Take a look at {@link software.bernie.geckolib3.core.easing.EasingManager}
	 */
	public AnimationController(T animatable, String name, float transitionLengthTicks,
			EaseFunc easeOverride, IAnimationPredicate<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLengthTicks = transitionLengthTicks;
		this.easeOverride = easeOverride;
		this.animationPredicate = animationPredicate;
	}

	/**
	 * Gets the controller's name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
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
	 * Returns the current state of this animation controller.
	 */
	public AnimationState getAnimationState() {
		return AnimationState.Running;
	}

	/**
	 * Registers a sound listener.
	 */
	public void registerSoundListener(ISoundListener<T> soundListener) {
		this.soundListener = soundListener;
	}

	/**
	 * Registers a particle listener.
	 */
	public void registerParticleListener(IParticleListener<T> particleListener) {
		this.particleListener = particleListener;
	}

	/**
	 * Registers a custom instruction listener.
	 */
	public void registerCustomInstructionListener(ICustomInstructionListener<T> customInstructionListener) {
		this.customInstructionListener = customInstructionListener;
	}


	/**
	 * This method is called every frame in order to populate the animation point queues, and process animation state logic.
	 *
	 * @param boneTree
	 * @param renderTime The current tick + partial tick
	 * @param event      The animation test event
	 */
	public void process(BoneTree<?> boneTree, double renderTime, AnimationEvent<T> event, MolangParser parser) {
		setAnimTime(parser, 0);

		checkForNewAnimation(event);

		if (currentAnimation == null) {
			if (queueNextAnimation(boneTree, renderTime)) return;
		}

		if (currentAnimation != null) {
			currentAnimation.process(renderTime, this);

			if (currentAnimation.isFinished(renderTime)) {
				queueNextAnimation(boneTree, renderTime);
			}
		}
	}

	/**
	 * @param boneTree The bone tree the animation will be working with.
	 * @param renderTime The current tick + partial tick.
	 * @return True if no animation was queued
	 */
	private boolean queueNextAnimation(BoneTree<?> boneTree, double renderTime) {
		if (animationQueue.isEmpty()) {
			return true;
		} else {
			AnimationQueue.QueuedAnimation next = animationQueue.peek();

			currentAnimation = new RunningAnimation(next.animation, boneTree, renderTime);

			if (!next.loop) {
				animationQueue.poll();
			}
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
