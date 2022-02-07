package software.bernie.geckolib3.core.engine;

import software.bernie.geckolib3.core.bone.IBone;
import software.bernie.geckolib3.core.bone.ImmutableBone;
import software.bernie.geckolib3.core.easing.EasingFunction;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.TimelineValue;

public class RunningBoneAnimation {

	private final IBone bone;

	private TimelineValue rotationX;
	private TimelineValue rotationY;
	private TimelineValue rotationZ;
	private TimelineValue positionX;
	private TimelineValue positionY;
	private TimelineValue positionZ;
	private TimelineValue scaleX;
	private TimelineValue scaleY;
	private TimelineValue scaleZ;

	public RunningBoneAnimation(IBone bone, BoneAnimation animation, double startTime) {
		this.bone = bone;
		if (animation.rotationKeyFrames.hasKeyFrames()) {
			this.rotationX = animation.rotationKeyFrames.x.seek(startTime);
			this.rotationY = animation.rotationKeyFrames.y.seek(startTime);
			this.rotationZ = animation.rotationKeyFrames.z.seek(startTime);
		}
		if (animation.positionKeyFrames.hasKeyFrames()) {
			this.positionX = animation.positionKeyFrames.x.seek(startTime);
			this.positionY = animation.positionKeyFrames.y.seek(startTime);
			this.positionZ = animation.positionKeyFrames.z.seek(startTime);
		}
		if (animation.scaleKeyFrames.hasKeyFrames()) {
			this.scaleX = animation.scaleKeyFrames.x.seek(startTime);
			this.scaleY = animation.scaleKeyFrames.y.seek(startTime);
			this.scaleZ = animation.scaleKeyFrames.z.seek(startTime);
		}

	}

	public void process(double animationTime, EasingFunction easeOverride) {
		rotate(animationTime, easeOverride);
		translate(animationTime, easeOverride);
		scale(animationTime, easeOverride);
	}

	public void rotate(double animationTime, EasingFunction easeOverride) {
		if (rotationX != null) {
			ImmutableBone initialSnapshot = bone.getSourceBone();
			bone.setRotationX((float) (rotationX.get(animationTime, easeOverride) + initialSnapshot.getRotationX()));
			bone.setRotationY((float) (rotationY.get(animationTime, easeOverride) + initialSnapshot.getRotationY()));
			bone.setRotationZ((float) (rotationZ.get(animationTime, easeOverride) + initialSnapshot.getRotationZ()));
		}
	}

	public void translate(double animationTime, EasingFunction easeOverride) {
		if (positionX != null) {
			bone.setPositionX((float) (positionX.get(animationTime, easeOverride)));
			bone.setPositionY((float) (positionY.get(animationTime, easeOverride)));
			bone.setPositionZ((float) (positionZ.get(animationTime, easeOverride)));
		}
	}

	public void scale(double animationTime, EasingFunction easeOverride) {
		if (scaleX != null) {
			bone.setScaleX((float) (scaleX.get(animationTime, easeOverride)));
			bone.setScaleY((float) (scaleY.get(animationTime, easeOverride)));
			bone.setScaleZ((float) (scaleZ.get(animationTime, easeOverride)));
		}
	}
}
