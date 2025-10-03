package io.codetoil.purpuritis;

import com.google.common.collect.*;
import com.mojang.logging.LogUtils;
import io.codetoil.purpuritis.data.DataProviderPurpuritis;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Purpuritis.MOD_ID)
public class Purpuritis {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "purpuritis";

    public static final BiMap<ResourceKey<DimensionType>, ResourceKey<DimensionType>> purpuredDimensionTypes
            = HashBiMap.create();

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_DEFERRED_REGISTER =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public static final RegistryObject<DataComponentType<Unit>> PURPURED = DATA_COMPONENT_TYPE_DEFERRED_REGISTER
            .register("purpured", () -> DataComponentType.<Unit>builder()
                    .persistent(Unit.CODEC)
                    .networkSynchronized(Unit.STREAM_CODEC)
                    .build());

    public Purpuritis(FMLJavaModLoadingContext context) {
        LOGGER.info("Constructing Purpuritis");
        var modBusGroup = context.getModBusGroup();

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);
        InterModEnqueueEvent.getBus(modBusGroup).addListener(this::enqueueIMC);
        InterModProcessEvent.getBus(modBusGroup).addListener(this::processIMC);
        FMLClientSetupEvent.getBus(modBusGroup).addListener(this::clientSetup);
        GatherDataEvent.getBus(modBusGroup).addListener(this::gatherData);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM SETUP");
        //LOGGER.info("STONE BLOCK >> {}", Blocks.STONE.getRegistryName());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        // InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        List<Object> objects = event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList());
        // some example code to receive and process InterModComms from other mods
        // LOGGER.info("Got IMC {}", event.getIMCStream().
        // 		map(m -> m.getMessageSupplier().get()).
        // 		collect(Collectors.toList()));
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        // LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    public void gatherData(final GatherDataEvent event) {
        LOGGER.info("We are running a data version of minecraft forge, gathering data and printing");
        event.getGenerator().addProvider(true, (DataProvider.Factory<? extends DataProvider>)
                output -> new DataProviderPurpuritis(output, event.getLookupProvider()));
    }
}
