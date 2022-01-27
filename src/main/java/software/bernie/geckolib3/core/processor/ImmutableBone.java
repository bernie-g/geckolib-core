package software.bernie.geckolib3.core.processor;

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

    String getName();

    /**
     * Saves a copy of the current state of this bone for later reference.
     * @return An immutable copy of {@code this}.
     */
    default ImmutableBone saveView() {
        return new SavedBone(this);
    }
}
