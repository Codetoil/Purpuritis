package io.codetoil.purpuritis.world.level.block;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public interface IPurpuredBlock {
    @NotNull Block getSelf();
    @NotNull Block getNormalBlock();
}
