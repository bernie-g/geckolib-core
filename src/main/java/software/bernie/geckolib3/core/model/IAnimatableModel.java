package software.bernie.geckolib3.core.model;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;

import java.util.Optional;

public interface IAnimatableModel<T extends IAnimatable> extends IModel {
    default double getCurrentTick() {
        return (System.nanoTime() / 1000000L / 50.0);
    }

    default void setLivingAnimations(T entity, Integer uniqueID) {
        this.setLivingAnimations(entity, uniqueID, null);
    }

    void setLivingAnimations(T entity, Integer uniqueID, AnimationEvent customPredicate);

    AnimationProcessor<T> getAnimationProcessor();

    Optional<Animation> getAnimation(String name, IAnimatable animatable);

    /**
     * Gets a bone by name.
     *
     * @param boneName The bone name
     * @return the bone
     */
    @Override
    default IBone getBone(String boneName) {
        IBone bone = this.getAnimationProcessor().getBone(boneName);
        if (bone == null) {
            throw new RuntimeException("Could not find bone: " + boneName);
        }
        return bone;
    }

    void setMolangQueries(IAnimatable animatable, double currentTick);
}
