package software.bernie.geckolib3.core.keyframe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimelineTest {
	@BeforeEach
	void setup() {
	}

	@Test
	void emptyTimeline() {
		var timeline = Timeline.EMPTY;

		assertFalse(timeline.hasKeyFrames());
		assertEquals(0, timeline.getTotalTime());

		assertThrows(IndexOutOfBoundsException.class, timeline::getLast);
	}
}
