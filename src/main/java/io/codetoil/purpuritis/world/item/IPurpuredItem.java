package io.codetoil.purpuritis.world.item;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public interface IPurpuredItem {
    @NotNull Item getSelf();
    @NotNull Item getNormalItem();
}
