package io.github.codetoil.purpuritis.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PurpuredItem extends Item implements IPurpuredItem
{
	public Item normalItem;

	public PurpuredItem(Item normalItem, Item.Properties properties)
	{
		super(properties);
		this.normalItem = normalItem;
		setRegistryName(new ResourceLocation("purpuritis", "purpured_" + normalItem.getRegistryName().getNamespace() + "_" + normalItem.getRegistryName().getPath()));
	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return normalItem.getItemStackLimit(new ItemStack(normalItem));
	}

	@Override
	public Item getItemToCopy()
	{
		return normalItem;
	}

	@Override
	public Item asItem()
	{
		return this;
	}

	@Override
	public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_)
	{
		normalItem.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count)
	{
		normalItem.onUsingTick(stack, player, count);
	}

	public void func_219972_a(World worldIn, LivingEntity livingEntityIn, ItemStack stack, int p_219972_4_) {
		normalItem.func_219972_a(worldIn, livingEntityIn, stack, p_219972_4_);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_)
	{
		return normalItem.onItemRightClick(p_77659_1_, p_77659_2_, p_77659_3_);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_)
	{
		return normalItem.onItemUseFinish(p_77654_1_, p_77654_2_, p_77654_3_);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
	{
		return normalItem.onItemUseFirst(stack, context);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		return normalItem.onEntityItemUpdate(stack, entity);
	}
}
