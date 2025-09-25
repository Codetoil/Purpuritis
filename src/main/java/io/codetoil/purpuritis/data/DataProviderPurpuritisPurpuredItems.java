package io.codetoil.purpuritis.data;

import com.google.common.collect.Maps;
import io.codetoil.purpuritis.PurpuredObjectHelper;
import io.codetoil.purpuritis.Purpuritis;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DataProviderPurpuritisPurpuredItems implements DataProvider
{
	public final DataGenerator gen;

	public DataProviderPurpuritisPurpuredItems(DataGenerator gen)
	{
		this.gen = gen;
	}

	@Override
	public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput)
	{
        Purpuritis.LOGGER.info("Starting the generation of item classes");
        var data = Maps.transformValues(
                Maps.transformValues(
                        Maps.filterValues(Purpuritis.purpuredItems, Objects::nonNull),
                        Item::getClass),
                PurpuredObjectHelper::getPurpuredItemClassByteArray);
		Purpuritis.LOGGER.info("Finished generating item classes");
        return CompletableFuture.completedFuture(data);
    }

	@Override
	public @NotNull String getName()
	{
		return "Purpuritis' Data Provider for Item Classes";
	}

	@Override
	public String toString()
	{
		return getName() + ":" + this.hashCode();
	}


}
