package io.codetoil.purpuritis;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class PurpuredObjectHelper {

    private static final BiMap<Class<? extends Item>, Class<? extends Item>> purpuredItemClassMap
            = HashBiMap.create();

    public static @NotNull <I extends Item> Class<I> generatePurpuredItemClass(Class<I> originalItemClass) {
        if (purpuredItemClassMap.containsKey(originalItemClass)) {
            return (Class<I>) purpuredItemClassMap.get(originalItemClass);
        }
        return null; // TODO Replace with Generation via ASM.
    }

    public static <I extends Item> boolean isPurpuredItem(Class<I> itemClass) {
        return purpuredItemClassMap.containsValue(itemClass);
    }

    public static <I extends Item> I createPurpuredItem(I originalItem, Item.Properties properties) {
        try {
            return (I) generatePurpuredItemClass(originalItem.getClass()).getConstructor(Item.Properties.class)
                    .newInstance(properties);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static final BiMap<Class<? extends Block>, Class<? extends Block>> purpuredBlockClassMap
            = HashBiMap.create();

    public static @NotNull <B extends Block> Class<B> generatePurpuredBlockClass(Class<B> originalBlockClass) {
        if (purpuredBlockClassMap.containsKey(originalBlockClass)) {
            return (Class<B>) purpuredBlockClassMap.get(originalBlockClass);
        }
        return null; // TODO Replace with Generation via ASM
    }

    public static <B extends Block> boolean isPurpuredBlock(Class<Block> blockClass) {
        return purpuredBlockClassMap.containsValue(blockClass);
    }

    public static <B extends Block> B createPurpuredBlock(Block originalBlock, Block.Properties properties) {
        try {
            return (B) generatePurpuredBlockClass(originalBlock.getClass()).getConstructor(Block.Properties.class)
                    .newInstance(properties);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
