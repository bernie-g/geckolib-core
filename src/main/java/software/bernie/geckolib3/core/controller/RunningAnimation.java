package software.bernie.geckolib3.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eliotlash.mclib.math.IValue;

import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.keyframe.*;
import software.bernie.geckolib3.core.processor.BoneTree;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.processor.ImmutableBone;
import software.bernie.geckolib3.core.processor.DirtyTracker;
import software.bernie.geckolib3.core.util.Axis;

public class RunningAnimation {
    public final Animation animation;
    public final boolean loop;

    public final Map<IBone, ImmutableBone> initialValues = new HashMap<>();
    public double startTime;
    private double transitionTime;
    private BoneTree<?> boneTree;

    public RunningAnimation(Animation animation, boolean loop) {
        this.animation = animation;
        this.loop = loop;
    }

    public void begin(BoneTree<?> boneTree, double renderTime, double transitionTime) {
        this.boneTree = boneTree;
        this.transitionTime = transitionTime;
        this.startTime = renderTime;
        for (IBone bone : boneTree.getAllBones()) {
            initialValues.put(bone, bone.saveView());
        }
    }

    void resetEventKeyFrames()
    {
        for (EventKeyFrame<String> soundKeyFrame : animation.soundKeyFrames)
        {
            soundKeyFrame.hasExecuted = false;
        }
        for (ParticleEventKeyFrame particleKeyFrame : animation.particleKeyFrames)
        {
            particleKeyFrame.hasExecuted = false;
        }
        for (EventKeyFrame<List<String>> customInstructionKeyFrame : animation.customInstructionKeyframes)
        {
            customInstructionKeyFrame.hasExecuted = false;
        }
    }

