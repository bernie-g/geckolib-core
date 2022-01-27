/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.processor;

import software.bernie.geckolib3.core.util.MathUtil;

public class DirtyTracker {
	private final ResetVector position;
	private final ResetVector rotation;
	private final ResetVector scale;

	public DirtyTracker(IBone bone, ImmutableBone sourceBone) {
		position = new ResetVector(bone::setPositionX, bone::setPositionY, bone::setPositionZ, bone::getPositionX, bone::getPositionY, bone::getPositionZ, sourceBone.getPositionX(), sourceBone.getPositionY(), sourceBone.getPositionZ());
		rotation = new ResetVector(bone::setRotationX, bone::setRotationY, bone::setRotationZ, bone::getRotationX, bone::getRotationY, bone::getRotationZ, sourceBone.getRotationX(), sourceBone.getRotationY(), sourceBone.getRotationZ());
		scale = new ResetVector(bone::setScaleX, bone::setScaleY, bone::setScaleZ, bone::getScaleX, bone::getScaleY, bone::getScaleZ, sourceBone.getScaleX(), sourceBone.getScaleY(), sourceBone.getScaleZ());
	}

	public void beginFrame() {
		position.beginFrame();
		rotation.beginFrame();
		scale.beginFrame();
	}

	public void endFrame(double renderTime) {
		position.endFrame(renderTime);
		rotation.endFrame(renderTime);
		scale.endFrame(renderTime);
	}

	public void notifyScaleChange() {
		this.scale.changed = true;
	}

	public void notifyPositionChange() {
		this.position.changed = true;
	}

	public void notifyRotationChange() {
		this.rotation.changed = true;
	}

	private static class ResetVector {
		private final FloatSetter setX;
		private final FloatSetter setY;
		private final FloatSetter setZ;
		private final FloatGetter getX;
		private final FloatGetter getY;
		private final FloatGetter getZ;

		private final float xReset;
		private final float yReset;
		private final float zReset;

		double xStale = 0;
		double yStale = 0;
		double zStale = 0;

		boolean changed = false;
		boolean changedLastFrame = false;
		double stopTime = Double.NaN;

		public ResetVector(FloatSetter setX, FloatSetter setY, FloatSetter setZ, FloatGetter getX, FloatGetter getY,
				FloatGetter getZ, float xReset, float yReset, float zReset) {
			this.setX = setX;
			this.setY = setY;
			this.setZ = setZ;
			this.getX = getX;
			this.getY = getY;
			this.getZ = getZ;
			this.xReset = xReset;
			this.yReset = yReset;
			this.zReset = zReset;
		}

		void beginFrame() {
			changedLastFrame = changed;
			changed = false;
		}

		void endFrame(double renderTime) {
			if (!changed) {
				if (changedLastFrame) {
					stopTime = renderTime;
					xStale = getX.get();
					yStale = getY.get();
					zStale = getZ.get();
				}

				double percentageReset = Math.min((renderTime - stopTime) / 4, 1);

				setX.set(MathUtil.lerpValues(percentageReset, xStale, xReset));
				setY.set(MathUtil.lerpValues(percentageReset, yStale, yReset));
				setZ.set(MathUtil.lerpValues(percentageReset, zStale, zReset));

				if (percentageReset >= 1) {
					xStale = 0;
					yStale = 0;
					zStale = 0;
				}
			}
		}
	}

	private interface FloatSetter {
		void set(float value);
	}

	private interface FloatGetter {
		float get();
	}
}
