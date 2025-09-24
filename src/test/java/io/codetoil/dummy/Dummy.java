package io.codetoil.dummy;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@Mod("purpuritis_dummy")
public class Dummy
{
	public static final Logger LOGGER = LogManager.getLogger();
	public Dummy()
	{
		LOGGER.info("Constructing Dummy");
	}
}
