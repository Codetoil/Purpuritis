package io.codetoil.purpuritis.world.level.block;


import io.codetoil.purpuritis.world.item.IPurpuredItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class PurpuredBlockItem extends BlockItem implements IPurpuredItem
{
	public Item normalItem;

	public PurpuredBlockItem(Item normalItem, IPurpuredBlock purpuredBlock, Item.Properties properties)
	{
		super(purpuredBlock.getSelf(), properties);
		this.normalItem = normalItem;
	}

    @Override
    public @NotNull Item getSelf() {
        return this;
    }

    public @NotNull Item getNormalItem()
	{
		return normalItem;
	}
}
