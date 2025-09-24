package io.codetoil.purpuritis;

import com.google.common.collect.*;
import io.codetoil.purpuritis.data.DataProviderPurpuritis;
import io.codetoil.purpuritis.world.level.block.IPurpuredBlock;
import io.codetoil.purpuritis.world.level.block.PurpuredBlock;
import io.codetoil.purpuritis.world.level.block.PurpuredBlockItem;
import io.codetoil.purpuritis.world.level.levelgen.CopyChunkGenerator;
import io.codetoil.purpuritis.world.item.*;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Purpuritis.MOD_ID)
public class Purpuritis {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "purpuritis";

    public static final BiMap<Block, IPurpuredBlock> purpuredBlocks = HashBiMap.create();
    public static final BiMap<Item, IPurpuredItem> purpuredItems = HashBiMap.create();
    public static final Collection<ToolMaterial> toolMaterials = Sets.newHashSet();
    public static final Collection<ArmorMaterial> armorMaterials = Sets.newHashSet();
    public static final BiMap<ToolMaterial, ToolMaterial> purpuredToolMaterials = HashBiMap.create();
    public static final BiMap<ArmorMaterial, ArmorMaterial> purpuredArmorMaterials = HashBiMap.create();
    public static final BiMap<ResourceKey<DimensionType>, ResourceKey<DimensionType>> purpuredDimensionTypes
            = HashBiMap.create();
    // public boolean hasSetFirstToLoad = false;
    private final Queue<ChunkPos> chunkPositionQueue = Queues.newArrayDeque();
    private boolean loadedAllChunks = false;

