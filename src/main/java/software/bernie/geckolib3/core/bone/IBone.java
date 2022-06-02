package software.bernie.geckolib3.core.bone;

public interface IBone extends ImmutableBone {

	void setRotationX(float value);

	void setRotationY(float value);

	void setRotationZ(float value);

	void setPositionX(float value);

	void setPositionY(float value);

	void setPositionZ(float value);

	void setScaleX(float value);

	void setScaleY(float value);

	void setScaleZ(float value);

	void setPivotX(float value);

	void setPivotY(float value);

	void setPivotZ(float value);

	/**
	 * Get the reference bone that this animatable bone is based on.
	 *
	 * @return The reference bone.
	 */
	ImmutableBone getSourceBone();

}
