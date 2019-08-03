package io.github.codetoil.purpuritis.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.OverworldGenSettings;

public class PurpuredOverworldGenSettings extends OverworldGenSettings
{
	private static PurpuredOverworldGenSettings MAIN = new PurpuredOverworldGenSettings();

	public static PurpuredOverworldGenSettings getMAIN()
	{
		return MAIN;
	}

	@Override
	public BlockState getDefaultBlock()
	{
		return Blocks.PURPUR_BLOCK.getDefaultState();
	}
}
