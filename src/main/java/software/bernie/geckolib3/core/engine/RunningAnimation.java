package software.bernie.geckolib3.core.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.keyframe.*;
import software.bernie.geckolib3.core.bone.BoneTree;
import software.bernie.geckolib3.core.bone.DirtyTracker;
import software.bernie.geckolib3.core.bone.IBone;
import software.bernie.geckolib3.core.bone.ImmutableBone;

public class RunningAnimation {
	public final Animation animation;
	public final double startTime;

	private final Queue<EventKeyFrame<String>> soundKeyFrames;
	private final Queue<ParticleEventKeyFrame> particleKeyFrames;
	private final Queue<EventKeyFrame<List<String>>> customInstructionKeyFrames;
	private final ArrayList<RunningBone> boneAnimations;

	public RunningAnimation(Animation animation, BoneTree<?> boneTree, double renderTime) {
		this.animation = animation;
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

	public <T> void process(double renderTime, AnimationChannel<T> controller) {
		double animationTime = renderTime - startTime;

		for (RunningBone boneAnimation : boneAnimations) {
			IBone bone = boneAnimation.bone;

			ImmutableBone initialSnapshot = bone.getSourceBone();
			DirtyTracker dirtyTracker = bone.getDirtyTracker();

			VectorTimeline rotationKeyFrames = boneAnimation.rotationKeyFrames;
			VectorTimeline positionKeyFrames = boneAnimation.positionKeyFrames;
			VectorTimeline scaleKeyFrames = boneAnimation.scaleKeyFrames;

			if (rotationKeyFrames.hasKeyFrames()) {
				double x = rotationKeyFrames.x.getValueAt(animationTime, controller.easeOverride);
				double y = rotationKeyFrames.y.getValueAt(animationTime, controller.easeOverride);
				double z = rotationKeyFrames.z.getValueAt(animationTime, controller.easeOverride);
				bone.setRotationX((float) (x + initialSnapshot.getRotationX()));
				bone.setRotationY((float) (y + initialSnapshot.getRotationY()));
				bone.setRotationZ((float) (z + initialSnapshot.getRotationZ()));
				dirtyTracker.notifyRotationChange();
			}

			if (positionKeyFrames.hasKeyFrames()) {
				double x = positionKeyFrames.x.getValueAt(animationTime, controller.easeOverride);
				double y = positionKeyFrames.y.getValueAt(animationTime, controller.easeOverride);
				double z = positionKeyFrames.z.getValueAt(animationTime, controller.easeOverride);
				bone.setPositionX((float) (x));
				bone.setPositionY((float) (y));
				bone.setPositionZ((float) (z));
				dirtyTracker.notifyPositionChange();
			}

			if (scaleKeyFrames.hasKeyFrames()) {
				double x = scaleKeyFrames.x.getValueAt(animationTime, controller.easeOverride);
				double y = scaleKeyFrames.y.getValueAt(animationTime, controller.easeOverride);
				double z = scaleKeyFrames.z.getValueAt(animationTime, controller.easeOverride);
				bone.setScaleX((float) (x));
				bone.setScaleY((float) (y));
				bone.setScaleZ((float) (z));
				dirtyTracker.notifyScaleChange();
			}
		}

		processKeyFrames(controller, animationTime);
	}

	private <T> void processKeyFrames(AnimationChannel<T> controller, double animationTime) {
		processSoundKeyFrames(controller, animationTime);

		processParticleKeyFrames(controller, animationTime);

		processEventKeyFrames(controller, animationTime);
	}

	private <T> void processSoundKeyFrames(AnimationChannel<T> controller, double animationTime) {
		if (controller.soundListener == null) return;

		EventKeyFrame<String> soundKeyFrame = soundKeyFrames.peek();
		if (soundKeyFrame != null && animationTime >= soundKeyFrame.getStartTick()) {
			SoundKeyframeEvent<T> event = new SoundKeyframeEvent<>(animationTime, soundKeyFrame.getEventData(), controller);
			controller.soundListener.playSound(event);
			// Remove the sound keyframe from the queue
			soundKeyFrames.poll();
		}
	}

	private <T> void processParticleKeyFrames(AnimationChannel<T> controller, double animationTime) {
		if (controller.particleListener == null) return;

		ParticleEventKeyFrame particleEventKeyFrame = particleKeyFrames.peek();
		if (particleEventKeyFrame != null && animationTime >= particleEventKeyFrame.getStartTick()) {
			ParticleKeyFrameEvent<T> event = new ParticleKeyFrameEvent<>(animationTime, particleEventKeyFrame.effect, particleEventKeyFrame.locator, particleEventKeyFrame.script, controller);
			controller.particleListener.summonParticle(event);
			// Remove the particle keyframe from the queue
			particleKeyFrames.poll();
		}
	}

	private <T> void processEventKeyFrames(AnimationChannel<T> controller, double animationTime) {
		if (controller.customInstructionListener == null) return;

		EventKeyFrame<List<String>> customInstructionKeyFrame = customInstructionKeyFrames.peek();
		if (customInstructionKeyFrame != null && animationTime >= customInstructionKeyFrame.getStartTick()) {
			CustomInstructionKeyframeEvent<T> event = new CustomInstructionKeyframeEvent<>(animationTime, customInstructionKeyFrame.getEventData(), controller);
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
