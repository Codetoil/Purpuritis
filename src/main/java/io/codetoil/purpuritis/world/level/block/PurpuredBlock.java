package io.codetoil.purpuritis.world.level.block;


import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PurpuredBlock extends Block implements IPurpuredBlock
{
	private final Block blockToPurpur;

	public PurpuredBlock(Block blockToPurpur)
	{
		super(Block.Properties.of().mapColor(MapColor.COLOR_PINK));
		this.blockToPurpur = blockToPurpur;
	}

	public PurpuredBlock(Block blockToPurpur, Block.Properties properties)
	{
		super(properties);
		this.blockToPurpur = blockToPurpur;
	}

    @Override
    public @NotNull Block getSelf() {
        return this;
    }

    public @NotNull Block getNormalBlock()
	{
		return this.blockToPurpur;
	}

	@Override
	public @NotNull Item asItem()
	{
		return Objects.requireNonNull(Purpuritis.purpuredItems.get(this.blockToPurpur.asItem())).getSelf();
	}
}
