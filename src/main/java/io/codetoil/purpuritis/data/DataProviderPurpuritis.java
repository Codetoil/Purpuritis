package io.codetoil.purpuritis.data;

import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class DataProviderPurpuritis implements DataProvider
{
	public final DataGenerator gen;

	public DataProviderPurpuritis(DataGenerator gen)
	{
		this.gen = gen;
	}

	@Override
	public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput)
	{
        String data = "LOGGER=" +
                Purpuritis.LOGGER +
                "\npurpuredItems=" +
                Purpuritis.purpuredItems +
                "\npurpuredBlocks=" +
                Purpuritis.purpuredBlocks +
                "\npurpuredDimensionTypes=" +
                Purpuritis.purpuredDimensionTypes;
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
