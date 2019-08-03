package io.github.codetoil.purpuritis.item;

import io.github.codetoil.purpuritis.Purpuritis;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

public class PurpuredTier implements IItemTier
{
	public final IItemTier original;

	public PurpuredTier(IItemTier original)
	{
		this.original = original;
	}

	@Override
	public int getMaxUses()
	{
		return original.getMaxUses() * 2;
	}

	@Override
	public float getEfficiency()
	{
		return original.getEfficiency() * 2;
	}

	@Override
	public float getAttackDamage()
	{
		return original.getAttackDamage() + 4;
	}

	@Override
	public int getHarvestLevel()
	{
		return original.getHarvestLevel() + 4;
	}

	@Override
	public int getEnchantability()
	{
		return original.getEnchantability() / 5;
	}

	@Override
	public Ingredient getRepairMaterial()
	{
		return Ingredient.fromItems(Purpuritis.purpuredItems.get(original.getRepairMaterial().getMatchingStacks()[0].getItem()));
	}
}
