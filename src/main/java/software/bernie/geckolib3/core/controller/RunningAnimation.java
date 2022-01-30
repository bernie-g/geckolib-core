package software.bernie.geckolib3.core.controller;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import com.eliotlash.mclib.math.IValue;

import software.bernie.geckolib3.core.ConstantValue;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationQueue;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.keyframe.*;
import software.bernie.geckolib3.core.processor.BoneTree;
import software.bernie.geckolib3.core.processor.DirtyTracker;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.processor.ImmutableBone;
import software.bernie.geckolib3.core.util.Axis;

public class RunningAnimation {
	public final Animation animation;
	public final boolean loop;
	public final double startTime;
	private final BoneTree<?> boneTree;

	private final Queue<EventKeyFrame<String>> soundKeyFrames;
	private final Queue<ParticleEventKeyFrame> particleKeyFrames;
	private final Queue<EventKeyFrame<List<String>>> customInstructionKeyFrames;

	public RunningAnimation(AnimationQueue.QueuedAnimation next, BoneTree<?> boneTree, double renderTime) {
		this.animation = next.animation;
		this.loop = next.loop;
		this.boneTree = boneTree;
		this.startTime = renderTime;

		this.soundKeyFrames = new ArrayDeque<>(next.animation.soundKeyFrames);
		this.particleKeyFrames = new ArrayDeque<>(next.animation.particleKeyFrames);
		this.customInstructionKeyFrames = new ArrayDeque<>(next.animation.customInstructionKeyFrames);
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

	public <T> void process(double renderTime, AnimationController<T> controller) {
		double animationTime = renderTime - startTime;
		// Loop through every boneanimation in the current animation and process the values
		List<BoneAnimation> boneAnimations = animation.boneAnimations;
		for (BoneAnimation boneAnimation : boneAnimations) {
			IBone bone = boneTree.getBoneByName(boneAnimation.boneName);

			ImmutableBone initialSnapshot = bone.getSourceBone();
			DirtyTracker dirtyTracker = bone.getDirtyTracker();

			VectorKeyFrameList<IValue> rotationKeyFrames = boneAnimation.rotationKeyFrames;
			VectorKeyFrameList<IValue> positionKeyFrames = boneAnimation.positionKeyFrames;
			VectorKeyFrameList<IValue> scaleKeyFrames = boneAnimation.scaleKeyFrames;

			if (!rotationKeyFrames.xKeyFrames.isEmpty()) {
				AnimationPoint x = getAnimationPointAtTick(rotationKeyFrames.xKeyFrames, animationTime, true, Axis.X);
				AnimationPoint y = getAnimationPointAtTick(rotationKeyFrames.yKeyFrames, animationTime, true, Axis.Y);
				AnimationPoint z = getAnimationPointAtTick(rotationKeyFrames.zKeyFrames, animationTime, true, Axis.Z);
				bone.setRotationX(x.lerpValues(controller.easingType, controller.customEasingMethod) + initialSnapshot.getRotationX());
				bone.setRotationY(y.lerpValues(controller.easingType, controller.customEasingMethod) + initialSnapshot.getRotationY());
				bone.setRotationZ(z.lerpValues(controller.easingType, controller.customEasingMethod) + initialSnapshot.getRotationZ());
				dirtyTracker.notifyRotationChange();
			}

			if (!positionKeyFrames.xKeyFrames.isEmpty()) {
				AnimationPoint x = getAnimationPointAtTick(positionKeyFrames.xKeyFrames, animationTime, false, Axis.X);
				AnimationPoint y = getAnimationPointAtTick(positionKeyFrames.yKeyFrames, animationTime, false, Axis.Y);
				AnimationPoint z = getAnimationPointAtTick(positionKeyFrames.zKeyFrames, animationTime, false, Axis.Z);
				bone.setPositionX(x.lerpValues(controller.easingType, controller.customEasingMethod));
				bone.setPositionY(y.lerpValues(controller.easingType, controller.customEasingMethod));
				bone.setPositionZ(z.lerpValues(controller.easingType, controller.customEasingMethod));
				dirtyTracker.notifyPositionChange();
			}

			if (!scaleKeyFrames.xKeyFrames.isEmpty()) {
				AnimationPoint x = getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, animationTime, false, Axis.X);
				AnimationPoint y = getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, animationTime, false, Axis.Y);
				AnimationPoint z = getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, animationTime, false, Axis.Z);
				bone.setScaleX(x.lerpValues(controller.easingType, controller.customEasingMethod));
				bone.setScaleY(y.lerpValues(controller.easingType, controller.customEasingMethod));
				bone.setScaleZ(z.lerpValues(controller.easingType, controller.customEasingMethod));
				dirtyTracker.notifyScaleChange();
			}
		}

		processKeyFrames(controller, animationTime);
	}

	private <T> void processKeyFrames(AnimationController<T> controller, double animationTime) {
		processSoundKeyFrames(controller, animationTime);

		processParticleKeyFrames(controller, animationTime);

		processEventKeyFrames(controller, animationTime);
	}

	private <T> void processSoundKeyFrames(AnimationController<T> controller, double animationTime) {
		if (controller.soundListener == null) return;

		EventKeyFrame<String> soundKeyFrame = soundKeyFrames.peek();
		if (soundKeyFrame != null && animationTime >= soundKeyFrame.getStartTick()) {
			SoundKeyframeEvent<T> event = new SoundKeyframeEvent<>(controller.animatable, animationTime, soundKeyFrame.getEventData(), controller);
			controller.soundListener.playSound(event);
			// Remove the sound keyframe from the queue
			soundKeyFrames.poll();
		}
	}

	private <T> void processParticleKeyFrames(AnimationController<T> controller, double animationTime) {
		if (controller.particleListener == null) return;

		ParticleEventKeyFrame particleEventKeyFrame = particleKeyFrames.peek();
		if (particleEventKeyFrame != null && animationTime >= particleEventKeyFrame.getStartTick()) {
			ParticleKeyFrameEvent<T> event = new ParticleKeyFrameEvent<>(controller.animatable, animationTime, particleEventKeyFrame.effect, particleEventKeyFrame.locator, particleEventKeyFrame.script, controller);
			controller.particleListener.summonParticle(event);
			// Remove the particle keyframe from the queue
			particleKeyFrames.poll();
		}
	}

	private <T> void processEventKeyFrames(AnimationController<T> controller, double animationTime) {
		if (controller.customInstructionListener == null) return;

		EventKeyFrame<List<String>> customInstructionKeyFrame = customInstructionKeyFrames.peek();
		if (customInstructionKeyFrame != null && animationTime >= customInstructionKeyFrame.getStartTick()) {
			CustomInstructionKeyframeEvent<T> event = new CustomInstructionKeyframeEvent<>(controller.animatable, animationTime, customInstructionKeyFrame.getEventData(), controller);
			controller.customInstructionListener.executeInstruction(event);
			// Remove the custom instruction keyframe from the queue
			customInstructionKeyFrames.poll();
		}
	}

	public boolean isFinished(double renderTime) {
		return false;
	}
}
