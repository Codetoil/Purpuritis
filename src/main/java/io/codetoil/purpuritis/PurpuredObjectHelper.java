package io.codetoil.purpuritis;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PurpuredObjectHelper {

    private static final PurpuritisDynamicClassLoader purpuritisDynamicClassLoader
            = new PurpuritisDynamicClassLoader(PurpuredObjectHelper.class.getClassLoader());


    // Items


    private static final BiMap<Class<? extends Item>, Class<? extends Item>> purpuredItemClassMap = HashBiMap.create();
    private static final Map<Class<? extends Item>, byte[]> purpuredItemClassByteArrayMap = Maps.newHashMap();
    private static final Map<Class<? extends Item>, Constructor<? extends Item>> purpuredItemClassSelectedConstructor
            = Maps.newHashMap();

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

        MethodVisitor constructorVisitor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "(" + originalItemClass.descriptorString() + ")V",
                null,
                null);

        Label startConstructorParameters = new Label();
        Label endConstructorParameters = new Label();
        constructorVisitor.visitLocalVariable("constructorParameters", Object[].class.descriptorString(),
                null, startConstructorParameters, endConstructorParameters, 2);

        constructorVisitor.visitCode();
        constructorVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        constructorVisitor.visitLdcInsn(Type.getType(originalItemClass));
        constructorVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        constructorVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                "io/codetoil/purpuritis/PurpuredObjectHelper",
                "createItemConstructorParameters",
                "(" + Class.class.descriptorString() + Item.class.descriptorString() + ")["
                        + Object.class.descriptorString(),
                false);
        constructorVisitor.visitLabel(startConstructorParameters);
        constructorVisitor.visitVarInsn(Opcodes.ASTORE, 2);
        for (int index = 0; index < getSelectedConstructor(originalItemClass).getParameterCount(); index++) {
            constructorVisitor.visitVarInsn(Opcodes.ALOAD, 2);
            constructorVisitor.visitIntInsn(Opcodes.SIPUSH, index); // Limited to Short.MAX_VALUE parameters.
            constructorVisitor.visitInsn(Opcodes.AALOAD);
            constructorVisitor.visitTypeInsn(Opcodes.CHECKCAST, getSelectedConstructor(originalItemClass)
                    .getParameterTypes()[index].getName().replace('.', '/'));
            switch (getSelectedConstructor(originalItemClass).getParameterTypes()[index].getName()) {
                case "io.codetoil.purpuritis.PurpuredObjectHelper$PurpuritisByteWrapper":
                    constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "io/codetoil/purpuritis/PurpuredObjectHelper$PurpuritisByteWrapper",
                            "getValue",
                            "()B",
                            false);
                case "io.codetoil.purpuritis.PurpuredObjectHelper$PurpuritisShortWrapper":
                    constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "io/codetoil/purpuritis/PurpuredObjectHelper$PurpuritisShortWrapper",
                            "getValue",
                            "()S",
                            false);
                case "io.codetoil.purpuritis.PurpuredObjectHelper$PurpuritisIntWrapper":
                    constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "io/codetoil/purpuritis/PurpuredObjectHelper$PurpuritisIntWrapper",
                            "getValue",
                            "()I",
                            false);
                case "io.codetoil.purpuritis.PurpuredObjectHelper$PurpuritisLongWrapper":
                    constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "io/codetoil/purpuritis/PurpuredObjectHelper$PurpuritisLongWrapper",
                            "getValue",
                            "()J",
                            false);
                case "io.codetoil.purpuritis.PurpuredObjectHelper$PurpuritisCharWrapper":
                    constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "io/codetoil/purpuritis/PurpuredObjectHelper$PurpuritisCharWrapper",
                            "getValue",
                            "()C",
                            false);
                case "io.codetoil.purpuritis.PurpuredObjectHelper$PurpuritisFloatWrapper":
                    constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "io/codetoil/purpuritis/PurpuredObjectHelper$PurpuritisFloatWrapper",
                            "getValue",
                            "()F",
                            false);
                case "io.codetoil.purpuritis.PurpuredObjectHelper$PurpuritisDoubleWrapper":
                    constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "io/codetoil/purpuritis/PurpuredObjectHelper$PurpuritisDoubleWrapper",
                            "getValue",
                            "()D",
                            false);
                case "io.codetoil.purpuritis.PurpuredObjectHelper$PurpuritisBooleanWrapper":
                    constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            "io/codetoil/purpuritis/PurpuredObjectHelper$PurpuritisBooleanWrapper",
                            "getValue",
                            "()Z",
                            false);
            }
        }
        constructorVisitor.visitLabel(endConstructorParameters);

        constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                originalItemClass.getName().replace('.', '/'),
                "<init>",
                "(" +
                        Arrays.stream(getSelectedConstructor(originalItemClass).getParameterTypes())
                                .map(Class::descriptorString).collect(Collectors.joining()) + ")V",
                false);

        constructorVisitor.visitInsn(Opcodes.RETURN);
        constructorVisitor.visitMaxs(1, 1);

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

        String purpuredItemClassName = "purpuritis_dynamic.item."
                + originalItemClass.getPackageName() + ".Purpured" + originalItemClass.getSimpleName();

        Class<I> purpuredItemClass;
        try {
            purpuredItemClass = (Class<I>) purpuritisDynamicClassLoader.loadClass(purpuredItemClassName);
        } catch (ClassNotFoundException e) {
            purpuredItemClass = (Class<I>) purpuritisDynamicClassLoader.defineClass(purpuredItemClassName,
                    getPurpuredItemClassByteArray(originalItemClass));
        }

        purpuredItemClassMap.put(originalItemClass, purpuredItemClass);

        return purpuredItemClass;
    }

    @SuppressWarnings("unused") // Used by Generated Classes
    public static Object[] createItemConstructorParameters(Class<? extends Item> originalItemClass,
                                                                            Item originalItem) {
        Constructor<? extends Item> constructor = getSelectedConstructor(originalItemClass);
        Object[] result = new Object[constructor.getParameterCount()];
        for (int index = 0; index < result.length; index++) {
            if (constructor.getParameterTypes()[index] == Block.class) {
                if (originalItem instanceof BlockItem)
                {
                    result[index] = Purpuritis.purpuredBlocks.get(((BlockItem) originalItem).getBlock());
                } else if (originalItem instanceof AirItem)
                {
                    result[index] = Blocks.AIR;
                } else {
                    result[index] = null;
                }
            } else if (constructor.getParameterTypes()[index] == Item.Properties.class) {
                ResourceKey<Item> key = ResourceKey.create(ForgeRegistries.ITEMS.getRegistryKey(),
                        ForgeRegistries.ITEMS.getKey(originalItem));
                result[index] = new Item.Properties().setId(key); // TODO implement custom Item properties
            } else if (constructor.getParameterTypes()[index] == byte.class) {
                result[index] = new PurpuritisByteWrapper((byte) 0);
            } else if (constructor.getParameterTypes()[index] == short.class) {
                result[index] = new PurpuritisShortWrapper((short) 0);
            } else if (constructor.getParameterTypes()[index] == int.class) {
                result[index] = new PurpuritisIntWrapper(0);
            } else if (constructor.getParameterTypes()[index] == long.class) {
                result[index] = new PurpuritisLongWrapper(0L);
            } else if (constructor.getParameterTypes()[index] == char.class) {
                result[index] = new PurpuritisCharWrapper((char) 0);
            } else if (constructor.getParameterTypes()[index] == float.class) {
                result[index] = new PurpuritisFloatWrapper(0.0f);
            } else if (constructor.getParameterTypes()[index] == double.class) {
                result[index] = new PurpuritisDoubleWrapper(0.0);
            } else if (constructor.getParameterTypes()[index] == boolean.class) {
                result[index] = new PurpuritisBooleanWrapper(false);
            } else {
                result[index] = null;
            }
        }
        return result;
    }

    public static <I extends Item> Constructor<I> getSelectedConstructor(Class<I> originalItemClass) {
        if (purpuredItemClassSelectedConstructor.containsKey(originalItemClass)) {
            return (Constructor<I>) purpuredItemClassSelectedConstructor.get(originalItemClass);
        }

        return selectConstructor(originalItemClass);
    }

    private static <I extends Item> Constructor<I> selectConstructor(Class<I> originalItemClass) {
        Constructor<I>[] constructors = (Constructor<I>[]) originalItemClass.getConstructors();
        if (constructors.length == 0)
            throw new IllegalArgumentException("Class " + originalItemClass + " does not contain any constructors " +
                    "(should not be possible, is someone messing with bytecode?)");
        int sizeOfMinimumSizedConstructor = Integer.MAX_VALUE;
        int indexOfMinimumSizedConstructor = 0;
        for (int index = 0; index < constructors.length; index++) {
            Constructor<I> constructor = constructors[index];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0] == Item.Properties.class) {
                return constructor;
            }
            if (parameterTypes.length == 2 && parameterTypes[0] == Block.class
                    && parameterTypes[1] == Item.Properties.class) {
                return constructor;
            }
            if (constructor.getParameterCount() < sizeOfMinimumSizedConstructor) {
                indexOfMinimumSizedConstructor = index;
                sizeOfMinimumSizedConstructor = constructor.getParameterCount();
            }
        }

        Constructor<I> constructor = constructors[indexOfMinimumSizedConstructor];
        purpuredItemClassSelectedConstructor.put(originalItemClass, constructor);
        return constructor;
    }

    public static <I extends Item> boolean isPurpuredItem(Class<I> itemClass) {
        return purpuredItemClassMap.containsValue(itemClass);
    }

    public static <I extends Item> I createPurpuredItem(I originalItem) {
        Class<I> originalItemClass = (Class<I>) originalItem.getClass();
        try {
            return getPurpuredItemClass(originalItemClass).getConstructor(originalItemClass).newInstance(originalItem);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class PurpuritisDynamicClassLoader extends ClassLoader {
        public PurpuritisDynamicClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    @ApiStatus.Internal
    private static final class PurpuritisByteWrapper {
        private final byte value;
        public PurpuritisByteWrapper(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    @ApiStatus.Internal
    private static final class PurpuritisShortWrapper {
        private final short value;
        public PurpuritisShortWrapper(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }
    }

    @ApiStatus.Internal
    private static final class PurpuritisIntWrapper {
        private final int value;
        public PurpuritisIntWrapper(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @ApiStatus.Internal
    private static final class PurpuritisLongWrapper {
        private final long value;
        public PurpuritisLongWrapper(long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }
    }

    @ApiStatus.Internal
    private static final class PurpuritisCharWrapper {
        private final char value;
        public PurpuritisCharWrapper(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }
    }

    @ApiStatus.Internal
    private static final class PurpuritisFloatWrapper {
        private final float value;
        public PurpuritisFloatWrapper(float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }
    }

    @ApiStatus.Internal
    private static final class PurpuritisDoubleWrapper {
        private final double value;
        public PurpuritisDoubleWrapper(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    @ApiStatus.Internal
    private static final class PurpuritisBooleanWrapper {
        private final boolean value;
        public PurpuritisBooleanWrapper(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }
    }
}
