package software.bernie.geckolib3.core.bone;

import java.util.List;

public interface BoneTree {

	List<? extends IBone> getAllBones();

	List<? extends IBone> getTopLevelBones();

	IBone getBoneByName(String name);
}
