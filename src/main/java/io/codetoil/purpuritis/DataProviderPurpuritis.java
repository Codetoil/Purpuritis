package io.codetoil.purpuritis;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class DataProviderPurpuritis implements IDataProvider
{
	public final DataGenerator gen;

	public DataProviderPurpuritis(DataGenerator gen)
	{
		this.gen = gen;
	}

	@Override
	public void act(DirectoryCache directoryCache) throws IOException
	{
		Path path = gen.getOutputFolder();
		StringBuilder builder = new StringBuilder();
		builder.append("LOGGER=");
		builder.append(Purpuritis.LOGGER);
		builder.append("\npurpuredItems=");
		builder.append(Purpuritis.purpuredItems);
		builder.append("\npurpuredBlocks=");
		builder.append(Purpuritis.purpuredBlocks);
		builder.append("\npurpuritis_overworld=");
		builder.append(Purpuritis.purpuritis_overworld);
		builder.append("\npurpuritis_overworld_loc=");
		builder.append(Purpuritis.purpuritis_overworld_loc);
		builder.append("\npurpuritis_overworld_biome_provider_loc=");
		builder.append(Purpuritis.purpuritis_overworld_biome_provider_loc);
		builder.append("\npurpuritis_overworld_chunk_generator_loc=");
		builder.append(Purpuritis.purpuritis_overworld_chunk_generator_loc);
		builder.append("\npurpuritis_overworld_dim=");
		builder.append(Purpuritis.purpuritis_overworld_dim);
		builder.append("\nbiomeConversionMap=");
		builder.append(Purpuritis.biomeConversionMap);
		builder.append("\nblockConversionMap=");
		builder.append(Purpuritis.blockConversionMap);
		builder.append("\ndimensionConversionMap=");
		builder.append(Purpuritis.dimensionConversionMap);
		builder.append("\nPurpuritis Instance=");
		builder.append(Purpuritis.INSTANCE);
		builder.append("\nRegistryEvents=");
		builder.append(Purpuritis.INSTANCE.events);
		builder.append("\ndataProvider=");
		builder.append(Purpuritis.INSTANCE.dataProvider);
		String data = builder.toString();
		directoryCache.func_208316_a(path.resolve("purpuritis.dmp"), data);
		Purpuritis.LOGGER.info("Finished gathering data");
	}

	@Override
	public String getName()
	{
		return "Purpuritis' Data Provider";
	}

	@Override
	public String toString()
	{
		return getName() + ":" + this.hashCode();
	}


}
