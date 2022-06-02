package software.bernie.geckolib3.core.bone;

/**
 * A read only view of a bone.
 */
public interface ImmutableBone {
	float getRotationX();

	float getRotationY();

	float getRotationZ();

	float getPositionX();

	float getPositionY();

	float getPositionZ();

	float getScaleX();

	float getScaleY();

	float getScaleZ();

	float getPivotX();

	float getPivotY();

	float getPivotZ();

	boolean isHidden();
	boolean cubesAreHidden();
	boolean childBonesAreHiddenToo();
	void setCubesHidden(boolean hidden);
	void setHidden(boolean selfHidden, boolean skipChildRendering);

	void setHidden(boolean hidden);

	String getName();

	/**
	 * Saves a copy of the current state of this bone for later reference.
	 *
	 * @return An immutable copy of {@code this}.
	 */
	default ImmutableBone saveView() {
		return new SavedBone(this);
	}
}
