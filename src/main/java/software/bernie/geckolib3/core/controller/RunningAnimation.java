package software.bernie.geckolib3.core.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import software.bernie.geckolib3.core.builder.Animation;
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
	public final double startTime;
	private final BoneTree<?> boneTree;

	private final Queue<EventKeyFrame<String>> soundKeyFrames;
	private final Queue<ParticleEventKeyFrame> particleKeyFrames;
	private final Queue<EventKeyFrame<List<String>>> customInstructionKeyFrames;
	private final ArrayList<RunningBone> boneAnimations;

	public RunningAnimation(Animation animation, BoneTree<?> boneTree, double renderTime) {
		this.animation = animation;
		this.boneTree = boneTree;
		this.startTime = renderTime;

		this.soundKeyFrames = new ArrayDeque<>(animation.soundKeyFrames);
		this.particleKeyFrames = new ArrayDeque<>(animation.particleKeyFrames);
		this.customInstructionKeyFrames = new ArrayDeque<>(animation.customInstructionKeyFrames);

		boneAnimations = new ArrayList<>();
		for (BoneAnimation boneAnimation : animation.boneAnimations) {
			IBone bone = boneTree.getBoneByName(boneAnimation.boneName);

			if (bone == null) {
				throw new IllegalArgumentException("Bone " + boneAnimation.boneName + " does not exist in the bone tree");
			}

			boneAnimations.add(new RunningBone(bone, boneAnimation.rotationKeyFrames, boneAnimation.positionKeyFrames, boneAnimation.scaleKeyFrames));
		}
	}

	public <T> void process(double renderTime, AnimationController<T> controller) {
		double animationTime = renderTime - startTime;
		for (RunningBone boneAnimation : boneAnimations) {
			IBone bone = boneAnimation.bone;

			ImmutableBone initialSnapshot = bone.getSourceBone();
			DirtyTracker dirtyTracker = bone.getDirtyTracker();

			VectorTimeline rotationKeyFrames = boneAnimation.rotationKeyFrames;
			VectorTimeline positionKeyFrames = boneAnimation.positionKeyFrames;
			VectorTimeline scaleKeyFrames = boneAnimation.scaleKeyFrames;

			if (rotationKeyFrames.xKeyFrames.hasKeyFrames()) {
				double x = rotationKeyFrames.xKeyFrames.getValueAt(animationTime, true, Axis.X, controller.easingType, controller.customEasingMethod);
				double y = rotationKeyFrames.yKeyFrames.getValueAt(animationTime, true, Axis.Y, controller.easingType, controller.customEasingMethod);
				double z = rotationKeyFrames.zKeyFrames.getValueAt(animationTime, true, Axis.Z, controller.easingType, controller.customEasingMethod);
				bone.setRotationX((float) (x + initialSnapshot.getRotationX()));
				bone.setRotationY((float) (y + initialSnapshot.getRotationY()));
				bone.setRotationZ((float) (z + initialSnapshot.getRotationZ()));
				dirtyTracker.notifyRotationChange();
			}

			if (positionKeyFrames.xKeyFrames.hasKeyFrames()) {
				double x = positionKeyFrames.xKeyFrames.getValueAt(animationTime, false, Axis.X, controller.easingType, controller.customEasingMethod);
				double y = positionKeyFrames.yKeyFrames.getValueAt(animationTime, false, Axis.Y, controller.easingType, controller.customEasingMethod);
				double z = positionKeyFrames.zKeyFrames.getValueAt(animationTime, false, Axis.Z, controller.easingType, controller.customEasingMethod);
				bone.setPositionX((float) (x));
				bone.setPositionY((float) (y));
				bone.setPositionZ((float) (z));
				dirtyTracker.notifyPositionChange();
			}

			if (scaleKeyFrames.xKeyFrames.hasKeyFrames()) {
				double x = scaleKeyFrames.xKeyFrames.getValueAt(animationTime, false, Axis.X, controller.easingType, controller.customEasingMethod);
				double y = scaleKeyFrames.yKeyFrames.getValueAt(animationTime, false, Axis.Y, controller.easingType, controller.customEasingMethod);
				double z = scaleKeyFrames.zKeyFrames.getValueAt(animationTime, false, Axis.Z, controller.easingType, controller.customEasingMethod);
				bone.setScaleX((float) (x));
				bone.setScaleY((float) (y));
				bone.setScaleZ((float) (z));
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
		return renderTime > startTime + animation.animationLength;
	}

	private static class RunningBone {

		private final IBone bone;
		private final VectorTimeline rotationKeyFrames;
		private final VectorTimeline positionKeyFrames;
		private final VectorTimeline scaleKeyFrames;

		public RunningBone(IBone bone, VectorTimeline rotationKeyFrames, VectorTimeline positionKeyFrames,
				VectorTimeline scaleKeyFrames) {
			this.bone = bone;
			this.rotationKeyFrames = rotationKeyFrames;
			this.positionKeyFrames = positionKeyFrames;
			this.scaleKeyFrames = scaleKeyFrames;
		}
	}
}
