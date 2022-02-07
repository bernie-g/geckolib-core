package software.bernie.geckolib3.core;

import com.eliotlash.molang.MolangParser;

import software.bernie.geckolib3.core.builder.Animation;

public interface ModelType<E> {
	Animation getAnimation(E entity, String animationName);

	void setMolangQueries(E object, MolangParser parser, double renderTime);
}
