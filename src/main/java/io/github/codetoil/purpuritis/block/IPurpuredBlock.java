package io.github.codetoil.purpuritis.block;

import net.minecraft.block.Block;
import net.minecraft.util.IItemProvider;

public interface IPurpuredBlock extends IItemProvider
{
	Block getBlockToCopy();
	Block getPurpuredBlock();
}
