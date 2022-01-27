package software.bernie.geckolib3.core.builder;

import java.util.ArrayDeque;
import java.util.Queue;

import software.bernie.geckolib3.core.controller.RunningAnimation;

public class AnimationQueue {
	private final Queue<QueuedAnimation> queue = new ArrayDeque<>();

	public int size() {
		return queue.size();
	}

	public void add(Animation animation, boolean loop) {
		queue.add(new QueuedAnimation(animation, loop));
	}

	public RunningAnimation poll() {
		QueuedAnimation queuedAnimation = queue.poll();
		if (queuedAnimation == null) {
			return null;
		}
		return new RunningAnimation(queuedAnimation.animation, queuedAnimation.loop);
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	private static class QueuedAnimation {
		public final Animation animation;
		public final boolean loop;

		private QueuedAnimation(Animation animation, boolean loop) {
			this.animation = animation;
			this.loop = loop;
		}
	}
}
