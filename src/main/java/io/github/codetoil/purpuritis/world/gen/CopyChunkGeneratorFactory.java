package io.github.codetoil.purpuritis.world.gen;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.IChunkGeneratorFactory;

public class CopyChunkGeneratorFactory<C extends GenerationSettings> implements IChunkGeneratorFactory<C, CopyChunkGenerator<C>>
{
	public final BiMap<Block, Block> conversionMap;
	public DimensionType oldDimension;
	public IWorld worldToCopy;

	public CopyChunkGeneratorFactory(BiMap<Block, Block> conversionMap, DimensionType oldDimension)
	{
		this.conversionMap = conversionMap;
		this.oldDimension = oldDimension;
	}

	public CopyChunkGeneratorFactory(IWorld worldToCopy, BiMap<Block, Block> conversionMap)
	{
		this.worldToCopy = worldToCopy;
		this.conversionMap = conversionMap;
	}

	@Override
	public CopyChunkGenerator<C> create(World world, BiomeProvider biomeProvider, C generationSettings)
	{
		return new CopyChunkGenerator<C>(world, biomeProvider, generationSettings, worldToCopy, oldDimension, conversionMap);
	}
}
