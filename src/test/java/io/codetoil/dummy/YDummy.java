package io.codetoil.dummy;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@Mod("y_purpuritis_dummy")
public class YDummy
{
	public static final Logger LOGGER = LogManager.getLogger();
	public YDummy()
	{
		LOGGER.info("Constructing YDummy");
	}
}
