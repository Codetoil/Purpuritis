package io.github.codetoil.purpuritis.world.biome.provider;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.codetoil.purpuritis.Purpuritis;
import io.github.codetoil.purpuritis.world.gen.CopyChunkGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CopyBiomeProvider extends BiomeProvider
{
	public final BiMap<Biome, Biome> convertionMap;
	public DimensionType oldDimension;
	public IWorld worldToCopy;
	public CopyChunkGenerator<?> thisChunkGenorator;

	public CopyBiomeProvider(BiMap<Biome, Biome> convertionMap, DimensionType oldDimension)
	{
		this(null, convertionMap, oldDimension);
	}

	public CopyBiomeProvider(IWorld worldToCopy, BiMap<Biome, Biome> convertionMap, DimensionType oldDimension)
	{
		this.oldDimension = oldDimension;
		this.worldToCopy = worldToCopy;
		this.convertionMap = convertionMap;
	}

	@Override
	public Biome getBiome(int x, int z)
	{
		Purpuritis.LOGGER.info("Starting getBiome");
		long startTime = System.currentTimeMillis();
		validateWorldToCopy();
		BiomeProvider oldBiomeProvider = worldToCopy.getChunkProvider().getChunkGenerator().getBiomeProvider();
		Biome oldBiome = oldBiomeProvider.getBiome(new BlockPos(x, 0, z));
		Purpuritis.LOGGER.info("getBiomes took " + (System.currentTimeMillis() - startTime) + "ms");
		return convertBiome(oldBiome);
	}

	public boolean validateWorldToCopy()
	{
		if (worldToCopy != null)
			return true;
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			worldToCopy = server.getWorld(oldDimension);
			return true;
		} else {
			return false;
		}
	}

	public Biome convertBiome(Biome oldBiome)
	{
		Biome newBiome;
		if (convertionMap.containsKey(oldBiome)) {
			newBiome = convertionMap.get(oldBiome);
		} else if (convertionMap.containsValue(oldBiome)) {
			newBiome = convertionMap.inverse().get(oldBiome);
		} else {
			newBiome = oldBiome;
		}
		return newBiome;
	}

	@Override
	public Biome[] getBiomes(int i, int i1, int i2, int i3, boolean b)
	{
		validateWorldToCopy();
		//Purpuritis.LOGGER.info("Starting getBiomes");
		//long startTime = System.currentTimeMillis();
		BiomeProvider oldBiomeProvider = worldToCopy.getChunkProvider().getChunkGenerator().getBiomeProvider();
		Biome[] biomesOld = oldBiomeProvider.getBiomes(i, i1, i2, i3, b);
		Biome[] biomesNew = new Biome[biomesOld.length];
		for (int j = 0; j < biomesOld.length; j++) {
			biomesNew[j] = convertBiome(biomesOld[j]);
		}
		//Purpuritis.LOGGER.info("getBiomes took " + (System.currentTimeMillis() - startTime) + "ms");
		return biomesNew;
	}

	@Override
	public Set<Biome> getBiomesInSquare(int i, int i1, int i2)
	{
		Purpuritis.LOGGER.info("Starting getBiomesInSquare");
		long startTime = System.currentTimeMillis();
		validateWorldToCopy();
		BiomeProvider oldBiomeProvider = worldToCopy.getChunkProvider().getChunkGenerator().getBiomeProvider();
		Set<Biome> biomesOld = oldBiomeProvider.getBiomesInSquare(i, i1, i2);
		Set<Biome> biomesNew = Sets.newHashSet();
		biomesOld.forEach(biome -> {
			biomesNew.add(convertBiome(biome));
		});
		Purpuritis.LOGGER.info("getBiomesInSquare took " + (System.currentTimeMillis() - startTime) + "ms");
		return biomesNew;
	}

	@Nullable
	@Override
	public BlockPos findBiomePosition(int i, int i1, int i2, List<Biome> oldList, Random random)
	{
		Purpuritis.LOGGER.info("Starting findBiomePosition");
		long startTime = System.currentTimeMillis();
		validateWorldToCopy();
		BiomeProvider oldBiomeProvider = worldToCopy.getChunkProvider().getChunkGenerator().getBiomeProvider();
		List<Biome> newList = Lists.newArrayList();
		oldList.forEach(biome -> {
			newList.add(convertBiome(biome));
		});
		Purpuritis.LOGGER.info("findBiomePosition took " + (System.currentTimeMillis() - startTime) + "ms");
		return oldBiomeProvider.findBiomePosition(i, i1, i2, newList, random);
	}

	@Override
	public boolean hasStructure(Structure<?> structure)
	{
		return false;
	}

	@Override
	public Set<BlockState> getSurfaceBlocks()
	{
		Purpuritis.LOGGER.info("Starting getSurfaceBlocks");
		long startTime = System.currentTimeMillis();
		validateWorldToCopy();
		BiomeProvider oldBiomeProvider = worldToCopy.getChunkProvider().getChunkGenerator().getBiomeProvider();
		Set<BlockState> oldBlockStates = oldBiomeProvider.getSurfaceBlocks();
		Set<BlockState> newBlockStates = Sets.newHashSet();
		oldBlockStates.forEach(blockState -> {
			newBlockStates.add(thisChunkGenorator.convertBlock(blockState.getBlock()).getDefaultState());
		});
		Purpuritis.LOGGER.info("getSurfaceBlocks took " + (System.currentTimeMillis() - startTime) + "ms");
		return newBlockStates;
	}
}
