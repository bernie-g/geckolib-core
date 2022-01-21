/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.manager;

import software.bernie.geckolib3.core.IAnimated;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.core.snapshot.DirtyTracker;

import java.util.HashMap;
import java.util.Map;

public class AnimationData
{
	private final HashMap<IBone, BoneSnapshot> boneSnapshotCollection = new HashMap<>();
	private final HashMap<String, AnimationController> animationControllers = new HashMap<>();
	public double tick;
	public boolean isFirstTick = true;
	private double resetTickLength = 1;
	public Double startTick;
	public Object ticker;
	public boolean shouldPlayWhilePaused = false;

	/**
	 * This method is how you register animation controllers, without this, your AnimationPredicate method will never be called
	 *
	 * @param value The value
	 * @return the animation controller
	 */
	public <T extends IAnimated> AnimationController<T> addAnimationController(AnimationController<T> value)
	{
		return this.animationControllers.put(value.getName(), value);
	}

	public Map<IBone, BoneSnapshot> getBoneSnapshotCollection()
	{
		return boneSnapshotCollection;
	}

	public void clearSnapshotCache()
	{
		this.boneSnapshotCollection.clear();
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

	public HashMap<String, AnimationController> getAnimationControllers()
	{
		return animationControllers;
	}

	public Map<IBone, BoneSnapshot> updateBoneSnapshots()
	{
		Map<IBone, BoneSnapshot> boneSnapshotCollection = getBoneSnapshotCollection();
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
		return null;
	}
}
