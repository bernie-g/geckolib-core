package software.bernie.geckolib3.core.builder;

import java.util.ArrayDeque;
import java.util.Queue;

public class AnimationQueue {
	private final Queue<QueuedAnimation> queue = new ArrayDeque<>();

	public int size() {
		return queue.size();
	}

	public void add(Animation animation, boolean loop) {
		queue.add(new QueuedAnimation(animation, loop));
	}

	public QueuedAnimation poll() {
		return queue.poll();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public QueuedAnimation peek() {
		return queue.peek();
	}

	public static class QueuedAnimation {
		public final Animation animation;
		public final boolean loop;

		private QueuedAnimation(Animation animation, boolean loop) {
			this.animation = animation;
			this.loop = loop;
		}
	}
}
