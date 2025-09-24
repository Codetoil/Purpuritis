package io.codetoil.purpuritis.world.item;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class PurpuredItem extends Item implements IPurpuredItem
{
	public Item normalItem;

	public PurpuredItem(Item normalItem, Item.Properties properties)
	{
		super(properties);
		this.normalItem = normalItem;
	}

    @Override
    public @NotNull Item getSelf() {
        return this;
    }

    public @NotNull Item getNormalItem() {
        return normalItem;
    }
}
