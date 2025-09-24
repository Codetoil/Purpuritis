package io.codetoil.purpuritis.data;

import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class DataProviderPurpuritis implements DataProvider
{
	public final DataGenerator gen;

	public DataProviderPurpuritis(DataGenerator gen)
	{
		this.gen = gen;
	}

	@Override
	public @NotNull CompletableFuture<?> run(CachedOutput cachedOutput)
	{
		Path path = gen.getPackOutput().getOutputFolder();
        String data = "LOGGER=" +
                Purpuritis.LOGGER +
                "\npurpuredItems=" +
                Purpuritis.purpuredItems +
                "\npurpuredBlocks=" +
                Purpuritis.purpuredBlocks +
                "\npurpuritis_overworld_loc=" +
                Purpuritis.purpuritis_overworld_loc +
                "\npurpuritis_overworld_biome_provider_loc=" +
                Purpuritis.purpuritis_overworld_biome_provider_loc +
                "\npurpuritis_overworld_chunk_generator_loc=" +
                Purpuritis.purpuritis_overworld_chunk_generator_loc +
                "\nbiomeConversionMap=" +
                Purpuritis.biomeConversionMap +
                "\nblockConversionMap=" +
                Purpuritis.blockConversionMap +
                "\ndimensionConversionMap=" +
                Purpuritis.dimensionConversionMap;
		Purpuritis.LOGGER.info("Finished gathering data");
        return CompletableFuture.completedFuture(data);
    }

	@Override
	public @NotNull String getName()
	{
		return "Purpuritis' Data Provider";
	}

	@Override
	public String toString()
	{
		return getName() + ":" + this.hashCode();
	}


}
