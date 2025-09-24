package io.codetoil.purpuritis.world.level.levelgen;

import com.mojang.serialization.MapCodec;
import io.codetoil.purpuritis.world.level.biome.CopyBiomeSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CopyChunkGenerator extends ChunkGenerator
{
	public Level otherLevel;

	public CopyChunkGenerator(Level otherLevel)
	{
        super(new CopyBiomeSource(otherLevel), (Holder<Biome> holder) -> {
            return null;
        });
		this.otherLevel = otherLevel;
	}

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return null;
    }

    @Override
    public void applyCarvers(WorldGenRegion p_223043_, long p_223044_, RandomState p_223045_, BiomeManager p_223046_, StructureManager p_223047_, ChunkAccess p_223048_) {

    }

    @Override
    public void buildSurface(WorldGenRegion p_223050_, StructureManager p_223051_, RandomState p_223052_, ChunkAccess p_223053_) {

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion p_62167_) {

    }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender p_223210_, RandomState p_223211_, StructureManager p_223212_, ChunkAccess p_223213_) {
        return null;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int p_223032_, int p_223033_, Heightmap.Types p_223034_, LevelHeightAccessor p_223035_, RandomState p_223036_) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int p_223028_, int p_223029_, LevelHeightAccessor p_223030_, RandomState p_223031_) {
        return null;
    }

    @Override
    public void addDebugScreenInfo(List<String> p_223175_, RandomState p_223176_, BlockPos p_223177_) {

    }
}
