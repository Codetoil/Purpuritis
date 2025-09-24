package io.codetoil.purpuritis.world.dimension;


import io.codetoil.purpuritis.Purpuritis;
import io.codetoil.purpuritis.world.biome.provider.CopyBiomeProvider;
import io.codetoil.purpuritis.world.gen.CopyChunkGenerator;
import io.codetoil.purpuritis.world.gen.PurpuredOverworldGenSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;

import javax.annotation.Nullable;

public class DimensionPurpuredOverworld extends Dimension
{
	public DimensionPurpuredOverworld(World worldIn, DimensionType typeIn)
	{
		super(worldIn, typeIn);
	}

	@Override
	@SuppressWarnings("deprecation")
	public ChunkGenerator<?> createChunkGenerator()
	{
		CopyBiomeProvider biomeProvider;
		CopyChunkGenerator<PurpuredOverworldGenSettings> chunkGenerator = new CopyChunkGenerator<>(this.world, biomeProvider = new CopyBiomeProvider(null, Purpuritis.biomeConversionMap, DimensionType.OVERWORLD), PurpuredOverworldGenSettings.getMAIN(), null, DimensionType.OVERWORLD, Purpuritis.blockConversionMap);
		biomeProvider.thisChunkGenorator = chunkGenerator;
		return chunkGenerator;
	}

	@Nullable
	@Override
	public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid)
	{
		return null;
	}

	@Nullable
	@Override
	public BlockPos findSpawn(int posX, int posZ, boolean checkValid)
	{
		return null;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks)
	{
		return 0;
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return true;
	}

	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks)
	{
		float f = MathHelper.cos(celestialAngle * ((float) Math.PI * 2F)) * 2.0F + 0.5F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		float f1 = 0.66666666F;
		float f2 = 0.47843137F;
		float f3 = 0.66666666F;
		f1 = f1 * (f * 0.94F + 0.06F);
		f2 = f2 * (f * 0.94F + 0.06F);
		f3 = f3 * (f * 0.91F + 0.09F);
		return new Vec3d((double) f1, (double) f2, (double) f3);
	}

	@Override
	public boolean canRespawnHere()
	{
		return true;
	}

	@Override
	public boolean doesXZShowFog(int x, int z)
	{
		return true;
	}

	@Override
	public Vec3d getSkyColor(BlockPos cameraPos, float partialTicks)
	{
		float f1 = 0.66666666F;
		float f2 = 0.47843137F;
		float f3 = 0.66666666F;
		return new Vec3d(f1, f2, f3);
	}

	@Override
	public Vec3d getCloudColor(float partialTicks)
	{
		float f1 = 0.75294117F;
		float f2 = 0.61960794F;
		float f3 = 0.75294117F;
		return new Vec3d(f1, f2, f3);
	}
}
