/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.controller;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.eliotlash.mclib.math.IValue;
import com.eliotlash.molang.MolangParser;

import software.bernie.geckolib3.core.AnimationPage;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.ConstantValue;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.AnimationQueue;
import software.bernie.geckolib3.core.builder.RawAnimation;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.AnimationPoint;
import software.bernie.geckolib3.core.keyframe.KeyFrame;
import software.bernie.geckolib3.core.keyframe.KeyFrameLocation;
import software.bernie.geckolib3.core.processor.BoneTree;
import software.bernie.geckolib3.core.util.Axis;

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

	protected AnimationState animationState = AnimationState.Stopped;

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

	public boolean isJustStarting = false;

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
		PlayState test(AnimationController<P> controller, AnimationEvent<P> event);
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


	private double tickOffset;
	protected AnimationQueue animationQueue = new AnimationQueue();
	protected List<RawAnimation> rawAnimations = Collections.emptyList();
	protected RunningAnimation currentAnimation;
	protected boolean shouldResetTick = false;
	private boolean justStopped = false;
	protected boolean justStartedTransition = false;
	public Function<Double, Double> customEasingMethod;
	protected boolean needsAnimationReload = false;

	/**
	 * This method sets the current animation with an animation builder. You can run this method every frame, if you pass in the same animation builder every time, it won't restart. Additionally, it smoothly transitions between animation states.
	 */
	public void setAnimation(AnimationBuilder builder) {
		if (builder == null) {
			animationState = AnimationState.Stopped;
			return;
		}

		List<RawAnimation> animationList = builder.getRawAnimationList();

		if (animationList.size() == 0) {
			animationState = AnimationState.Stopped;
			return;
		}

		if (!needsAnimationReload) {
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

		// Reset the adjusted tick to 0 on next animation process call
		shouldResetTick = true;
		animationState = AnimationState.Transitioning;
		justStartedTransition = true;
		needsAnimationReload = false;
	}


	/**
	 * By default Geckolib uses the easing types of every keyframe. If you want to override that for an entire AnimationController, change this value.
	 */
	public EasingType easingType = EasingType.NONE;


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
		tickOffset = 0.0d;
	}


	/**
	 * Instantiates a new Animation controller. Each animation controller can run one animation at a time. You can have several animation controllers for each entity, i.e. one animation to control the entity's size, one to control movement, attacks, etc.
	 *
	 * @param animatable            The entity
	 * @param name                  Name of the animation controller (move_controller, size_controller, attack_controller, etc.)
	 * @param transitionLengthTicks How long it takes to transition between animations (IN TICKS!!)
	 * @param easingtype            The method of easing to use. The other constructor defaults to no easing.
	 */
	public AnimationController(T animatable, String name, float transitionLengthTicks, EasingType easingtype,
			IAnimationPredicate<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLengthTicks = transitionLengthTicks;
		this.easingType = easingtype;
		this.animationPredicate = animationPredicate;
		tickOffset = 0.0d;
	}

	/**
	 * Instantiates a new Animation controller. Each animation controller can run one animation at a time. You can have several animation controllers for each entity, i.e. one animation to control the entity's size, one to control movement, attacks, etc.
	 *
	 * @param animatable            The entity
	 * @param name                  Name of the animation controller (move_controller, size_controller, attack_controller, etc.)
	 * @param transitionLengthTicks How long it takes to transition between animations (IN TICKS!!)
	 * @param customEasingMethod    If you want to use an easing method that's not included in the EasingType enum, pass your method into here. The parameter that's passed in will be a number between 0 and 1. Return a number also within 0 and 1. Take a look at {@link software.bernie.geckolib3.core.easing.EasingManager}
	 */
	public AnimationController(T animatable, String name, float transitionLengthTicks,
			Function<Double, Double> customEasingMethod, IAnimationPredicate<T> animationPredicate) {
		this.animatable = animatable;
		this.name = name;
		this.transitionLengthTicks = transitionLengthTicks;
		this.customEasingMethod = customEasingMethod;
		this.easingType = EasingType.CUSTOM;
		this.animationPredicate = animationPredicate;
		tickOffset = 0.0d;
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
		return animationState;
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
		if (currentAnimation != null) {
			Animation animation = animationPage.getAnimation(this.animatable, currentAnimation.animation.animationName);
			if (animation != null) {
				currentAnimation.begin(boneTree, renderTime, transitionLengthTicks);
			}
		}

		double actualTick = renderTime;

		// Transition period has ended, reset the tick and set the animation to running
		if (animationState == AnimationState.Transitioning && renderTime >= transitionLengthTicks) {
			this.shouldResetTick = true;
			animationState = AnimationState.Running;
		}

		renderTime = adjustTick(renderTime);

		assert renderTime >= 0 : "GeckoLib: Render time was less than zero";

		// This tests the animation predicate
		PlayState playState = this.testAnimationPredicate(event);
		if (playState == PlayState.STOP || (currentAnimation == null && animationQueue.size() == 0)) {
			// The animation should transition to the model's initial state
			animationState = AnimationState.Stopped;
			justStopped = true;
			return;
		}
		if (justStartedTransition && (shouldResetTick || justStopped)) {
			justStopped = false;
			renderTime = adjustTick(actualTick);
		} else if (currentAnimation == null && this.animationQueue.size() != 0) {
			this.shouldResetTick = true;
			this.animationState = AnimationState.Transitioning;
			justStartedTransition = true;
			needsAnimationReload = false;
			renderTime = adjustTick(actualTick);
		} else {
			if (animationState != AnimationState.Transitioning) {
				animationState = AnimationState.Running;
			}
		}

		// Handle transitioning to a different animation (or just starting one)
		if (animationState == AnimationState.Transitioning) {
			// Just started transitioning, so set the current animation to the first one
			if (renderTime == 0 || isJustStarting) {
				justStartedTransition = false;
				this.currentAnimation = animationQueue.poll();
				if (currentAnimation != null) {
					currentAnimation.resetEventKeyFrames();
				}
			}
			if (currentAnimation != null) {
				setAnimTime(parser, 0);
				RunningAnimation currentAnimation1 = this.currentAnimation;
				currentAnimation1.runTransition(renderTime, this);
			}
		} else if (getAnimationState() == AnimationState.Running) {
			// Animation has ended
			if (renderTime >= currentAnimation.animation.animationLength) {
				currentAnimation.resetEventKeyFrames();
				// If the current animation is set to loop, keep it as the current animation and just start over
				if (!currentAnimation.loop) {
					// Pull the next animation from the queue
					if (animationQueue.isEmpty()) {
						// No more animations left, stop the animation controller
						this.animationState = AnimationState.Stopped;
						return;
					} else {
						// Otherwise, set the state to transitioning and start transitioning to the next animation next frame
						this.animationState = AnimationState.Transitioning;
						shouldResetTick = true;
						currentAnimation = this.animationQueue.poll();
					}
				} else {
					// Reset the adjusted tick so the next animation starts at tick 0
					shouldResetTick = true;
					renderTime = adjustTick(actualTick);
				}
			}
			setAnimTime(parser, renderTime);
			// Actually run the animation
			currentAnimation.processCurrentAnimation(renderTime, this);

			if (this.transitionLengthTicks == 0 && shouldResetTick && this.animationState == AnimationState.Transitioning) {
				this.currentAnimation = animationQueue.poll();
			}
		}
	}

	private void setAnimTime(MolangParser parser, double tick) {
		parser.setValue("query.anim_time", tick / 20);
		parser.setValue("query.life_time", tick / 20);
	}

	protected PlayState testAnimationPredicate(AnimationEvent<T> event) {
		return this.animationPredicate.test(this, event);
	}

	// Used to reset the "tick" everytime a new animation starts, a transition starts, or something else of importance happens
	protected double adjustTick(double tick) {
		if (shouldResetTick) {
			if (getAnimationState() == AnimationState.Transitioning) {
				this.tickOffset = tick;
			} else if (getAnimationState() == AnimationState.Running) {
				this.tickOffset = tick;
			}
			shouldResetTick = false;
			return 0;
		}
		//assert tick - this.tickOffset >= 0;
		return Math.max(tick - this.tickOffset, 0.0D);
	}

	//Helper method to transform a KeyFrameLocation to an AnimationPoint
	public static AnimationPoint getAnimationPointAtTick(List<KeyFrame<IValue>> frames, double tick, boolean isRotation,
			Axis axis) {
		KeyFrameLocation<IValue> location = getCurrentKeyFrameLocation(frames, tick);
		KeyFrame<IValue> currentFrame = location.currentFrame;
		double startValue = currentFrame.getStartValue().get();
		double endValue = currentFrame.getEndValue().get();

		if (isRotation) {
			if (!(currentFrame.getStartValue() instanceof ConstantValue)) {
				startValue = Math.toRadians(startValue);
				if (axis == Axis.X || axis == Axis.Y) {
					startValue *= -1;
				}
			}
			if (!(currentFrame.getEndValue() instanceof ConstantValue)) {
				endValue = Math.toRadians(endValue);
				if (axis == Axis.X || axis == Axis.Y) {
					endValue *= -1;
				}
			}
		}

		return new AnimationPoint(currentFrame, location.currentTick, currentFrame.getLength(), startValue, endValue);
	}

	/**
	 * Returns the current keyframe object, plus how long the previous keyframes have taken (aka elapsed animation time)
	 **/
	private static KeyFrameLocation<IValue> getCurrentKeyFrameLocation(List<KeyFrame<IValue>> frames,
			double ageInTicks) {
		double totalTimeTracker = 0;
		for (KeyFrame<IValue> frame : frames) {
			totalTimeTracker += frame.getLength();
			if (totalTimeTracker > ageInTicks) {
				double tick = (ageInTicks - (totalTimeTracker - frame.getLength()));
				return new KeyFrameLocation<>(frame, tick);
			}
		}
		return new KeyFrameLocation<>(frames.get(frames.size() - 1), ageInTicks);
	}


	public void markNeedsReload() {
		this.needsAnimationReload = true;
	}

	public void clearAnimationCache() {
		this.rawAnimations.clear();
	}
}
