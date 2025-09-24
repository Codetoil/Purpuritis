package io.codetoil.purpuritis.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class PurpuredBlock extends Block implements IPurpuredBlock
{
	private Block blockToPurpur;

	public PurpuredBlock(Block blockToPurpur)
	{
		super(Block.Properties.create(Material.ROCK, MaterialColor.PINK).hardnessAndResistance(blockToPurpur.getBlockHardness(null, null, null) + Blocks.PURPUR_BLOCK.getBlockHardness(null, null, null), blockToPurpur.getExplosionResistance() + Blocks.PURPUR_BLOCK.getBlockHardness(null, null, null)));
		this.blockToPurpur = blockToPurpur;
		setRegistryName("purpuritis", "purpured_" + blockToPurpur.getRegistryName().getNamespace() + "_" + blockToPurpur.getRegistryName().getPath());
	}

	public PurpuredBlock(Block blockToPurpur, Block.Properties properties)
	{
		super(properties);
		this.blockToPurpur = blockToPurpur;
		setRegistryName("purpuritis", "purpured_" + blockToPurpur.getRegistryName().getNamespace() + "_" + blockToPurpur.getRegistryName().getPath());
	}

	public Block getBlock()
	{
		return blockToPurpur;
	}

	@Override
	public float getBlockHardness(BlockState p_176195_1_, IBlockReader p_176195_2_, BlockPos p_176195_3_)
	{
		return Blocks.PURPUR_BLOCK.getBlockHardness(p_176195_1_, p_176195_2_, p_176195_3_) + blockToPurpur.getBlockHardness(p_176195_1_, p_176195_2_, p_176195_3_);
	}

	@Override
	public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_)
	{
		return new ItemStack(ForgeRegistries.ITEMS.getValue(this.getRegistryName()));
	}

	@Nullable
	@Override
	public ToolType getHarvestTool(BlockState p_getHarvestTool_1_)
	{
		return ToolType.PICKAXE;
	}

	@Override
	public Item asItem()
	{
		return ForgeRegistries.ITEMS.getValue(this.getRegistryName());
	}

	@Override
	public Block getBlockToCopy()
	{
		return blockToPurpur;
	}

	@Override
	public Block getPurpuredBlock()
	{
		return this;
	}
}
