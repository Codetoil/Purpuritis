package io.codetoil.purpuritis.world.item;

import io.codetoil.dynamic_registries.api.DynamicRegistriesObjectHelper;
import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PurpuredItemHelper {

    public static final DynamicRegistriesObjectHelper<Item> ITEM_DYNAMIC_REGISTRIES_OBJECT_HELPER =
            new DynamicRegistriesObjectHelper<>((Constructor<? extends Item> constructor, Integer index) -> {
                if (constructor.getParameterTypes()[index] == Block.class) {
                    return (Item item) -> {
                        if (item instanceof BlockItem) {
                            return Purpuritis.purpuredBlocks.get(((BlockItem) item).getBlock());
                        } else if (item instanceof AirItem) {
                            return Blocks.AIR;
                        } else {
                            return null;
                        }
                    };
                }
                if (constructor.getParameterTypes()[index] == Item.Properties.class) {
                    return (Item item) -> {
                        ResourceKey<Item> key = ResourceKey.create(ForgeRegistries.ITEMS.getRegistryKey(),
                                ForgeRegistries.ITEMS.getKey(item));
                        return new Item.Properties().setId(key); // TODO implement custom Item properties
                    };
                }
                return null;
            }, ResourceLocation.fromNamespaceAndPath(Purpuritis.MOD_ID, "item"));

    public static <I extends Item> I createPurpuredItem(I originalItem) {
        Class<I> originalItemClass = (Class<I>) originalItem.getClass();
        try {
            return ITEM_DYNAMIC_REGISTRIES_OBJECT_HELPER.getClass(originalItemClass)
                    .getConstructor(originalItemClass, DynamicRegistriesObjectHelper.class)
                    .newInstance(originalItem, ITEM_DYNAMIC_REGISTRIES_OBJECT_HELPER);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
