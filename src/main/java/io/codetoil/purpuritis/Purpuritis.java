package io.codetoil.purpuritis;

import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.mojang.logging.LogUtils;
import io.codetoil.purpuritis.core.component.PurpuritisDataComponentTypes;
import io.codetoil.purpuritis.data.PurpuritisModelProvider;
import io.codetoil.purpuritis.data.PurpuritisLootTableProvider;
import io.codetoil.purpuritis.world.item.PurpuritisItems;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Purpuritis.MOD_ID)
public class Purpuritis {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "purpuritis";

    public Purpuritis(FMLJavaModLoadingContext context) {
        LOGGER.info("Constructing Purpuritis");
        var modBusGroup = context.getModBusGroup();

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);
        InterModEnqueueEvent.getBus(modBusGroup).addListener(this::enqueueIMC);
        InterModProcessEvent.getBus(modBusGroup).addListener(this::processIMC);
        FMLClientSetupEvent.getBus(modBusGroup).addListener(this::clientSetup);
        GatherDataEvent.getBus(modBusGroup).addListener(this::gatherData);
        BuildCreativeModeTabContentsEvent.BUS.addListener(this::buildCreativeModeTabContents);
        PurpuritisDataComponentTypes.DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(modBusGroup);
        PurpuritisItems.ITEM_DEFERRED_REGISTER.register(modBusGroup);
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
        @SuppressWarnings("unused") List<Object> objects = event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList());
        // some example code to receive and process InterModComms from other mods
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        // LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    public void gatherData(final GatherDataEvent event) {
        event.getGenerator().addProvider(true, (DataProvider.Factory<PurpuritisModelProvider>)
                PurpuritisModelProvider::new);
        event.getGenerator().addProvider(true, (DataProvider.Factory<PurpuritisLootTableProvider>)
                (output) -> new PurpuritisLootTableProvider(
                        output,
                        Collections.emptySet(),
                        List.of(),
                        event.getLookupProvider()
                ));
    }

    public void buildCreativeModeTabContents(final BuildCreativeModeTabContentsEvent event) {
        Map<Map.Entry<ItemStack, CreativeModeTab.TabVisibility>, ItemStack> purpuredItemMap = Maps.newHashMap();

        event.getEntries().forEach((entry) -> {
            ItemStack purpuredItemStack = entry.getKey().copy();
            purpuredItemStack.set(PurpuritisDataComponentTypes.PURPURED.get(), Unit.INSTANCE);
            purpuredItemMap.put(entry, purpuredItemStack);
        });

        purpuredItemMap.forEach((entry, itemStack) ->
                event.getEntries().putAfter(entry.getKey(), itemStack, entry.getValue()));
    }
}
