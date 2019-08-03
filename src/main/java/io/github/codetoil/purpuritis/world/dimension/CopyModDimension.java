package io.github.codetoil.purpuritis.world.dimension;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;

import java.util.function.BiFunction;

public class CopyModDimension extends ModDimension
{
	public final Class<? extends Dimension> dim;

	public CopyModDimension(ResourceLocation loc, Class<? extends Dimension> dim)
	{
		setRegistryName(loc);
		this.dim = dim;
	}

	@Override
	public BiFunction<World, DimensionType, ? extends Dimension> getFactory()
	{
		return (world, dimensionType) -> {
			try {
				return dim.getConstructor(World.class, DimensionType.class).newInstance(world, dimensionType);
			}
			catch (Throwable t) {
				t.printStackTrace();
				throw new ReportedException(new CrashReport("Could not create instance of dimension " + dim, t));
			}
		};
	}
}
