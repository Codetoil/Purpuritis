package io.github.codetoil.dummy;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@Mod("x_purpuritis_dummy")
public class XDummy
{
	public static final Logger LOGGER = LogManager.getLogger();
	public XDummy()
	{
		LOGGER.info("Constructing XDummy");
	}
}
