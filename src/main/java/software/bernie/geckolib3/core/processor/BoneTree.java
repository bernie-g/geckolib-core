package software.bernie.geckolib3.core.processor;

import java.util.List;

public interface BoneTree<B extends IBone> {

	List<B> getAllBones();

	List<B> getTopLevelBones();

	B getBoneByName(String name);
}
