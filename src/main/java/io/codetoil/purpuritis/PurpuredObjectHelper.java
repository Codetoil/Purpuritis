package io.codetoil.purpuritis;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class PurpuredObjectHelper {

    private static final BiMap<Class<? extends Item>, Class<? extends Item>> purpuredItemClassMap
            = HashBiMap.create();

    public static @NotNull <I extends Item> Class<I> generatePurpuredItemClass(Class<I> originalItemClass) {
        if (purpuredItemClassMap.containsKey(originalItemClass)) {
            return (Class<I>) purpuredItemClassMap.get(originalItemClass);
        }
        return originalItemClass; // TODO Replace with Generation via ASM.
    }

    public static <I extends Item> boolean isPurpuredItem(Class<I> itemClass) {
        return purpuredItemClassMap.containsValue(itemClass);
    }

    public static <I extends Item> I createPurpuredItem(I originalItem,
                                                        Item.Properties properties) {
        Class<I> originalItemClass = (Class<I>) originalItem.getClass();
        try {
            return generatePurpuredItemClass(originalItemClass).getConstructor(originalItemClass, Item.Properties.class)
                    .newInstance(originalItem, properties);
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
        return originalBlockClass; // TODO Replace with Generation via ASM
    }

    public static <B extends Block> boolean isPurpuredBlock(Class<B> blockClass) {
        return purpuredBlockClassMap.containsValue(blockClass);
    }

    public static <B extends Block> B createPurpuredBlock(B originalBlock, BlockBehaviour.Properties properties) {
        Class<B> originalBlockClass = (Class<B>) originalBlock.getClass();
        try {
            return generatePurpuredBlockClass(originalBlockClass)
                    .getConstructor(originalBlockClass, BlockBehaviour.Properties.class)
                    .newInstance(originalBlock, properties);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
