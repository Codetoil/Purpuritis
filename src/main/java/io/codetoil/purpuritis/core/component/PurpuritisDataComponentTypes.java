package io.codetoil.purpuritis.core.component;

import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Unit;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PurpuritisDataComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_DEFERRED_REGISTER =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Purpuritis.MOD_ID);

    public static final RegistryObject<DataComponentType<Unit>> PURPURED = DATA_COMPONENT_TYPE_DEFERRED_REGISTER
            .register("purpured", () -> DataComponentType.<Unit>builder()
                    .persistent(Unit.CODEC)
                    .networkSynchronized(Unit.STREAM_CODEC)
                    .build());
}