    public <T> void processCurrentAnimation(double renderTime, AnimationController<T> controller)
    {
        double animationTime = renderTime - startTime;
        // Loop through every boneanimation in the current animation and process the values
        List<BoneAnimation> boneAnimations = animation.boneAnimations;
        for (BoneAnimation boneAnimation : boneAnimations)
        {
            IBone bone = boneTree.getBoneByName(boneAnimation.boneName);

            ImmutableBone initialSnapshot = bone.getSourceBone();
            DirtyTracker dirtyTracker = bone.getDirtyTracker();

            VectorKeyFrameList<IValue> rotationKeyFrames = boneAnimation.rotationKeyFrames;
            VectorKeyFrameList<IValue> positionKeyFrames = boneAnimation.positionKeyFrames;
            VectorKeyFrameList<IValue> scaleKeyFrames = boneAnimation.scaleKeyFrames;

            if (!rotationKeyFrames.xKeyFrames.isEmpty())
            {
                AnimationPoint x = AnimationController.getAnimationPointAtTick(rotationKeyFrames.xKeyFrames, animationTime, true, Axis.X);
                AnimationPoint y = AnimationController.getAnimationPointAtTick(rotationKeyFrames.yKeyFrames, animationTime, true, Axis.Y);
                AnimationPoint z = AnimationController.getAnimationPointAtTick(rotationKeyFrames.zKeyFrames, animationTime, true, Axis.Z);
                bone.setRotationX(x.lerpValues(controller.easingType, controller.customEasingMethod) + initialSnapshot.getRotationX());
                bone.setRotationY(y.lerpValues(controller.easingType, controller.customEasingMethod) + initialSnapshot.getRotationY());
                bone.setRotationZ(z.lerpValues(controller.easingType, controller.customEasingMethod) + initialSnapshot.getRotationZ());
                dirtyTracker.notifyRotationChange();
            }

            if (!positionKeyFrames.xKeyFrames.isEmpty())
            {
                AnimationPoint x = AnimationController.getAnimationPointAtTick(positionKeyFrames.xKeyFrames, animationTime, false, Axis.X);
                AnimationPoint y = AnimationController.getAnimationPointAtTick(positionKeyFrames.yKeyFrames, animationTime, false, Axis.Y);
                AnimationPoint z = AnimationController.getAnimationPointAtTick(positionKeyFrames.zKeyFrames, animationTime, false, Axis.Z);
                bone.setPositionX(x.lerpValues(controller.easingType, controller.customEasingMethod));
                bone.setPositionY(y.lerpValues(controller.easingType, controller.customEasingMethod));
                bone.setPositionZ(z.lerpValues(controller.easingType, controller.customEasingMethod));
                dirtyTracker.notifyPositionChange();
            }

            if (!scaleKeyFrames.xKeyFrames.isEmpty())
            {
                AnimationPoint x = AnimationController.getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, animationTime, false, Axis.X);
                AnimationPoint y = AnimationController.getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, animationTime, false, Axis.Y);
                AnimationPoint z = AnimationController.getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, animationTime, false, Axis.Z);
                bone.setScaleX(x.lerpValues(controller.easingType, controller.customEasingMethod));
                bone.setScaleY(y.lerpValues(controller.easingType, controller.customEasingMethod));
                bone.setScaleZ(z.lerpValues(controller.easingType, controller.customEasingMethod));
                dirtyTracker.notifyScaleChange();
            }
        }

        if (controller.soundListener != null || controller.particleListener != null || controller.customInstructionListener != null)
        {
            for (EventKeyFrame<String> soundKeyFrame : animation.soundKeyFrames)
            {
                if (!soundKeyFrame.hasExecuted && animationTime >= soundKeyFrame.getStartTick())
                {
                    SoundKeyframeEvent<T> event = new SoundKeyframeEvent<>(controller.animatable, animationTime, soundKeyFrame.getEventData(), controller);
                    controller.soundListener.playSound(event);
                    soundKeyFrame.hasExecuted = true;
                }
            }

            for (ParticleEventKeyFrame particleEventKeyFrame : animation.particleKeyFrames)
            {
                if (!particleEventKeyFrame.hasExecuted && animationTime >= particleEventKeyFrame.getStartTick())
                {
                    ParticleKeyFrameEvent<T> event = new ParticleKeyFrameEvent<>(controller.animatable, animationTime,
                            particleEventKeyFrame.effect, particleEventKeyFrame.locator, particleEventKeyFrame.script, controller);
                    controller.particleListener.summonParticle(event);
                    particleEventKeyFrame.hasExecuted = true;
                }
            }

            for (EventKeyFrame<List<String>> customInstructionKeyFrame : animation.customInstructionKeyframes)
            {
                if (!customInstructionKeyFrame.hasExecuted && animationTime >= customInstructionKeyFrame.getStartTick())
                {
                    CustomInstructionKeyframeEvent<T> event = new CustomInstructionKeyframeEvent<>(controller.animatable, animationTime,
                            customInstructionKeyFrame.getEventData(), controller);
                    controller.customInstructionListener.executeInstruction(event);
                    customInstructionKeyFrame.hasExecuted = true;
                }
            }
        }
    }

    void runTransition(double renderTime, AnimationController<?> controller) {
        double tick = renderTime - startTime;

        for (BoneAnimation boneAnimation : animation.boneAnimations)
        {
            IBone bone = boneTree.getBoneByName(boneAnimation.boneName);
            if (bone == null) {
                continue;
            }
            ImmutableBone boneSnapshot = initialValues.get(bone);

            ImmutableBone initialSnapshot = bone.getSourceBone();

            VectorKeyFrameList<IValue> rotationKeyFrames = boneAnimation.rotationKeyFrames;
            VectorKeyFrameList<IValue> positionKeyFrames = boneAnimation.positionKeyFrames;
            VectorKeyFrameList<IValue> scaleKeyFrames = boneAnimation.scaleKeyFrames;

            DirtyTracker dirtyTracker = bone.getDirtyTracker();

            // Adding the initial positions of the upcoming animation, so the model transitions to the initial state of the new animation
            if (!rotationKeyFrames.xKeyFrames.isEmpty())
            {
                AnimationPoint xPoint = AnimationController.getAnimationPointAtTick(rotationKeyFrames.xKeyFrames, 0, true, Axis.X);
                AnimationPoint yPoint = AnimationController.getAnimationPointAtTick(rotationKeyFrames.yKeyFrames, 0, true, Axis.Y);
                AnimationPoint zPoint = AnimationController.getAnimationPointAtTick(rotationKeyFrames.zKeyFrames, 0, true, Axis.Z);
                AnimationPoint x = new AnimationPoint(null, tick, transitionTime, boneSnapshot.getRotationX() - initialSnapshot.getRotationX(), xPoint.animationStartValue);
                AnimationPoint y = new AnimationPoint(null, tick, transitionTime, boneSnapshot.getRotationY() - initialSnapshot.getRotationY(), yPoint.animationStartValue);
                AnimationPoint z = new AnimationPoint(null, tick, transitionTime, boneSnapshot.getRotationZ() - initialSnapshot.getRotationZ(), zPoint.animationStartValue);
                bone.setRotationX(x.lerpValues(controller.easingType, controller.customEasingMethod) + initialSnapshot.getRotationX());
                bone.setRotationY(y.lerpValues(controller.easingType, controller.customEasingMethod) + initialSnapshot.getRotationY());
                bone.setRotationZ(z.lerpValues(controller.easingType, controller.customEasingMethod) + initialSnapshot.getRotationZ());
                dirtyTracker.notifyRotationChange();
            }

            if (!positionKeyFrames.xKeyFrames.isEmpty())
            {
                AnimationPoint xPoint = AnimationController.getAnimationPointAtTick(positionKeyFrames.xKeyFrames, 0, true, Axis.X);
                AnimationPoint yPoint = AnimationController.getAnimationPointAtTick(positionKeyFrames.yKeyFrames, 0, true, Axis.Y);
                AnimationPoint zPoint = AnimationController.getAnimationPointAtTick(positionKeyFrames.zKeyFrames, 0, true, Axis.Z);
                AnimationPoint x = new AnimationPoint(null, tick, transitionTime, boneSnapshot.getPositionX(), xPoint.animationStartValue);
                AnimationPoint y = new AnimationPoint(null, tick, transitionTime, boneSnapshot.getPositionY(), yPoint.animationStartValue);
                AnimationPoint z = new AnimationPoint(null, tick, transitionTime, boneSnapshot.getPositionZ(), zPoint.animationStartValue);
                bone.setPositionX(x.lerpValues(controller.easingType, controller.customEasingMethod));
                bone.setPositionY(y.lerpValues(controller.easingType, controller.customEasingMethod));
                bone.setPositionZ(z.lerpValues(controller.easingType, controller.customEasingMethod));
                dirtyTracker.notifyPositionChange();
            }

            if (!scaleKeyFrames.xKeyFrames.isEmpty())
            {
                AnimationPoint xPoint = AnimationController.getAnimationPointAtTick(scaleKeyFrames.xKeyFrames, 0, true, Axis.X);
                AnimationPoint yPoint = AnimationController.getAnimationPointAtTick(scaleKeyFrames.yKeyFrames, 0, true, Axis.Y);
                AnimationPoint zPoint = AnimationController.getAnimationPointAtTick(scaleKeyFrames.zKeyFrames, 0, true, Axis.Z);
                AnimationPoint x = new AnimationPoint(null, tick, transitionTime, boneSnapshot.getScaleX(), xPoint.animationStartValue);
                AnimationPoint y = new AnimationPoint(null, tick, transitionTime, boneSnapshot.getScaleY(), yPoint.animationStartValue);
                AnimationPoint z = new AnimationPoint(null, tick, transitionTime, boneSnapshot.getScaleZ(), zPoint.animationStartValue);
                bone.setScaleX(x.lerpValues(controller.easingType, controller.customEasingMethod));
                bone.setScaleY(y.lerpValues(controller.easingType, controller.customEasingMethod));
                bone.setScaleZ(z.lerpValues(controller.easingType, controller.customEasingMethod));
                dirtyTracker.notifyScaleChange();
            }
        }
    }
}
