package io.codetoil.purpuritis.world.level.biome;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class CopyBiomeSource extends BiomeSource {
    public Level otherLevel;

    public CopyBiomeSource(Level otherLevel)
    {
        super();
        this.otherLevel = otherLevel;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return null;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.empty();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int p_204238_, int p_204239_, int p_204240_, Climate.Sampler p_204241_) {
        return null;
    }
}
