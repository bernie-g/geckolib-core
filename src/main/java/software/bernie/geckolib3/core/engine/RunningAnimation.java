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
import software.bernie.geckolib3.core.bone.IBone;

public class RunningAnimation {
	public final Animation animation;
	public final double startTime;

	private final Queue<EventKeyFrame<String>> soundKeyFrames;
	private final Queue<ParticleEventKeyFrame> particleKeyFrames;
	private final Queue<EventKeyFrame<List<String>>> customInstructionKeyFrames;
	private final ArrayList<RunningBoneAnimation> boneAnimations;

	public RunningAnimation(Animation animation, BoneTree boneTree, double renderTime) {
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

			boneAnimations.add(new RunningBoneAnimation(bone, boneAnimation, 0));
		}
	}

	public <T> void process(double renderTime, AnimationChannel<T> controller) {
		double animationTime = renderTime - startTime;

		for (RunningBoneAnimation boneAnimation : boneAnimations) {
			boneAnimation.process(animationTime, controller.easeOverride);
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

}
