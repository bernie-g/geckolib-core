package software.bernie.geckolib3.core.bone;

public class SavedBone implements ImmutableBone {

	private final String name;
	private final boolean isHidden;
	private final float rotationX;
	private final float rotationY;
	private final float rotationZ;
	private final float positionX;
	private final float positionY;
	private final float positionZ;
	private final float scaleX;
	private final float scaleY;
	private final float scaleZ;
	private final float pivotX;
	private final float pivotY;
	private final float pivotZ;

	public SavedBone(ImmutableBone immutableBone) {
		this.name = immutableBone.getName();
		this.isHidden = immutableBone.isHidden();
		this.rotationX = immutableBone.getRotationX();
		this.rotationY = immutableBone.getRotationY();
		this.rotationZ = immutableBone.getRotationZ();
		this.positionX = immutableBone.getPositionX();
		this.positionY = immutableBone.getPositionY();
		this.positionZ = immutableBone.getPositionZ();
		this.scaleX = immutableBone.getScaleX();
		this.scaleY = immutableBone.getScaleY();
		this.scaleZ = immutableBone.getScaleZ();
		this.pivotX = immutableBone.getPivotX();
		this.pivotY = immutableBone.getPivotY();
		this.pivotZ = immutableBone.getPivotZ();

	}

	@Override
	public float getRotationX() {
		return this.rotationX;
	}

	@Override
	public float getRotationY() {
		return this.rotationY;
	}

	@Override
	public float getRotationZ() {
		return this.rotationZ;
	}

	@Override
	public float getPositionX() {
		return this.positionX;
	}

	@Override
	public float getPositionY() {
		return this.positionY;
	}

	@Override
	public float getPositionZ() {
		return this.positionZ;
	}

	@Override
	public float getScaleX() {
		return this.scaleX;
	}

	@Override
	public float getScaleY() {
		return this.scaleY;
	}

	@Override
	public float getScaleZ() {
		return this.scaleZ;
	}

	@Override
	public float getPivotX() {
		return this.pivotX;
	}

	@Override
	public float getPivotY() {
		return this.pivotY;
	}

	@Override
	public float getPivotZ() {
		return this.pivotZ;
	}

	@Override
	public boolean isHidden() {
		return this.isHidden;
	}

	@Override
	public String getName() {
		return this.name;
	}
}
