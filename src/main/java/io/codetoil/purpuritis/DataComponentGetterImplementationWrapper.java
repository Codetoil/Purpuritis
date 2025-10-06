package io.codetoil.purpuritis;

import io.codetoil.purpuritis.core.component.PurpuritisDataComponentTypes;
import io.codetoil.purpuritis.world.food.PurpuritisFoodHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class DataComponentGetterImplementationWrapper {

    public static <T> T onGet(DataComponentType<? extends T> dataComponentType, Function<DataComponentType<?>, ?> get)
    {
        T value = (T) get.apply(dataComponentType);

        if (PurpuritisDataComponentTypes.PURPURED.get().equals(dataComponentType)) return value;

        if (get.apply(PurpuritisDataComponentTypes.PURPURED.get()) == null) return value;

        if (DataComponents.ITEM_NAME.equals(dataComponentType)) {
            if (value instanceof Component) {
                return (T) Component.literal("Purpured ").append((Component) value);
            }
        }
        if (DataComponents.RARITY.equals(dataComponentType)) {
            if (value instanceof Rarity) {
                return (T) Rarity.BY_ID.apply(Math.min(((Rarity) value).ordinal() + 1, Rarity.values().length));
            }
        }
        if (DataComponents.FOOD.equals(dataComponentType)) {
            if (value instanceof FoodProperties) {
                return (T) PurpuritisFoodHelper.getPurpuredFoodProperties((FoodProperties) value);
            }
        }
        return value;
    }
}
