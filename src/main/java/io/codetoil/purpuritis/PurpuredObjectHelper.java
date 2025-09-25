package io.codetoil.purpuritis;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class PurpuredObjectHelper {

    private static final PurpuritisDynamicClassLoader purpuritisDynamicClassLoader
            = new PurpuritisDynamicClassLoader();

    // Items

    private static final BiMap<Class<? extends Item>, Class<? extends Item>> purpuredItemClassMap
            = HashBiMap.create();
    private static final Map<Class<? extends Item>, byte[]> purpuredItemClassByteArrayMap = Maps.newHashMap();

    public static <I extends Item> byte[] getPurpuredItemClassByteArray(Class<I> originalItemClass) {
        if (purpuredItemClassByteArrayMap.containsKey(originalItemClass)) {
            return purpuredItemClassByteArrayMap.get(originalItemClass);
        }
        return generatePurpuredItemClassByteArray(originalItemClass);
    }

    private static <I extends Item> byte[] generatePurpuredItemClassByteArray(Class<I> originalItemClass) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        classWriter.visit(
                Opcodes.V21,
                Opcodes.ACC_PUBLIC,
                "purpuritis_dynamic/item/"
                        + originalItemClass.getPackageName().replace('.', '/') + "/Purpured"
                        + originalItemClass.getSimpleName(),
                null,
                originalItemClass.getName().replace('.', '/'),
                new String[] {});

        MethodVisitor constructor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "(" + originalItemClass.descriptorString()
                        + Item.Properties.class.descriptorString() + ")V",
                null,
                null);

        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitLdcInsn(Type.getType(originalItemClass));
        constructor.visitMethodInsn(Opcodes.INVOKESTATIC,
                "io/codetoil/purpuritis/PurpuredObjectHelper",
                "createItemProperties",
                "(" + Class.class.descriptorString() + ")" + Item.Properties.class.descriptorString(),
                false);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                originalItemClass.getName().replace('.', '/'),
                "<init>",
                "(" + Item.Properties.class.descriptorString() + ")V",
                false);

        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(1, 1);

        classWriter.visitEnd();
        byte[] result = classWriter.toByteArray();
        purpuredItemClassByteArrayMap.put(originalItemClass, result);
        return result;
    }

    public static @NotNull <I extends Item> Class<I> getPurpuredItemClass(Class<I> originalItemClass)
            throws IllegalAccessException {
        if (purpuredItemClassMap.containsKey(originalItemClass)) {
            return (Class<I>) purpuredItemClassMap.get(originalItemClass);
        }

        var byteArray = getPurpuredItemClassByteArray(originalItemClass);

        return (Class<I>) purpuritisDynamicClassLoader.defineClass("purpuritis_dynamic.item."
                + originalItemClass.getPackageName() + ".Purpured" + originalItemClass.getSimpleName(),
                byteArray);
    }

    @SuppressWarnings("unused") // Used by Generated Classes
    public static <I extends Item> Item.Properties createItemProperties(Class<I> originalItemClass) {
        return new Item.Properties();
    }

    public static <I extends Item> boolean isPurpuredItem(Class<I> itemClass) {
        return purpuredItemClassMap.containsValue(itemClass);
    }

    public static <I extends Item> I createPurpuredItem(I originalItem,
                                                        Item.Properties properties) {
        Class<I> originalItemClass = (Class<I>) originalItem.getClass();
        try {
            return getPurpuredItemClass(originalItemClass).getConstructor(originalItemClass, Item.Properties.class)
                    .newInstance(originalItem, properties);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    // Blocks




    private static final BiMap<Class<? extends Block>, Class<? extends Block>> purpuredBlockClassMap
            = HashBiMap.create();

    public static @NotNull <B extends Block> Class<B> getPurpuredBlockClass(Class<B> originalBlockClass)
            throws IllegalAccessException {
        if (purpuredBlockClassMap.containsKey(originalBlockClass)) {
            return (Class<B>) purpuredBlockClassMap.get(originalBlockClass);
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        classWriter.visit(
                Opcodes.V21,
                Opcodes.ACC_PUBLIC,
                "purpuritis_dynamic/block/"
                        + originalBlockClass.getPackageName().replace('.', '/') + "/Purpured"
                        + originalBlockClass.getSimpleName(),
                null,
                originalBlockClass.getName().replace('.', '/'),
                new String[] {});

        classWriter.visitEnd();

        return (Class<B>) purpuritisDynamicClassLoader.defineClass("purpuritis_dynamic.block."
                        + originalBlockClass.getPackageName() + ".Purpured" + originalBlockClass.getSimpleName(),
                classWriter.toByteArray());
    }

    public static <B extends Block> boolean isPurpuredBlock(Class<B> blockClass) {
        return purpuredBlockClassMap.containsValue(blockClass);
    }

    public static <B extends Block> B createPurpuredBlock(B originalBlock, BlockBehaviour.Properties properties) {
        Class<B> originalBlockClass = (Class<B>) originalBlock.getClass();
        try {
            return getPurpuredBlockClass(originalBlockClass)
                    .getConstructor(originalBlockClass, BlockBehaviour.Properties.class)
                    .newInstance(originalBlock, properties);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused") // Used by Generated Classes
    public static <B extends Block> BlockBehaviour.Properties createBlockBehaviorProperties(Class<B> originalBlockCLass) {
        return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK);
    }

    private static final class PurpuritisDynamicClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
