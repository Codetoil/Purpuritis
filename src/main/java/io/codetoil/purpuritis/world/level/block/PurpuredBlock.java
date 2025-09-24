package io.codetoil.purpuritis.world.level.block;


import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PurpuredBlock extends Block implements IPurpuredBlock
{
	private final Block normalBlock;

	public PurpuredBlock(Block normalBlock, Block.Properties properties)
	{
		super(properties);
		this.normalBlock = normalBlock;
	}

    @Override
    public @NotNull Block getSelf() {
        return this;
    }

    public @NotNull Block getNormalBlock()
	{
		return this.normalBlock;
	}

	@Override
	public @NotNull Item asItem()
	{
		return Objects.requireNonNull(Purpuritis.purpuredItems.get(this.normalBlock.asItem())).getSelf();
	}
}
