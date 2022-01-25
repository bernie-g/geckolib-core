/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.manager;

import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.keyframe.BoneAnimationQueue;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.core.snapshot.DirtyTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationData
{
	private final List<IBone> modelRendererList = new ArrayList<>();
	private final Map<String, IBone> nameLookup = new HashMap<>();
	private final Map<IBone, BoneSnapshot> boneSnapshotCollection = new HashMap<>();
	private final Map<String, AnimationController> animationControllers = new HashMap<>();
	public boolean isFirstTick = true;
	private double resetTickLength = 1;
	public Object ticker;
	public boolean shouldPlayWhilePaused = false;

	/**
	 * This method is how you register animation controllers, without this, your AnimationPredicate method will never be called
	 *
	 * @param value The value
	 * @return the animation controller
	 */
	public <T> AnimationController<T> addAnimationController(AnimationController<T> value)
	{
		this.animationControllers.put(value.getName(), value);
		return value;
	}

	public double getResetSpeed()
	{
		return resetTickLength;
	}

	/**
	 * This is how long it takes for any bones that don't have an animation to revert back to their original position
	 *
	 * @param resetTickLength The amount of ticks it takes to reset. Cannot be negative.
	 */
	public void setResetSpeedInTicks(double resetTickLength)
	{
		this.resetTickLength = resetTickLength < 0 ? 0 : resetTickLength;
	}

	public void setModelRendererList(List<IBone> modelRendererList)
	{
		if (this.modelRendererList.isEmpty())
		{
			this.modelRendererList.addAll(modelRendererList);
			for (IBone iBone : this.modelRendererList) {
				this.nameLookup.put(iBone.getName(), iBone);
				iBone.saveInitialSnapshot();
			}
		}
	}

	public Map<String, AnimationController> getAnimationControllers()
	{
		return animationControllers;
	}

	public Map<IBone, BoneSnapshot> updateBoneSnapshots()
	{
		for (IBone bone : modelRendererList)
		{
			if (!boneSnapshotCollection.containsKey(bone))
			{
				boneSnapshotCollection.put(bone, new BoneSnapshot(bone.getInitialSnapshot()));
			}
		}
		return boneSnapshotCollection;
	}

	public Map<IBone, DirtyTracker> createNewDirtyTracker()
	{
		HashMap<IBone, DirtyTracker> tracker = new HashMap<>();
		for (IBone bone : modelRendererList)
		{
			tracker.put(bone, new DirtyTracker(false, false, false, bone));
		}
		return tracker;
	}

	public IBone getBone(String name)
	{
		return nameLookup.get(name);
	}

	public void createBoneAnimationQueues(Map<String, BoneAnimationQueue> out) {
		out.clear();
		for (IBone bone : modelRendererList) {
			out.put(bone.getName(), new BoneAnimationQueue(bone));
		}
	}
}
