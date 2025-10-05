package io.codetoil.purpuritis.client.render.item.properties.conditional;

import io.codetoil.purpuritis.core.component.PurpuritisDataComponentTypes;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;

import java.util.function.Supplier;

public class PurpuritisConditionalItemModelProperties {
    public static final Supplier<ConditionalItemModelProperty> IS_PURPURED =
            () -> new HasComponent(PurpuritisDataComponentTypes.PURPURED.get(), false);
}
