package io.codetoil.purpuritis.world.food;

import net.minecraft.world.food.FoodProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PurpuritisFoodHelper {
    private static final Map<FoodProperties, FoodProperties> PROPERTIES_CACHE = new ConcurrentHashMap<>();

    public static FoodProperties getPurpuredFoodProperties(FoodProperties properties) {
        if (PROPERTIES_CACHE.containsKey(properties)) {
            return PROPERTIES_CACHE.get(properties);
        }
        return new FoodProperties(properties.nutrition() + 5, properties.saturation() + 1.2F, properties.canAlwaysEat());
    }
}
