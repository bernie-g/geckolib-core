package software.bernie.geckolib3.core.keyframe;

import software.bernie.geckolib3.core.easing.EasingFunction;

public interface TimelineValue {
	double get(double animationTime, EasingFunction easeOverride);
}
