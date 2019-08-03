package io.github.codetoil.purpuritis.item;

import io.github.codetoil.purpuritis.Purpuritis;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;

public class PurpuredArmorMaterial implements IArmorMaterial
{
	public final IArmorMaterial original;

	public PurpuredArmorMaterial(IArmorMaterial original)
	{
		this.original = original;
	}

	@Override
	public int getDurability(EquipmentSlotType equipmentSlotType)
	{
		return original.getDurability(equipmentSlotType) * 2;
	}

	@Override
	public int getDamageReductionAmount(EquipmentSlotType equipmentSlotType)
	{
		return original.getDamageReductionAmount(equipmentSlotType) + 4;
	}

	@Override
	public int getEnchantability()
	{
		return original.getEnchantability() / 5;
	}

	@Override
	public SoundEvent getSoundEvent()
	{
		return original.getSoundEvent();
	}

	@Override
	public Ingredient getRepairMaterial()
	{
		return Ingredient.fromItems(Purpuritis.purpuredItems.get(original.getRepairMaterial().getMatchingStacks()[0].getItem()));
	}

	@Override
	public String getName()
	{
		return "purpuritis:purpured_minecraft_" + original.getName();
	}

	@Override
	public float getToughness()
	{
		return 2.0F + original.getToughness();
	}
}