    public Purpuritis(FMLJavaModLoadingContext context) {
        LOGGER.info("Constructing Purpuritis");
        var modBusGroup = context.getModBusGroup();

        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);
        InterModEnqueueEvent.getBus(modBusGroup).addListener(this::enqueueIMC);
        InterModProcessEvent.getBus(modBusGroup).addListener(this::processIMC);
        FMLClientSetupEvent.getBus(modBusGroup).addListener(this::clientSetup);
        GatherDataEvent.getBus(modBusGroup).addListener(this::gatherData);
        ServerStartingEvent.BUS.addListener(this::onServerStarting);
        ServerStoppedEvent.BUS.addListener(this::onServerStopped);
        TickEvent.ServerTickEvent.Pre.BUS.addListener(this::onServerPretick);
        ChunkEvent.Load.BUS.addListener(this::onChunkLoad);
        RegisterEvent.getBus(modBusGroup).addListener(this::registerEvent);
    }

    public static void addToolMaterials(Collection<ToolMaterial> toolMaterials) {
        Purpuritis.toolMaterials.addAll(toolMaterials);
    }

    public static void addArmorMaterials(Collection<ArmorMaterial> armorMaterials) {
        Purpuritis.armorMaterials.addAll(armorMaterials);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM SETUP");
        addToolMaterials(Sets.newHashSet(ToolMaterial.WOOD, ToolMaterial.STONE,
                ToolMaterial.GOLD, ToolMaterial.IRON, ToolMaterial.DIAMOND, ToolMaterial.NETHERITE));
        addArmorMaterials(Sets.newHashSet(ArmorMaterials.LEATHER,
                ArmorMaterials.ARMADILLO_SCUTE, ArmorMaterials.TURTLE_SCUTE, ArmorMaterials.CHAINMAIL,
                ArmorMaterials.IRON, ArmorMaterials.DIAMOND, ArmorMaterials.NETHERITE));
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
        DataProvider dataProvider = new DataProviderPurpuritis(event.getGenerator());
        event.getGenerator().addProvider(true, dataProvider);
    }

    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public void onServerStopped(ServerStoppedEvent event) {
        loadedAllChunks = false;
        LOGGER.info("Server has stopped");
    }

    public void onServerPretick(TickEvent.ServerTickEvent.Pre event) {
        MinecraftServer server = event.getServer();
        Iterator<ServerLevel> levelIterator = server.getAllLevels().iterator();
        while (levelIterator.hasNext()) {
            try (ServerLevel level = levelIterator.next()) {
                if (level.getChunkSource().getGenerator() instanceof CopyChunkGenerator) {
                    // TODO Set Chunk provider, somehow... maybe set the world to something else? Like ServerWorldCopy?
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (chunkPositionQueue.isEmpty()) return;
        LOGGER.info("Going through queue of size {}", chunkPositionQueue.size());
        long startTime = System.currentTimeMillis();

        LOGGER.info("Finished going through queue");
        loadedAllChunks = true;
        Purpuritis.LOGGER.info("loadingChunks took {}ms", System.currentTimeMillis() - startTime);
    }

    public void onChunkLoad(ChunkEvent.Load event) {
        if (this.loadedAllChunks) return;
        chunkPositionQueue.add(event.getChunk().getPos());
    }

    public void registerEvent(final RegisterEvent registerEvent) {
        registerEvent.register(ForgeRegistries.Keys.BLOCKS, helper -> {
            LOGGER.info("Registering Blocks");
            ForgeRegistries.BLOCKS.forEach((block) -> {
                if (!(block instanceof IPurpuredBlock)) {
                    PurpuredBlock block1 = new PurpuredBlock(block);
                    purpuredBlocks.put(block, block1);
                }
            });
            purpuredBlocks.forEach((block, purpuredBlock) ->
            {
                assert purpuredBlock != null;
                helper.register(
                        ResourceLocation.fromNamespaceAndPath(Purpuritis.MOD_ID,
                                Objects.requireNonNull(ForgeRegistries.BLOCKS
                                        .getKey(purpuredBlock.getNormalBlock())).getNamespace()
                                        + "_" + Objects.requireNonNull(ForgeRegistries.BLOCKS
                                        .getKey(purpuredBlock.getNormalBlock())).getPath()
                        ),
                        purpuredBlock.getSelf());
            });
        });
        registerEvent.register(ForgeRegistries.Keys.ITEMS, helper -> {
            if (purpuredToolMaterials.isEmpty()) {
                for (ToolMaterial toolMaterial : toolMaterials) {
                    purpuredToolMaterials.put(toolMaterial, new ToolMaterial(toolMaterial.incorrectBlocksForDrops(),
                            toolMaterial.durability() * 2, toolMaterial.speed() * 2,
                            toolMaterial.attackDamageBonus() + 4,
                            toolMaterial.enchantmentValue() / 5, toolMaterial.repairItems()));
                }
            }
            LOGGER.info(purpuredToolMaterials);
            if (purpuredArmorMaterials.isEmpty()) {
                for (ArmorMaterial armorMaterial : armorMaterials) {
                    purpuredArmorMaterials.put(armorMaterial, new ArmorMaterial(armorMaterial.durability() * 2,
                            Maps.transformValues(armorMaterial.defense(),
                                    (value) -> {
                                        assert value != null;
                                        return value + 4;
                                    }),
                            armorMaterial.enchantmentValue() / 5, armorMaterial.equipSound(),
                            armorMaterial.toughness() + 2,
                            armorMaterial.knockbackResistance() + 2,
                            armorMaterial.repairIngredient(), armorMaterial.assetId()));
                }
            }
            LOGGER.info(purpuredArmorMaterials);
            LOGGER.info("Registering Items");
            ForgeRegistries.ITEMS.forEach((item) -> {
                LOGGER.info("purpurifying item: {}", item);
                if (!(item instanceof IPurpuredItem)) {
                    IPurpuredItem purpuredItem;
                    if (item instanceof BlockItem) {
                        purpuredItem = new PurpuredBlockItem(item,
                                Objects.requireNonNull(purpuredBlocks.get(((BlockItem) item).getBlock())),
                                new Item.Properties());
                    } else {
                        purpuredItem = new PurpuredItem(item, new Item.Properties());
                    }
                    helper.register(
                            ResourceLocation.fromNamespaceAndPath(Purpuritis.MOD_ID,
                                    Objects.requireNonNull(ForgeRegistries.ITEMS
                                            .getKey(purpuredItem.getNormalItem())).getNamespace()
                                            + "_" + Objects.requireNonNull(ForgeRegistries.ITEMS
                                            .getKey(purpuredItem.getNormalItem())).getPath()
                            ), purpuredItem.getSelf());
                }
            });
        });
    }
}
