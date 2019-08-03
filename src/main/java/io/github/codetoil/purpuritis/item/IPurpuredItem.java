package io.github.codetoil.purpuritis.item;

import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;

public interface IPurpuredItem extends IItemProvider
{
	Item getItemToCopy();
}
