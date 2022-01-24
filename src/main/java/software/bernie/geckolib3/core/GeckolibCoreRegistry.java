package software.bernie.geckolib3.core;

import software.bernie.geckolib3.core.model.IAnimatableModel;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class GeckolibCoreRegistry {
    private static Map<Class<? extends IAnimatable>, IAnimatableModel> modelRegistry = new ConcurrentHashMap<>();

    /**
     * Registers a model to geckolib's internal registry. You probably shouldn't call this directly unless you're not
     * using one of geckolib's built-in renderers.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param model the model
     */
    public static <T extends IAnimatable> void registerModel(Class<? extends IAnimatable> clazz, IAnimatableModel<T> model) {
        modelRegistry.put(clazz, model);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IAnimatable> Optional<IAnimatableModel<T>> getModel(T animatable) {
        IAnimatableModel<T> model = modelRegistry.get(animatable.getClass());
        if(model == null){
            GeckolibCore.getLogger().error("Could not find registered renderer/model for {}", animatable.getClass());
        }
        return Optional.ofNullable(model);
    }
}
