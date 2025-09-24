package io.codetoil.purpuritis.block;

import io.codetoil.purpuritis.item.IPurpuredItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class PurpuredItemBlock extends BlockItem implements IPurpuredItem
{
	public Item normalItem;

	public PurpuredItemBlock(Item normalItem, IPurpuredBlock block, Item.Properties properties)
	{
		super(block.getPurpuredBlock(), properties);
		this.normalItem = normalItem;
		setRegistryName(new ResourceLocation("purpuritis", "purpured_" + normalItem.getRegistryName().getNamespace() + "_" + normalItem.getRegistryName().getPath()));
	}

	@Override
	public Item getItemToCopy()
	{
		return normalItem;
	}
}
