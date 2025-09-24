package io.codetoil.purpuritis.world.biome;

import net.minecraft.world.biome.Biome;

public class PurpuredBiome extends Biome
{
	public final Biome biomeToPurpur;

	public PurpuredBiome(Biome biomeToPurpur, Biome.Builder builder)
	{
		super(builder);
		this.biomeToPurpur = biomeToPurpur;
	}
}
