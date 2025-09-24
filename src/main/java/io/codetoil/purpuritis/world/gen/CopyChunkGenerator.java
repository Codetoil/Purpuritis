package io.codetoil.purpuritis.world.gen;

import com.google.common.collect.BiMap;
import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CopyChunkGenerator<C extends GenerationSettings> extends ChunkGenerator<C>
{
	public final BiMap<Block, Block> convertionMap;
	public DimensionType oldDimension;
	public IWorld worldToCopy;

	public CopyChunkGenerator(IWorld world, BiomeProvider provider, C settings, IWorld worldToCopy, DimensionType oldDimension, BiMap<Block, Block> convertionMap)
	{
		super(world, provider, settings);
		this.oldDimension = oldDimension;
		this.worldToCopy = worldToCopy;
		validateWorldToCopy();
		this.convertionMap = convertionMap;
	}

	public boolean validateWorldToCopy()
	{
		if (worldToCopy != null && !(worldToCopy.getChunkProvider() instanceof ClientChunkProvider))
			return true;
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			worldToCopy = server.getWorld(oldDimension);
			return !(worldToCopy.getChunkProvider() instanceof ClientChunkProvider);
		} else {
			return false;
		}
	}

	@Override
	public void generateSurface(IChunk iChunk)
	{
	}

	@Override
	public int getGroundHeight()
	{
		if (!validateWorldToCopy()) return 0;
		return worldToCopy.getChunkProvider().getChunkGenerator().getGroundHeight();
	}

	@Override
	public void makeBase(IWorld iWorld, IChunk iChunk)
	{

	}

	@Override
	public int func_222529_a(int i, int i1, Heightmap.Type type)
	{
		return 0;
	}

	@Nullable
	public IChunk generateChunk(IChunk iChunk, IChunk oldChunk)
	{
		if (!validateWorldToCopy()) return null;
		//Purpuritis.LOGGER.debug("Starting generateChunk");
		long startTime = System.currentTimeMillis();
		//Purpuritis.LOGGER.info("Getting Chunk");
		if (oldChunk == null) {
			Purpuritis.LOGGER.debug("old chunk is null!");
			Purpuritis.LOGGER.debug("generateSurface took {} ms", System.currentTimeMillis() - startTime);
			return null;
		}
		if (iChunk == null)
		{
			iChunk = new ChunkPrimer(oldChunk.getPos(), UpgradeData.EMPTY);
			world.getChunkProvider().getChunkGenerator().generateBiomes(iChunk);
			if (!(world instanceof World))
			{
				Purpuritis.LOGGER.error("world {} is not of type the following class represents {}, and such we can not generate the new chunk. Skipping!", world, World.class);
				return null;
			}
			iChunk = new Chunk((World) world, (ChunkPrimer) iChunk);
		}
		//Purpuritis.LOGGER.info("Creating Stream");
		Iterator<BlockPos> blocks = BlockPos.getAllInBoxMutable(oldChunk.getPos().getXStart(), 0, oldChunk.getPos().getZStart(), oldChunk.getPos().getXEnd(), 255, oldChunk.getPos().getZEnd()).iterator();
		//Purpuritis.LOGGER.info("Preforming Operation");
		int i;
		while (blocks.hasNext())
		{
			BlockPos pos = blocks.next();

			BlockState state = oldChunk.getBlockState(pos);
			//	Purpuritis.LOGGER.info("State: " + state);
			Block blockOldWorld = state.getBlock();
			//Purpuritis.LOGGER.info("BlockOld: " + blockOldWorld);
			Block newBlock = convertBlock(blockOldWorld);
			if (!newBlock.equals(Blocks.AIR))
			{
				//Purpuritis.LOGGER.info("Found not air block of {} at {} with a state of {}, converting to {}", blockOldWorld, pos, state, newBlock);
			}
			//Purpuritis.LOGGER.info("blockNew: " + newBlock);
			BlockState newState = newBlock.getDefaultState();

			iChunk.setBlockState(pos, newState, false);
		}
		//	Purpuritis.LOGGER.info("Finished!");
		Purpuritis.LOGGER.debug("generateChunk took {} ms", System.currentTimeMillis() - startTime);
		((Chunk) iChunk).setLoaded(true);
		return iChunk;
	}

	public Block convertBlock(Block oldBlock)
	{
		if (!validateWorldToCopy()) return oldBlock;
		Block newBlock;
		if (convertionMap.containsKey(oldBlock)) {
			newBlock = convertionMap.get(oldBlock);
		} else if (convertionMap.containsValue(oldBlock)) {
			newBlock = convertionMap.inverse().get(oldBlock);
		} else {
			newBlock = oldBlock;
		}
		return newBlock;
	}
}
