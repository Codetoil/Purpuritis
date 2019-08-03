package io.github.codetoil.dummy;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@Mod("z_purpuritis_dummy")
public class ZDummy
{
	public static final Logger LOGGER = LogManager.getLogger();
	public ZDummy()
	{
		LOGGER.info("Constructing ZDummy");
	}
}
